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
package org.eclipse.php.internal.ui.preferences.includepath;

import java.util.List;

import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.internal.ui.wizards.buildpath.BuildPathBasePage;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.dltk.ui.util.PixelConverter;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class PHPBuildpathOrderingWorkbookPage extends BuildPathBasePage {

	private ListDialogField<?> fBuildpathList;

	public PHPBuildpathOrderingWorkbookPage(ListDialogField<?> buildpathList) {
		fBuildpathList = buildpathList;
	}

	@Override
	public Control getControl(Composite parent) {
		PixelConverter converter = new PixelConverter(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		LayoutUtil.doDefaultLayout(composite, new DialogField[] { fBuildpathList }, true, SWT.DEFAULT, SWT.DEFAULT);
		LayoutUtil.setHorizontalGrabbing(fBuildpathList.getListControl(null));

		int buttonBarWidth = converter.convertWidthInCharsToPixels(24);
		fBuildpathList.setButtonsMinWidth(buttonBarWidth);

		return composite;
	}

	/*
	 * @see BuildPathBasePage#getSelection
	 */
	@Override
	public List<?> getSelection() {
		return fBuildpathList.getSelectedElements();
	}

	/*
	 * @see BuildPathBasePage#setSelection
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void setSelection(List selElements, boolean expand) {
		fBuildpathList.selectElements(new StructuredSelection(selElements));
	}

	@Override
	public boolean isEntryKind(int kind) {
		return true;
	}

	@Override
	public void init(IScriptProject scriptProject) {
	}

}
