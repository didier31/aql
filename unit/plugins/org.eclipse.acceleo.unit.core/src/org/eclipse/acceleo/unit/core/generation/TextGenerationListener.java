/*******************************************************************************
 * Copyright (c) 2006, 2014 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.acceleo.unit.core.generation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.acceleo.engine.event.AbstractAcceleoTextGenerationListener;
import org.eclipse.acceleo.engine.event.AcceleoTextGenerationEvent;
import org.eclipse.acceleo.traceability.GeneratedFile;
import org.eclipse.emf.ecore.EObject;

/**
 * The traceability listener.
 * 
 * @author <a href="mailto:hugo.marchadour@obeo.fr">Hugo Marchadour</a>
 * @author <a href="mailto:stephane.begaudeau@obeo.fr">Stephane Begaudeau</a>
 */
public class TextGenerationListener extends AbstractAcceleoTextGenerationListener {

	/**
	 * The generated files.
	 */
	private List<GeneratedFile> generatedFiles = new ArrayList<GeneratedFile>();

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.acceleo.engine.event.IAcceleoTextGenerationListener#textGenerated(org.eclipse.acceleo.engine.event.AcceleoTextGenerationEvent)
	 */
	public void textGenerated(AcceleoTextGenerationEvent event) {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.acceleo.engine.event.IAcceleoTextGenerationListener#filePathComputed(org.eclipse.acceleo.engine.event.AcceleoTextGenerationEvent)
	 */
	public void filePathComputed(AcceleoTextGenerationEvent event) {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.acceleo.engine.event.IAcceleoTextGenerationListener#fileGenerated(org.eclipse.acceleo.engine.event.AcceleoTextGenerationEvent)
	 */
	public void fileGenerated(AcceleoTextGenerationEvent event) {
		EObject traceabilityInformation = event.getTraceabilityInformation();
		if (traceabilityInformation instanceof GeneratedFile) {
			GeneratedFile generatedFile = (GeneratedFile)traceabilityInformation;
			this.generatedFiles.add(generatedFile);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.acceleo.engine.event.IAcceleoTextGenerationListener#generationEnd(org.eclipse.acceleo.engine.event.AcceleoTextGenerationEvent)
	 */
	public void generationEnd(AcceleoTextGenerationEvent event) {
		// nothing to do.
	}

	/**
	 * Returns the list of all the generated files. This list will be empty with a simple generate(), use
	 * generateFile().
	 * 
	 * @return The list of all the generated files.
	 */
	public List<GeneratedFile> getGeneratedFiles() {
		return generatedFiles;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.acceleo.engine.event.IAcceleoTextGenerationListener#listensToGenerationEnd()
	 */
	public boolean listensToGenerationEnd() {
		return true;
	}

}
