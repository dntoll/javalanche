package de.unisb.cs.st.javalanche.mutation.properties;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.util.MutationUtil;

public class MutationProperties {

	private static Logger logger = Logger.getLogger(MutationProperties.class);

	public static final String PROPERTIES_FILE = "mutation.incl.properties";

	public static final Properties PROPERTIES = getProperties();

	static {
		logger.info("Loaded log4j configuration from "
				+ MutationUtil.getLog4jPropertiesLocation());
	}

	private static Properties getProperties() {
		Properties properties = new Properties();
		InputStream is = MutationProperties.class.getClassLoader()
				.getResourceAsStream(PROPERTIES_FILE);
		if (is != null) {
			try {
				properties.load(is);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (properties != null) {
				logger.debug("Got following properties from file. File: "
						+ PROPERTIES_FILE + " Properties: "
						+ properties.keySet());
			}
			if (properties == null) {
				logger.warn("Could not read properties file:  "
						+ PROPERTIES_FILE);
			}
		}
		return properties;
	}

	public static final String OUTPUT_DIR = getPropertyOrDefault(
			"javalanche.mutation.output.dir", "mutation-files");

	/*
	 * Different run modes.
	 */

	public enum RunMode {
		SCAN("scan"), MUTATION_TEST("mutation"), CHECK_TESTS("test1"), TEST_TESTSUITE_SECOND(
				"test2"), MUTATION_TEST_INVARIANT("mutation-invariant"), MUTATION_TEST_INVARIANT_PER_TEST(
				"mutation-invariant-per-test"), MUTATION_TEST_COVERAGE(
				"mutation-coverage"), CREATE_COVERAGE("create-coverage"), OFF(
				"off"), CHECK_INVARIANTS_PER_TEST("check-per-test"), TEST_PERMUTED(
				"test3"), SCAN_PROJECT("scan-project"), SCAN_ECLIPSE(
				"scan-eclipse");

		private String key;

		RunMode(String key) {
			this.key = key;
		}

		/**
		 * @return the key
		 */
		public String getKey() {
			return key;
		}

	}

	private static final String RUN_MODE_KEY = "mutation.run.mode";

	public static final RunMode RUN_MODE = getRunMode();

	public static final String RESULT_FILE_KEY = "mutation.result.file";

	public static final String RESULT_FILE = getProperty(RESULT_FILE_KEY);

	public static final String MUTATION_FILE_KEY = "mutation.file";

	public static String MUTATION_FILE_NAME = getProperty(MUTATION_FILE_KEY);

	public static String IGNORE_MESSAGES_KEY = "javalanche.ignore.test.messages";

	public static boolean IGNORE_MESSAGES = getPropertyOrDefault(
			IGNORE_MESSAGES_KEY, false);

	public static String INSERT_ORIGINAL_INSTEAD_OF_MUTATION_KEY = "javalanche.debug.insert.original.code";

	public static boolean INSERT_ORIGINAL_INSTEAD_OF_MUTATION = getPropertyOrDefault(
			INSERT_ORIGINAL_INSTEAD_OF_MUTATION_KEY, false);

	public static final String MUTATION_TEST_DEBUG_KEY = "mutation.test.debug";

	public static final boolean DEBUG = true;

	public static final String DEBUG_PORT_KEY = "mutation.debug.port";

	// private static boolean getDebug() {
	// String debugProperty = System.getProperty(MUTATION_TEST_DEBUG_KEY);
	// if (debugProperty != null && !debugProperty.equals("false")) {
	// logger.info("Debugging enabled");
	// return true;
	// }
	// logger.info("Debugging not enabled");
	// return false;
	// }

	public static final String RESULT_DIR = OUTPUT_DIR;

	/**
	 * Directory the serialized files are stored.
	 */
	public static final String RESULT_OBJECTS_DIR = OUTPUT_DIR + "objects/";

	public static final String MUTATIONS_CLASS_RESULT_XML = "mutations-class-result.xml";

	/**
	 * 
	 * The key for the system property that specifies the package prefix of the
	 * project to mutate.
	 * 
	 * -dmutation.package.prefix=org.aspectj
	 */
	public static final String PROJECT_PREFIX_KEY = "mutation.package.prefix";

	public static String PROJECT_PREFIX = getPrefix();

	/**
	 * 
	 * The key for the system property that specifies the the testsuite which
	 * should be modified
	 * 
	 * -dmutation.test.suite=AllTests
	 */
	public static final String TEST_SUITE_KEY = "mutation.test.suite";

	public static final String TEST_SUITE = getProperty(TEST_SUITE_KEY);

	/**
	 * The key for the system property that specifies if there is coverage
	 * information in the db.
	 * 
	 * -dmutation.coverage.information=false
	 * 
	 */
	public static final String COVERAGE_INFORMATION_KEY = "mutation.coverage.information";

	/**
	 * True if coverage information is available in the db.
	 */
	public static final boolean COVERAGE_INFORMATION = getPropertyOrDefault(
			COVERAGE_INFORMATION_KEY, true);

	/**
	 * Directory where the processes are executed
	 */
	public static final String EXEC_DIR = ".";

	public static final String TESTCASES_FILE = OUTPUT_DIR + "/testCases.xml";

	public static final String ACTUAL_MUTATION_KEY = "mutation.actual.mutation";

	public static final String NOT_MUTATED = "notMutated";

	public static final String TEST_TESTSUITE_KEY = "mutation.test.testsuite";

	public static final String TEST_TESTSUITE = getProperty(TEST_TESTSUITE_KEY);

	public static final String TRACE_BYTECODE_KEY = "mutation.trace";

	public static final boolean TRACE_BYTECODE = getPropertyOrDefault(
			TRACE_BYTECODE_KEY, false);

	public static final String TEST_FILTER_FILE_NAME_KEY = "mutation.test.filter.map";
	public static final String TEST_FILTER_FILE_NAME = getProperty(TEST_FILTER_FILE_NAME_KEY);

	public static final boolean SHOULD_FILTER_TESTS = shoudFilterTests();

	public static final String EXPERIMENT_DATA_FILENAME_KEY = "experiment.data.filename";

	public static final String EXPERIMENT_DATA_FILENAME = getProperty(EXPERIMENT_DATA_FILENAME_KEY);

	public static final String MULTIPLE_MAKEFILES_KEY = "mutation.multiple.makefile";

	public static final boolean MULTIPLE_MAKEFILES = getPropertyOrDefault(
			MULTIPLE_MAKEFILES_KEY, false);

	public static final String STOP_AFTER_FIRST_FAIL_KEY = "mutation.stop.after.first.fail";

	public static final boolean STOP_AFTER_FIRST_FAIL = getPropertyOrDefault(
			STOP_AFTER_FIRST_FAIL_KEY, true);

	private static final String DEFAULT_TIMEOUT_IN_SECONDS_KEY = "mutation.default.timeout";

	public static final int DEFAULT_TIMEOUT_IN_SECONDS = getPropertyOrDefault(
			DEFAULT_TIMEOUT_IN_SECONDS_KEY, 10);

	/**
	 * The save interval in which the mutation results are written to the
	 * database.
	 */
	private static final String SAVE_INTERVAL_KEY = "mutation.save.interval";
	public static final int SAVE_INTERVAL = getPropertyOrDefault(
			SAVE_INTERVAL_KEY, 50);

	public static final String IGNORE_RIC_KEY = "javalanche.ignore.ric";
	public static final boolean IGNORE_RIC = getPropertyOrDefault(
			IGNORE_RIC_KEY, false);

	public static final String IGNORE_NEGATE_JUMPS_KEY = "javalanche.ignore.jumps";
	public static final boolean IGNORE_NEGATE_JUMPS = getPropertyOrDefault(
			IGNORE_NEGATE_JUMPS_KEY, false);

	public static final String IGNORE_ARITHMETIC_REPLACE_KEY = "javalanche.ignore.replace";
	public static final boolean IGNORE_ARITHMETIC_REPLACE = getPropertyOrDefault(
			IGNORE_ARITHMETIC_REPLACE_KEY, false);

	public static final String IGNORE_REMOVE_CALLS_KEY = "javalanche.ignore.remove.calls";
	public static final boolean IGNORE_REMOVE_CALLS = getPropertyOrDefault(
			IGNORE_REMOVE_CALLS_KEY, false);

	public static final int getPropertyOrDefault(String key, int defaultValue) {
		String result = getPropertyOrDefault(key, defaultValue + "");
		return Integer.parseInt(result);
	}

	private static boolean shoudFilterTests() {
		if (MutationProperties.TEST_FILTER_FILE_NAME != null) {
			File filterMapFile = new File(
					MutationProperties.TEST_FILTER_FILE_NAME);
			if (filterMapFile.exists()) {
				logger.info("Applying filters for test cases");
				return true;
			}
		}
		return false;
	}

	private static boolean getPropertyOrDefault(String key, boolean b) {
		String property = getProperty(key);
		if (property == null) {
			return b;
		} else {
			String propertyTrimmed = property.trim().toLowerCase();
			if (propertyTrimmed.equals("true") || propertyTrimmed.equals("yes")) {
				return true;
			}
		}
		return false;
	}

	private static RunMode getRunMode() {
		String runModeString = getProperty(RUN_MODE_KEY);
		if (runModeString != null) {
			runModeString = runModeString.toLowerCase();
			for (RunMode runMode : RunMode.values()) {
				if (runMode.getKey().equals(runModeString)) {
					return runMode;
				}
			}
		}
		return RunMode.OFF;
	}

	private static final String getPropertyOrDefault(String key,
			String defaultValue) {
		String result = getProperty(key);
		if (result == null) {
			result = defaultValue;
		}
		return result;
	}

	private static String getProperty(String key) {
		String result = null;
		if (System.getProperty(key) != null) {
			result = System.getProperty(key);
		}
		// no else if - property may also be null
		if (result == null && PROPERTIES.containsKey(key)) {
			result = PROPERTIES.getProperty(key);
		}
		logger.info(String.format("Got property: key=%s  ,  value=%s", key,
				result));
		return result;
	}

	private static String getPrefix() {
		String project_prefix = getProperty(PROJECT_PREFIX_KEY);
		if (project_prefix == null || project_prefix.length() == 0) {
			logger.warn("No project prefix found (Property: "
					+ PROJECT_PREFIX_KEY + " not set)");
		}
		return project_prefix;
	}

	public static void checkProperty(String key) {
		String property = System.getProperty(key);
		if (property == null) {
			throw new IllegalStateException("Property not specified. Key: "
					+ key);
		}
	}

	public static final File TEST_MAP_FILE = new File(OUTPUT_DIR,
			"testname-map.xml");

	public static final File EXCLUDE_FILE = new File(OUTPUT_DIR, "exclude.txt");

	public static final int BATCH_SIZE = 1;

	private static final String SINGLE_TASK_MODE_KEY = "single.task.mode";

	public static final boolean SINGLE_TASK_MODE = getPropertyOrDefault(
			SINGLE_TASK_MODE_KEY, false);

	public static final String CLASSES_TO_MUTATE_KEY = "javalanche.classes.to.mutate";

	public static final File TEST_EXCLUDE_FILE = new File(OUTPUT_DIR,
			"test-exclude.txt"); // TODO other dir

}
