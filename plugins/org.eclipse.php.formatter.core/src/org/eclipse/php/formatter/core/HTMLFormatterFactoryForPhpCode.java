/*******************************************************************************
 * Copyright (c) 2013 Zend Techologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.eclipse.php.formatter.core;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.format.HTMLFormatter;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatPreferences;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatter;
import org.eclipse.wst.xml.core.internal.provisional.format.StructuredFormatPreferencesXML;
import org.w3c.dom.Node;

class HTMLFormatterFactoryForPhpCode {
	private static HTMLFormatterFactoryForPhpCode fInstance = null;
	protected StructuredFormatPreferencesXML fFormatPreferences = null;

	static synchronized HTMLFormatterFactoryForPhpCode getInstance() {
		if (fInstance == null) {
			fInstance = new HTMLFormatterFactoryForPhpCode();
		}
		return fInstance;
	}

	protected IStructuredFormatter createFormatter(Node node, IStructuredFormatPreferences formatPreferences) {
		IStructuredFormatter formatter = null;

		switch (node.getNodeType()) {
		case Node.ELEMENT_NODE:
			formatter = new HTMLElementFormatterForPhpCode();
			break;
		case Node.TEXT_NODE:
			if (isEmbeddedCSS(node)) {
				formatter = new EmbeddedCSSFormatterForPhpCode();
			} else {
				formatter = new HTMLTextFormatterForPhpCode();
			}
			break;
		default:
			formatter = new HTMLFormatter();
			break;
		}

		// init FormatPreferences
		formatter.setFormatPreferences(formatPreferences);

		return formatter;
	}

	/**
	 */
	private boolean isEmbeddedCSS(Node node) {
		if (node == null)
			return false;
		Node parent = node.getParentNode();
		if (parent == null)
			return false;
		if (parent.getNodeType() != Node.ELEMENT_NODE)
			return false;
		String name = parent.getNodeName();
		if (name == null)
			return false;
		return name.equalsIgnoreCase("STYLE");//$NON-NLS-1$
	}

	private HTMLFormatterFactoryForPhpCode() {
		super();
	}

	protected StructuredFormatPreferencesXML getFormatPreferences() {
		if (fFormatPreferences == null) {
			fFormatPreferences = new StructuredFormatPreferencesXML();

			IPreferencesService service = Platform.getPreferencesService();
			fFormatPreferences
					.setLineWidth(service.getInt(HTMLCorePlugin.ID, HTMLCorePreferenceNames.LINE_WIDTH, 0, null));
			fFormatPreferences.setSplitMultiAttrs(
					service.getBoolean(HTMLCorePlugin.ID, HTMLCorePreferenceNames.SPLIT_MULTI_ATTRS, false, null));
			fFormatPreferences.setAlignEndBracket(
					service.getBoolean(HTMLCorePlugin.ID, HTMLCorePreferenceNames.ALIGN_END_BRACKET, false, null));
			fFormatPreferences.setClearAllBlankLines(
					service.getBoolean(HTMLCorePlugin.ID, HTMLCorePreferenceNames.CLEAR_ALL_BLANK_LINES, false, null));

			char indentChar = ' ';
			String indentCharPref = service.getString(HTMLCorePlugin.ID, HTMLCorePreferenceNames.INDENTATION_CHAR, null,
					null);
			if (HTMLCorePreferenceNames.TAB.equals(indentCharPref)) {
				indentChar = '\t';
			}
			int indentationWidth = service.getInt(HTMLCorePlugin.ID, HTMLCorePreferenceNames.INDENTATION_SIZE, 0, null);

			StringBuilder indent = new StringBuilder();
			for (int i = 0; i < indentationWidth; i++) {
				indent.append(indentChar);
			}
			fFormatPreferences.setIndent(indent.toString());
		}

		return fFormatPreferences;
	}
}