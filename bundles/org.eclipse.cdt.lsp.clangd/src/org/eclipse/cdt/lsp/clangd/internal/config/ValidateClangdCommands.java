package org.eclipse.cdt.lsp.clangd.internal.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.Platform;

public class ValidateClangdCommands {

	public static boolean validCommands(List<String> commandLine) {
		// Startup the command
		var result = new AtomicBoolean(true);
		commandLine.add("--check=C:\\temp\\dummy.c"); //$NON-NLS-1$
		ProcessBuilder processBuilder = new ProcessBuilder(commandLine);
		Process process;
		try {
			process = processBuilder.start();
		} catch (IOException e) {
			Platform.getLog(ValidateClangdCommands.class).error(e.getMessage(), e);
			result.set(false);
			return result.get();
		}
		runValidation(result, process);
		return result.get();
	}

	private static void runValidation(final AtomicBoolean result, final Process process) {
		Thread clangdStderrReaderThread = new Thread("Clangd Commands Validator") { //$NON-NLS-1$
			@Override
			public void run() {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
					for (String line = reader.readLine(); line != null; line = reader.readLine()) {
						if (errorInLine(line)) {
							Platform.getLog(getClass()).error(line);
							result.set(false);
						}
					}
				} catch (IOException e) {
					Platform.getLog(ValidateClangdCommands.class).error(e.getMessage(), e);
				}
			}
		};
		clangdStderrReaderThread.start();
		try {
			clangdStderrReaderThread.join();
			process.waitFor();
		} catch (InterruptedException e) {
			Platform.getLog(ValidateClangdCommands.class).error(e.getMessage(), e);
			result.set(false);
		}
	}

	private static boolean errorInLine(String line) {
		return line.contains("clangd.exe:"); //$NON-NLS-1$
	}

}
