package org.softevo.mutation.bytecodeMutations.mutationCoverage;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.softevo.mutation.bytecodeMutations.MutationMarker;
import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.persistence.QueryManager;

public class CoverageData {

	private static final boolean SAVE_INTERVALLS = false;

	private static Logger logger = Logger.getLogger(CoverageData.class);

	public ThreadLocal<String> testName = new ThreadLocal<String>();

	private static class SingletonHolder {
		private final static CoverageData instance = new CoverageData();
	}

	private Map<Long, Set<String>> coverageData = new HashMap<Long, Set<String>>();

	private Set<String> testsRun = new HashSet<String>();

	private int saveCount = 1;

	private CoverageData() {
	}

	public static CoverageData getInstance() {
		return SingletonHolder.instance;
	}

	public void saveAndEmpty() {
		logger.info("Saving coverage data for " + coverageData.size()
				+ " mutations");
		QueryManager.saveCoverageResults(coverageData);
		QueryManager.saveTestsWithNoCoverage(testsRun);
//		XmlIo.toXML(coverageData, "coverageData-" + saveCount + ".xml");
		saveCount++;
		coverageData = new HashMap<Long, Set<String>>();
	}

	static int call = 0;

	private static boolean shouldSave;

	public static void touch(long id) {
		call++;
		if (call % ((int) 1e6) == 0) {
			logger.info("Touch called " + call + "times.  Test " + SingletonHolder.instance.testName.get()
					+ " touched mutation " + id);
			shouldSave = true;
		}
		// Only for debugging puprosses. Impacts performance
		// logger.info("Test " + SingletonHolder.instance.testName.get()
		// + " touched mutation " + id);

		Set<String> coveredTests = SingletonHolder.instance.coverageData
				.get(id);
		if (coveredTests == null) {
			coveredTests = new HashSet<String>();
			SingletonHolder.instance.coverageData.put(id, coveredTests);
		}
		coveredTests.add(SingletonHolder.instance.testName.get());
	}

	public static void setTestName(String testName) {
		logger.info("Setting testname " + testName);

		CoverageData instance = SingletonHolder.instance;
		instance.testsRun.add(testName);

		if (instance.testName.get() == null) {
			instance.testName.set(testName);
		} else {
			logger.info("Trying to overwrite testname");
			logger.info("Old testname: " + instance.testName.get());
			logger.info("New testname: " + testName);
			Thread currentThread = Thread.currentThread();
			StackTraceElement[] sts = currentThread.getStackTrace();
			String stackTraceString = Arrays.toString(sts);
			logger.info("Stacktrace:\n" + stackTraceString);
		}
	}

	public static void unsetTestName(String testName) {
		logger.info("Unsetting testname " + testName);
		CoverageData instance = SingletonHolder.instance;
		String oldTestName = instance.testName.get();
		if (oldTestName == null) {
			logger.warn("Test name was  set to null expected " + testName);
		} else if (oldTestName.equals(testName)) {
			instance.testName.set(null);
		} else {
			logger.warn("Unset testname got different names");
			logger.warn("Tried to unset: " + testName);
			logger.warn("but got currently acctive test name: " + oldTestName);
			Thread currentThread = Thread.currentThread();
			StackTraceElement[] sts = currentThread.getStackTrace();
			String stackTraceString = Arrays.toString(sts);
			logger.warn("Stacktrace:\n" + stackTraceString);
		}

	}

	public static void main(String[] args) {
		touch(23454904540l);
	}

	public static void insertCoverageCalls(MethodVisitor mv, Mutation mutation) {
		Label endLabel = new Label();
		endLabel.info = new MutationMarker(false);
		Label mutationStartLabel = new Label();
		mutationStartLabel.info = new MutationMarker(true);
		mv.visitLabel(mutationStartLabel);
		Long id = mutation.getId();
		if (id == null) {
			Mutation mutationFromDb = QueryManager.getMutationOrNull(mutation);
			if (mutationFromDb != null) {
				id = mutationFromDb.getId();
			} else {
				QueryManager.saveMutation(mutation);
				id = mutation.getId();
			}
		}
		logger.debug("Inserting Coverage calls for:  " + id + " " + mutation);
		mv.visitLdcInsn(id);
		mv
				.visitMethodInsn(
						INVOKESTATIC,
						"org/softevo/mutation/bytecodeMutations/mutationCoverage/CoverageData",
						"touch", "(J)V");
		mv.visitLabel(endLabel);
	}

	public static void optionalSave() {
		if (SAVE_INTERVALLS && shouldSave) {
			CoverageData instance = SingletonHolder.instance;
			instance.saveAndEmpty();
			shouldSave = false;
		}
	}

	public static void endCoverage() {
		CoverageData instance = SingletonHolder.instance;
		instance.saveAndEmpty();
	}

	/**
	 * @return the testsRun
	 */
	public Set<String> getTestsRun() {
		return testsRun;
	}
}