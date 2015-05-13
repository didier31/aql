/*******************************************************************************
 * Copyright (c) 2008, 2015 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.acceleo.ide.ui.launching.strategy;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.acceleo.common.internal.utils.AcceleoLogger;
import org.eclipse.acceleo.common.internal.utils.workspace.AcceleoWorkspaceUtil;
import org.eclipse.acceleo.engine.service.AbstractAcceleoGenerator;
import org.eclipse.acceleo.engine.utils.AcceleoLaunchingUtil;
import org.eclipse.acceleo.ide.ui.AcceleoUIActivator;
import org.eclipse.acceleo.internal.ide.ui.AcceleoUIMessages;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.URI;
import org.osgi.framework.Bundle;

/**
 * To launch an Acceleo generator in a plugin as an Eclipse plug-in operation.
 * 
 * @author <a href="mailto:stephane.begaudeau@obeo.fr">Stephane Begaudeau</a>
 * @since 3.1
 */
public class AcceleoPluginLaunchOperation implements IWorkspaceRunnable {
	/**
	 * The name of the generator.
	 */
	private final String bunbleName;

	/**
	 * Qualified name of the class that's to be launched.
	 */
	private final String qualifiedName;

	/**
	 * The model URI.
	 */
	private String model;

	/**
	 * The target folder.
	 */
	private File targetFolder;

	/**
	 * The other arguments of the code generation.
	 */
	private List<String> args;

	/**
	 * Constructor.
	 * 
	 * @param bundleName
	 *            the name of the bundle where the module is located.
	 * @param qualifiedName
	 *            the module Java name (the first character may be in upper case)
	 * @param model
	 *            the model
	 * @param targetFolder
	 *            the target folder
	 * @param args
	 *            the other arguments of the code generation
	 */
	public AcceleoPluginLaunchOperation(String bundleName, String qualifiedName, String model,
			File targetFolder, List<String> args) {
		super();
		this.bunbleName = bundleName;
		this.qualifiedName = qualifiedName;
		this.model = model;
		this.targetFolder = targetFolder;
		this.args = args;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.core.resources.IWorkspaceRunnable#run(org.eclipse.core.runtime.IProgressMonitor)
	 */

	public void run(IProgressMonitor monitor) throws CoreException {
		Class<?> generatorClass = null;
		try {
			Bundle[] bundles = AcceleoWorkspaceUtil.getBundles(bunbleName);
			for (Bundle bundle : bundles) {
				generatorClass = bundle.loadClass(qualifiedName);
				if (generatorClass != null && generatorClass.isAssignableFrom(AbstractAcceleoGenerator.class)) {
					break;
				}
			}
		} catch (ClassNotFoundException e) {
			AcceleoLogger.log(e, true);
		}

		if (generatorClass == null) {
			final IStatus status = new Status(IStatus.ERROR, AcceleoUIActivator.PLUGIN_ID, AcceleoUIMessages
					.getString("AcceleoLaunchOperation.ClassNotFound", qualifiedName, //$NON-NLS-1$
							bunbleName));
			AcceleoUIActivator.getDefault().getLog().log(status);
			return;
		}

		try {
			AbstractAcceleoGenerator generator = null;
			if (AbstractAcceleoGenerator.class.isAssignableFrom(generatorClass)) {
				generator = safeInstantiate(generatorClass);
			}

			final long start = System.currentTimeMillis();
			if (generator != null) {
				String modelLocation = model;
				final String platformResource = "platform:/resource"; //$NON-NLS-1$
				if (modelLocation.startsWith(platformResource)) {
					modelLocation = modelLocation.substring(platformResource.length());
					IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(modelLocation));
					if (file != null && file.isAccessible()) {
						modelLocation = file.getLocation().toString();
					}
				}

				monitor.subTask(AcceleoUIMessages.getString("AcceleoLaunchOperation.Initialize")); //$NON-NLS-1$
				URI modelURI = URI.createFileURI(modelLocation.toString());
				modelURI = URI.createURI(URI.decode(modelURI.toString()));
				generator.initialize(modelURI, targetFolder, new ArrayList<String>());
				for (String argument : args) {
					generator.addPropertiesFile(argument);
				}
				monitor.worked(10);
				IContainer target = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(
						new Path(targetFolder.getAbsolutePath()));
				String generationID = AcceleoLaunchingUtil.computeUIProjectID(bunbleName, qualifiedName,
						model, target.getFullPath().toString(), args);
				generator.setGenerationID(generationID);
				generator.doGenerate(BasicMonitor.toMonitor(monitor));
			} else {
				// We know the generated class has a "main()" method.
				final Method main = generatorClass.getDeclaredMethod("main", String[].class); //$NON-NLS-1$
				final String[] invocationArgs = new String[2 + args.size()];
				invocationArgs[0] = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(model)
						.toString();
				invocationArgs[1] = targetFolder.getAbsolutePath();
				for (int i = 0; i < args.size(); i++) {
					invocationArgs[i + 2] = args.get(i);

				}
				main.invoke(null, new Object[] {invocationArgs, });
			}
			final long end = System.currentTimeMillis();
			System.out.println(((end - start) / 1000) + "s");
		} catch (NoSuchMethodException e) {
			final IStatus status = new Status(IStatus.ERROR, AcceleoUIActivator.PLUGIN_ID, e.getMessage(), e);
			AcceleoUIActivator.getDefault().getLog().log(status);
		} catch (IllegalArgumentException e) {
			final IStatus status = new Status(IStatus.ERROR, AcceleoUIActivator.PLUGIN_ID, e.getMessage(), e);
			AcceleoUIActivator.getDefault().getLog().log(status);
		} catch (IllegalAccessException e) {
			final IStatus status = new Status(IStatus.ERROR, AcceleoUIActivator.PLUGIN_ID, e.getMessage(), e);
			AcceleoUIActivator.getDefault().getLog().log(status);
		} catch (InvocationTargetException e) {
			final IStatus status = new Status(IStatus.ERROR, AcceleoUIActivator.PLUGIN_ID, e.getMessage(), e);
			AcceleoUIActivator.getDefault().getLog().log(status);
		} catch (IOException e) {
			final IStatus status = new Status(IStatus.ERROR, AcceleoUIActivator.PLUGIN_ID, e.getMessage(), e);
			AcceleoUIActivator.getDefault().getLog().log(status);
		} finally {
			AcceleoWorkspaceUtil.INSTANCE.reset();
		}
	}

	/**
	 * Tries and create the {@link AbstractAcceleoGenerator} instance.
	 * 
	 * @param generatorClass
	 *            The class to instantiate.
	 * @return The create instance, <code>null</code> if we couldn't instantiate it.
	 */
	protected AbstractAcceleoGenerator safeInstantiate(Class<?> generatorClass) {
		AbstractAcceleoGenerator generator = null;
		try {
			generator = (AbstractAcceleoGenerator)generatorClass.newInstance();
		} catch (InstantiationException e) {
			final IStatus status = new Status(IStatus.ERROR, AcceleoUIActivator.PLUGIN_ID, e.getMessage(), e);
			AcceleoUIActivator.getDefault().getLog().log(status);
		} catch (IllegalAccessException e) {
			final IStatus status = new Status(IStatus.ERROR, AcceleoUIActivator.PLUGIN_ID, e.getMessage(), e);
			AcceleoUIActivator.getDefault().getLog().log(status);
		}

		return generator;
	}
}
