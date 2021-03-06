/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.php.internal.ui.editor.templates;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.php.internal.ui.PHPUiPlugin;

/**
 * Provides access to the PHP template store.
 */
public class PHPTemplateAccess extends ScriptTemplateAccess {
	// Template
	private static final String CUSTOM_TEMPLATES_KEY = "org.eclipse.php.Templates"; //$NON-NLS-1$

	private static PHPTemplateAccess instance;

	public static PHPTemplateAccess getInstance() {
		if (instance == null) {
			instance = new PHPTemplateAccess();
		}

		return instance;
	}

	/*
	 * @see org.eclipse.dltk.ui.templates.ScriptTemplateAccess#getPreferenceStore()
	 */
	@Override
	protected IPreferenceStore getPreferenceStore() {
		return PHPUiPlugin.getDefault().getPreferenceStore();
	}

	/*
	 * @see org.eclipse.dltk.ui.templates.ScriptTemplateAccess#getContextTypeId()
	 */
	@Override
	protected String getContextTypeId() {
		return PHPTemplateContextType.PHP_CONTEXT_TYPE_ID;
	}

	/*
	 * @see org.eclipse.dltk.ui.templates.ScriptTemplateAccess#getCustomTemplatesKey
	 * ()
	 */
	@Override
	protected String getCustomTemplatesKey() {
		return CUSTOM_TEMPLATES_KEY;
	}
}
