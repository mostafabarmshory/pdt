/*******************************************************************************
 * Copyright (c) 2009, 2016 IBM Corporation and others.
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
package org.eclipse.php.internal.core.codeassist.strategies;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.core.codeassist.ICompletionReporter;
import org.eclipse.php.core.codeassist.IElementFilter;
import org.eclipse.php.internal.core.codeassist.contexts.AbstractCompletionContext;
import org.eclipse.php.internal.core.language.keywords.PHPKeywords;
import org.eclipse.php.internal.core.language.keywords.PHPKeywords.KeywordData;

/**
 * This strategy completes keywords. Direct implementation must define what kind
 * of keywords should be proposed in code assist.
 * 
 * @author michael
 */
public abstract class KeywordsStrategy extends ElementsStrategy {

	public KeywordsStrategy(ICompletionContext context, IElementFilter elementFilter) {
		super(context, elementFilter);
	}

	public KeywordsStrategy(ICompletionContext context) {
		super(context);
	}

	@Override
	public void apply(ICompletionReporter reporter) throws BadLocationException {

		ICompletionContext context = getContext();
		AbstractCompletionContext concreteContext = (AbstractCompletionContext) context;
		ISourceModule sourceModule = getCompanion().getSourceModule();
		String prefix = concreteContext.getPrefix();
		ISourceRange replaceRange = getReplacementRange(concreteContext);
		Collection<KeywordData> keywordsList = PHPKeywords.getInstance(sourceModule.getScriptProject().getProject())
				.findByPrefix(prefix);
		for (KeywordData keyword : keywordsList) {
			if (!filterKeyword(keyword)) {
				String suffix = getSuffix(keyword, replaceRange);
				reporter.reportKeyword(keyword.name, suffix, replaceRange);
			}
		}
	}

	private String getSuffix(KeywordData keyword, ISourceRange replaceRange) {
		String suffix = keyword.suffix;
		if (StringUtils.isEmpty(suffix)) {
			return suffix;
		}

		AbstractCompletionContext context = (AbstractCompletionContext) getContext();
		int offset;
		if (isInsertMode()) {
			offset = getCompanion().getOffset();
		} else {
			offset = replaceRange.getOffset() + replaceRange.getLength();
		}
		try {
			String realSuffix = getCompanion().getDocument().get(offset, suffix.length());
			if (suffix.equals(realSuffix)) {
				// return empty suffix if exists in target document
				return StringUtils.EMPTY;
			}
		} catch (BadLocationException e) {
			return suffix;
		}

		return suffix;
	}

	@Override
	public ISourceRange getReplacementRange(ICompletionContext context) throws BadLocationException {
		if (!isInsertMode()) {
			return getReplacementRangeWithSpaceAtPrefixEnd(context);
		}
		return super.getReplacementRange(context);
	}

	/**
	 * Filters keyword from the proposal list
	 * 
	 * @return
	 */
	abstract protected boolean filterKeyword(KeywordData keyword);
}
