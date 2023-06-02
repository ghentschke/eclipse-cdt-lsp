/*******************************************************************************
 * Copyright (c) 2023 Bachmann electronic GmbH and others.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Gesa Hentschke (Bachmann electronic GmbH) - initial implementation
 *******************************************************************************/

package org.eclipse.cdt.lsp.test.server;

import java.util.List;

import org.eclipse.cdt.lsp.server.EnableExpression;
import org.eclipse.cdt.lsp.server.ICLanguageServerProvider;
import org.eclipse.core.resources.IProject;

public class MockCLanguageServerProvider implements ICLanguageServerProvider {

	@Override
	public List<String> getCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCommands(List<String> commands) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEnableExpression(EnableExpression enableExpression) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isEnabledFor(IProject project) {
		// TODO Auto-generated method stub
		return false;
	}

}
