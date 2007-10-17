package org.softevo.mutation.run;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.testsuite.RunResult;

/**
 *
 * This class wraps around a process that is started via {@link Runtime}.exec().
 *
 * @author David Schuler
 *
 */
public class ProcessWrapper extends Thread {

	private static Logger logger = Logger.getLogger(ProcessWrapper.class);

	private String command;

	private String[] args;

	private File dir;

	private Process process;

	private boolean running;

	private boolean finished;

	private int exitvalue;

	private final File outputFile;

	private final File resultFile;

	public long startTime;

	private PipeThread errorPipe;

	private PipeThread outputPipe;

	public ProcessWrapper(String command, String[] args, File dir,
			File outputFile, File resultFile) {
		super();
		this.command = command;
		this.args = args;
		this.dir = dir;
		this.outputFile = outputFile;
		this.resultFile = resultFile;
		System.out.println("Process created" + this);
	}

	public void run() {
		System.out.println("Process started" + this);
		try {
			String[] cmdArray = getCommand();
			process = Runtime.getRuntime().exec(cmdArray, new String[0], dir);
			running = true;
			startTime = System.currentTimeMillis();
			InputStream is = process.getInputStream();
			FileOutputStream fw = new FileOutputStream(outputFile);
			outputPipe = new PipeThread(is, fw);
			// logger.info(is + " - " + process.getErrorStream());
			// errorPipe = new PipeThread(process.getErrorStream());
			outputPipe.start();
			// errorPipe.start();
			process.waitFor();
			closePipes();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		exitvalue = process.exitValue();
		end();
	}

	private void end() {
		running = false;
		finished = true;
	}

	private String[] getCommand() {
		String[] cmdArray = new String[args.length + 2];
		cmdArray[0] = command;
		cmdArray[1] = "-D" + MutationProperties.RESULT_FILE_KEY + "="
				+ resultFile.getAbsolutePath();
		System.arraycopy(args, 0, cmdArray, 2, args.length);
		return cmdArray;
	}

	private void closePipes() {
		if (outputPipe != null) {
			outputPipe.setRunning(false);
			try {
				outputPipe.join(100);
				// outputPipe.interrupt();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		if (errorPipe != null) {
			errorPipe.setRunning(false);
			// try {
			errorPipe.interrupt();
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }

		}

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Command" + command);
		sb.append('\n');
		sb.append("Arguments: ");
		sb.append(Arrays.toString(args));
		sb.append("Output File" + outputFile.getAbsolutePath());
		sb.append('\n');

		return sb.toString();
	}

	/**
	 * @return the exit value
	 */
	public int getExitvalue() {
		return exitvalue;
	}

	/**
	 * @return the isRunning
	 */
	public boolean isRunning() {
		return running;
	}

	public static void main(String[] args) {
		String cmd = "/scratch/schuler/mutationTest/src/scripts/run-tests.sh";
		// String cmd = "java";

		ProcessWrapper ps = new ProcessWrapper(cmd, new String[] {}, new File(
				"/scratch/schuler/mutationTest/src/scripts/"), new File(
				"processoutput.txt"), new File("res.xml"));
		Thread t = new Thread(ps);
		t.start();
	}

	public RunResult getRunResult() {
		if (finished) {
			if (resultFile.exists()) {
				RunResult rr = (RunResult) XmlIo.fromXml(resultFile);
				return rr;
			} else {
				logger.info("File " + resultFile + " does not exist");
			}
		}
		return null;

	}

	/**
	 * Return the time this process is running in milliseconds.
	 *
	 * @return The time this process is running in milliseconds.
	 */
	public long getTimeRunnning() {
		if (startTime > 0l) {
			return System.currentTimeMillis() - startTime;
		}
		return 0l;
	}

	/**
	 * @return the finished
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * Destroys the underlying process.
	 */
	public void destroyProcess() {
		logger.info("Destroying Process" + this);
		closePipes();
		logger.info("Pipes closed");
		end();
		logger.info("Trying to destroy sub process");
		process.destroy();
		logger.info("Sub process destroyed");
		interrupt();
	}
}