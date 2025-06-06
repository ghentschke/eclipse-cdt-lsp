/*******************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   See git history
 *******************************************************************************/

package org.eclipse.cdt.lsp.editor;

public interface EditorOptions {

	/**
	 * Prefer to use LSP based C/C++ Editor
	 *
	 * @return if LSP based C/C++ Editor should be preferred
	 */
	boolean preferLspEditor();

	/**
	 * Show the try new experience banner in the editor
	 *
	 * @return if banner should be displayed
	 * @since 3.0
	 */
	boolean showTryLspBanner();

	/**
	 * Format source code on file save action
	 *
	 * @return if source code should be formatted on file save action
	 */
	boolean formatOnSave();

	/**
	 * Format all lines in source file
	 *
	 * @return if all lines should be formatted
	 */
	boolean formatAllLines();

	/**
	 * Format edited lines only
	 *
	 * @return if only edited lines should be formatted
	 */
	boolean formatEditedLines();

}
