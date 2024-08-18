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

package org.eclipse.cdt.lsp.clangd;

/**
 * @since 2.1
 */
public interface ClangFormatFileOptions {

	/**
	 * Generates a default .clang-format file in the project root if not yet existing
	 *
	 * @return if format file should be generated automatically
	 */
	boolean generateDefaultFormatFile();

}
