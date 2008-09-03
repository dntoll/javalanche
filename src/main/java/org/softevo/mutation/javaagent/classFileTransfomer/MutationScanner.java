package org.softevo.mutation.javaagent.classFileTransfomer;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;
import org.softevo.mutation.bytecodeMutations.MutationScannerTransformer;
import org.softevo.mutation.bytecodeMutations.integrateSuite.IntegrateSuiteTransformer;
import org.softevo.mutation.javaagent.MutationPreMain;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.Mutation.MutationType;
import org.softevo.mutation.results.persistence.QueryManager;

import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class MutationScanner implements ClassFileTransformer {

	private static Logger logger = Logger.getLogger(MutationScanner.class);

	private MutationPossibilityCollector mpc = new MutationPossibilityCollector();

	private MutationScannerTransformer mutationScannerTransformer = new MutationScannerTransformer(
			mpc);

	private MutationDecision md = new MutationDecision() {

		private String prefix = System
				.getProperty(MutationProperties.PROJECT_PREFIX_KEY);

		public boolean shouldBeHandled(String classNameWithDots) {
			if (classNameWithDots.startsWith("java")
					|| classNameWithDots.startsWith("sun")
					|| classNameWithDots.startsWith("org.aspectj.org.eclipse")) {
				return false;
			}
			if (classNameWithDots.toLowerCase().contains("test")) {
				return false;
			}
			if (prefix != null && classNameWithDots.startsWith(prefix)) {
				if (QueryManager.hasMutationsforClass(classNameWithDots)) {
					return false;
				}
				return true;
			}
			return false;
		}
	};
	static {
		// DB must be loaded before transform method is entered. Otherwise
		// program crashes.
		Mutation someMutation = new Mutation("SomeMutationToAddToTheDb", 23,
				23, MutationType.ARITHMETIC_REPLACE, false);
		Mutation mutationFromDb = QueryManager.getMutationOrNull(someMutation);
		if (mutationFromDb == null) {
			MutationPossibilityCollector mpc1 = new MutationPossibilityCollector();
			mpc1.addPossibility(someMutation);
			mpc1.toDB();
		}
		MutationProperties.checkProperty(MutationProperties.TEST_SUITE_KEY);
		logger.info("Name of test suite: " + MutationProperties.TEST_SUITE);
	}

	public MutationScanner() {
		addShutDownHook();
	}

	private void addShutDownHook() {
		Runtime runtime = Runtime.getRuntime();
		final long mutationPossibilitiesPre = QueryManager
				.getNumberOfMutationsWithPrefix(MutationProperties.PROJECT_PREFIX);

		runtime.addShutdownHook(new Thread() {
			@Override
			public void run() {
				String message1 = String.format(
						"Got %d mutation possibilities before run",
						mutationPossibilitiesPre);
				final long mutationPossibilitiesPost = QueryManager
						.getNumberOfMutationsWithPrefix(MutationProperties.PROJECT_PREFIX);
				String message2 = String.format(
						"Got %d mutation possibilities after run",
						mutationPossibilitiesPost);
				String message3 = String.format(
						"Added %d mutation possibilities.",
						mutationPossibilitiesPost - mutationPossibilitiesPre);
				logger.info(message1);
				logger.info(message2);
				logger.info(message3);
			}
		});
	}

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		if (className != null) {
			try {

				String classNameWithDots = className.replace('/', '.');
				logger.info(classNameWithDots);

				if (md.shouldBeHandled(classNameWithDots)) {
//					TraceClassVisitor tr = new TraceClassVisitor(new PrintWriter(MutationPreMain.sysout));
//					ClassReader cr = new ClassReader(classfileBuffer);
//					cr.accept(tr,0);
//					return classfileBuffer;
					classfileBuffer = mutationScannerTransformer
							.transformBytecode(classfileBuffer);
					logger.info("Possibilities found for class " + className
							+ " " + mpc.size());
					mpc.updateDB();
					mpc.clear();
				} else {
					logger.info("Skipping class " + className);
				}
				if (classNameWithDots.endsWith("AllTests")
						|| compareWithSuiteProperty(classNameWithDots)) {
					logger.info("Trying to integrate ScanAndCoverageTestSuite");
					BytecodeTransformer integrateSuiteTransformer = IntegrateSuiteTransformer
							.getIntegrateScanAndCoverageTestSuiteTransformer();
					classfileBuffer = integrateSuiteTransformer
							.transformBytecode(classfileBuffer);
				}

			} catch (Throwable t) {
				t.printStackTrace();
				logger.info(t.getMessage());
				logger.info(t.getStackTrace());
				System.out.println("Exception during instrumentation - exiting");
				System.exit(1);
			}
		}
		return classfileBuffer;
	}

	public static boolean compareWithSuiteProperty(String classNameWithDots) {
		boolean returnValue = false;
		String testSuiteName = 	MutationProperties.TEST_SUITE;
		if (testSuiteName != null && classNameWithDots.contains(testSuiteName)) {
			returnValue = true;
		}
		return returnValue;
	}

	public static void main(String[] args) {
		new MutationScanner();
	}
}