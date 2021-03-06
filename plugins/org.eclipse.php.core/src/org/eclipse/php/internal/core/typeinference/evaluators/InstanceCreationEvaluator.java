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
package org.eclipse.php.internal.core.typeinference.evaluators;

import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.references.TypeReference;
import org.eclipse.dltk.evaluation.types.UnknownType;
import org.eclipse.dltk.ti.GoalState;
import org.eclipse.dltk.ti.ISourceModuleContext;
import org.eclipse.dltk.ti.goals.ExpressionTypeGoal;
import org.eclipse.dltk.ti.goals.GoalEvaluator;
import org.eclipse.dltk.ti.goals.IGoal;
import org.eclipse.dltk.ti.types.IEvaluatedType;
import org.eclipse.php.core.PHPVersion;
import org.eclipse.php.core.compiler.ast.nodes.ClassInstanceCreation;
import org.eclipse.php.core.project.ProjectOptions;
import org.eclipse.php.internal.core.typeinference.context.MethodContext;

public class InstanceCreationEvaluator extends GoalEvaluator {

	private IEvaluatedType result;

	public InstanceCreationEvaluator(IGoal goal) {
		super(goal);
	}

	@Override
	public IGoal[] init() {
		ExpressionTypeGoal typedGoal = (ExpressionTypeGoal) goal;
		ClassInstanceCreation expression = (ClassInstanceCreation) typedGoal.getExpression();
		if (expression.getAnonymousClassDeclaration() != null
				&& typedGoal.getContext() instanceof ISourceModuleContext) {
			return new IGoal[] { new ExpressionTypeGoal(goal.getContext(), expression.getAnonymousClassDeclaration()) };
		}
		Expression className = expression.getClassName();
		if ((className instanceof TypeReference)) {
			if (isSelf((TypeReference) className) && (goal.getContext() instanceof MethodContext)) {
				MethodContext methodContext = (MethodContext) goal.getContext();
				result = methodContext.getInstanceType();
				return IGoal.NO_GOALS;
			} else {
				return new IGoal[] { new ExpressionTypeGoal(goal.getContext(), className) };
			}
		}
		result = UnknownType.INSTANCE;
		return IGoal.NO_GOALS;
	}

	@Override
	public Object produceResult() {
		return result;
	}

	@Override
	public IGoal[] subGoalDone(IGoal subgoal, Object result, GoalState state) {
		this.result = (IEvaluatedType) result;
		return IGoal.NO_GOALS;
	}

	private boolean isSelf(TypeReference className) {
		String name = className.getName();
		if (goal.getContext() instanceof ISourceModuleContext && PHPVersion.PHP5_4.isLessThan(
				ProjectOptions.getPHPVersion(((ISourceModuleContext) goal.getContext()).getSourceModule()))) {
			name = name.toLowerCase();
		}

		return "self".equals(name); //$NON-NLS-1$
	}

}
