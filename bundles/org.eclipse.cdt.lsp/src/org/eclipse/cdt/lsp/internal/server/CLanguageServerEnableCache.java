/*******************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   See git history
 *******************************************************************************/

package org.eclipse.cdt.lsp.internal.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.cdt.internal.core.LRUCache;
import org.eclipse.cdt.lsp.util.LspUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentTypeManager.ContentTypeChangeEvent;
import org.eclipse.core.runtime.content.IContentTypeManager.IContentTypeChangeListener;
import org.eclipse.lsp4e.LSPEclipseUtils;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.genericeditor.ExtensionBasedTextEditor;

/**
 * Caches the Language Server enable for a given resource URI. Used by {@link HasLanguageServerPropertyTester#test(Object, String, Object[], Object)}
 * The cache is getting cleared on changes in the C/C++ related content types. The cache gets restored for all opened files in the LSP based editor
 * if their content type still matches a C/C++ source or header type.
 *
 * The cache is limited to 100 elements. The oldest entry will be removed.
 * A resource URI shall be removed from the cache if it's getting closed in the editor.
 * The enable Language Server is cached when the file has been opened in the LSP based C/C++ editor and the opened file matches a C/C++ content type.
 */
public final class CLanguageServerEnableCache implements IContentTypeChangeListener, IPartListener, IWindowListener {

	private final class Data {
		boolean enable = false;
		List<Integer> editorHashes = new ArrayList<>();

		private Data(boolean enable) {
			this.enable = enable;
		}

		private Data(boolean enable, int hash) {
			this.enable = enable;
			addEditor(hash);
		}

		private void addEditor(Integer hash) {
			var h = hash;
			if (h != 0) {
				editorHashes.add(h);
			}
		}

		private void removeEditor(Integer hash) {
			var h = hash;
			if (editorHashes.contains(h)) {
				editorHashes.remove(h);
			}
		}

		private boolean allEditorsClosed() {
			return editorHashes.isEmpty();
		}
	}

	private static final int BUFFER_SIZE = 100;
	private static final String C_SOURCE = "org.eclipse.cdt.core.cSource"; //$NON-NLS-1$
	private static final String CXX_SOURCE = "org.eclipse.cdt.core.cxxSource"; //$NON-NLS-1$
	private static final String C_HEADER = "org.eclipse.cdt.core.cHeader"; //$NON-NLS-1$
	private static final String CXX_HEADER = "org.eclipse.cdt.core.cxxHeader"; //$NON-NLS-1$
	private static final Map<URI, Data> cache = Collections.synchronizedMap(new LRUCache<>(BUFFER_SIZE));
	private static CLanguageServerEnableCache instance = null;

	private CLanguageServerEnableCache() {
		Platform.getContentTypeManager().addContentTypeChangeListener(this);
		if (PlatformUI.isWorkbenchRunning()) {
			var workbench = PlatformUI.getWorkbench();
			workbench.addWindowListener(this);
			Arrays.stream(workbench.getWorkbenchWindows()).map(IWorkbenchWindow::getPages).flatMap(Arrays::stream)
					.forEach(p -> p.addPartListener(this));
		}
	}

	private static void clearAll() {
		cache.clear();
	}

	public static void stop() {
		if (instance != null) {
			Platform.getContentTypeManager().removeContentTypeChangeListener(instance);
			var workbench = PlatformUI.getWorkbench();
			workbench.removeWindowListener(instance);
			Arrays.stream(workbench.getWorkbenchWindows()).map(IWorkbenchWindow::getPages).flatMap(Arrays::stream)
					.forEach(p -> p.removePartListener(instance));
		}
		clearAll();
	}

	public static synchronized CLanguageServerEnableCache getInstance() {
		if (instance == null) {
			instance = new CLanguageServerEnableCache();
		}
		return instance;
	}

	public Optional<Boolean> get(URI uri) {
		return Optional.ofNullable(cache.get(uri)).map(data -> data.enable);
	}

	public void disable(URI uri) {
		cache.put(uri, new Data(false));
	}

	@Override
	public void contentTypeChanged(ContentTypeChangeEvent event) {
		var id = event.getContentType().getId();
		if (C_SOURCE.contentEquals(id) || CXX_SOURCE.contentEquals(id) || C_HEADER.contentEquals(id)
				|| CXX_HEADER.contentEquals(id)) {
			clearAll();
			// add all opened files again if content type is still a C/C++ source or header:
			LspUtils.getFilesInLspBasedEditor().forEach((hash, uri) -> {
				var data = cache.get(uri);
				if (data != null) {
					data.addEditor(hash);
				} else {
					cache.put(uri, new Data(true, hash));
				}
			});
		}
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		// do nothing
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		// do nothing
	}

	// remove cache only if the URI is not opened in any other LSP based editor.
	@Override
	public void partClosed(IWorkbenchPart part) {
		if (part instanceof ExtensionBasedTextEditor editor && LspUtils.checkForCContentType(editor.getEditorInput())) {
			Optional.ofNullable(LSPEclipseUtils.toUri(editor.getEditorInput())).ifPresent(uri -> {
				var data = cache.get(uri);
				if (data != null) {
					data.removeEditor(part.hashCode());
					if (data.allEditorsClosed()) {
						cache.remove(uri);
					}
				}
			});
		}
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		// do nothing
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		if (part instanceof ExtensionBasedTextEditor editor && LspUtils.checkForCContentType(editor.getEditorInput())) {
			Optional.ofNullable(LSPEclipseUtils.toUri(editor.getEditorInput())).ifPresent(uri -> {
				var hash = part.hashCode();
				var data = cache.get(uri);
				if (data != null) {
					data.enable = true;
					data.addEditor(hash);
				} else {
					cache.put(uri, new Data(true, hash));
				}
			});
		}
	}

	@Override
	public void windowActivated(IWorkbenchWindow window) {
		// do nothing
	}

	@Override
	public void windowDeactivated(IWorkbenchWindow window) {
		// do nothing
	}

	@Override
	public void windowClosed(IWorkbenchWindow window) {
		Arrays.stream(window.getPages()).forEach(p -> p.removePartListener(this));
	}

	@Override
	public void windowOpened(IWorkbenchWindow window) {
		Arrays.stream(window.getPages()).forEach(p -> p.addPartListener(this));
	}
}
