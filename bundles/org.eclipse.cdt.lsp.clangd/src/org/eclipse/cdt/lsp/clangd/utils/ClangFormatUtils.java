/*******************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   See git history
 *******************************************************************************/

package org.eclipse.cdt.lsp.clangd.utils;

import java.io.IOException;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;

public final class ClangFormatUtils {
	public static final String format_file = ".clang-format"; //$NON-NLS-1$

	/**
	 * Checks if the formatFile exists. Creates it if not.
	 * @param formatFile
	 */
	public Optional<IFile> getClangFormatFile(IProject project) {
		var formatFile = project.getFile(ClangFormatUtils.format_file);
		if (!formatFile.exists()) {
			try {
				createFileFromResource(formatFile);
			} catch (CoreException | IOException e) {
				Platform.getLog(getClass()).error(e.getMessage(), e);
				return Optional.empty();
			}
		}
		return Optional.of(formatFile);
	}

	public void createClangFormatFile(IProject project) {
		var formatFile = project.getFile(ClangFormatUtils.format_file);
		if (!formatFile.exists()) {
			try {
				createFileFromResource(formatFile);
			} catch (CoreException | IOException e) {
				Platform.getLog(getClass()).error(e.getMessage(), e);
			}
		}
	}

	private void createFileFromResource(IFile formatFile) throws CoreException, IOException {
		try (final var source = getClass().getResourceAsStream(".clang-format-project");) { //$NON-NLS-1$
			formatFile.create(source, true, new NullProgressMonitor());
		}
	}

}
