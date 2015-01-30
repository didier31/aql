/*******************************************************************************
 * Copyright (c) 2013 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.acceleo.ui.interpreter.ocl;

import java.util.concurrent.Callable;

import org.eclipse.acceleo.ui.interpreter.language.AbstractLanguageInterpreter;
import org.eclipse.acceleo.ui.interpreter.language.CompilationResult;
import org.eclipse.acceleo.ui.interpreter.language.EvaluationContext;
import org.eclipse.acceleo.ui.interpreter.language.EvaluationResult;
import org.eclipse.acceleo.ui.interpreter.language.InterpreterContext;
import org.eclipse.acceleo.ui.interpreter.language.SplitExpression;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ocl.examples.xtext.console.xtfo.EmbeddedXtextEditor;
import org.eclipse.ocl.xtext.essentialocl.ui.internal.EssentialOCLActivator;
import org.eclipse.ocl.xtext.essentialocl.utilities.EssentialOCLPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;

/**
 * Implements the necessary API for an OCL interpreter.
 * 
 * @author <a href="mailto:laurent.goubet@obeo.fr">Laurent Goubet</a>
 */
public class OCLInterpreter extends AbstractLanguageInterpreter {
	/** The current editor. */
	private EmbeddedXtextEditor editor;

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.acceleo.ui.interpreter.language.AbstractLanguageInterpreter#getCompilationTask(InterpreterContext)
	 */
	@Override
	public Callable<CompilationResult> getCompilationTask(InterpreterContext context) {
		return new OCLCompilationTask(context, editor);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.acceleo.ui.interpreter.language.AbstractLanguageInterpreter#getEvaluationTask(EvaluationContext)
	 */
	@Override
	public Callable<EvaluationResult> getEvaluationTask(EvaluationContext context) {
		return new OCLEvaluationTask(context, editor.getEnvironmentFactory());

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.acceleo.ui.interpreter.language.AbstractLanguageInterpreter#getExpressionSplittingTask(EvaluationContext)
	 */
	@Override
	public Callable<SplitExpression> getExpressionSplittingTask(EvaluationContext context) {
		return new OCLExpressionSplittingTask(context);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.acceleo.ui.interpreter.language.AbstractLanguageInterpreter#createSourceViewer(Composite)
	 */
	@Override
	public SourceViewer createSourceViewer(Composite parent) {
		editor = new EmbeddedXtextEditor(parent, EssentialOCLActivator.getInstance().getInjector(
				EssentialOCLPlugin.LANGUAGE_ID), SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		SourceViewer viewer = editor.getViewer();
		viewer.getControl().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				editor.getResource().unload();
				editor = null;
			}
		});
		return viewer;
	}
}
