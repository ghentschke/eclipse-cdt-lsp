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

import org.eclipse.core.runtime.preferences.PreferenceMetadata;

/**
 * @since 2.1
 */
public interface ClangFormatFileMetadata {

	/**
	 * Returns the metadata for the "Generate format file automatically" option, must not return <code>null</code>.
	 *
	 * @return the metadata for the "Generate format file automatically" option
	 *
	 * @see ClangFormatFileOptions#generateFormatFile()
	 *
	 * @since 2.1
	 */
	PreferenceMetadata<Boolean> generateDefaultFormatFile();

}
