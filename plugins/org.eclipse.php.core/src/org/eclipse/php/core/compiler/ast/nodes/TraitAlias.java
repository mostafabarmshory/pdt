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
package org.eclipse.php.core.compiler.ast.nodes;

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.references.SimpleReference;

/**
 * A:b() as public b
 * 
 * @author zhaozw
 * 
 */
public class TraitAlias extends Expression {
	private Expression traitMethod;
	private int modifier;
	/**
	 * methodName could be null
	 */
	private SimpleReference methodName;

	public TraitAlias(Expression traitMethod, int modifier, SimpleReference methodName) {
		this.traitMethod = traitMethod;
		this.modifier = modifier;
		this.methodName = methodName;
	}

	@Override
	public void traverse(ASTVisitor pVisitor) throws Exception {
		if (pVisitor.visit(this)) {
			if (traitMethod != null) {
				traitMethod.traverse(pVisitor);
			}
			pVisitor.endvisit(this);
		}
	}

	public Expression getTraitMethod() {
		return traitMethod;
	}

	public int getModifier() {
		return modifier;
	}

	public SimpleReference getMethodName() {
		return methodName;
	}

	@Override
	public int getKind() {
		// TODO Auto-generated method stub
		return 0;
	}

}
