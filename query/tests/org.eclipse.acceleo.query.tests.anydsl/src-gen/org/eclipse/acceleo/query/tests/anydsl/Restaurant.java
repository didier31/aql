/*******************************************************************************
 * Copyright (c) 2015 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.acceleo.query.tests.anydsl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Restaurant</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.acceleo.query.tests.anydsl.Restaurant#getChefs <em>Chefs</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.acceleo.query.tests.anydsl.AnydslPackage#getRestaurant()
 * @model
 * @generated
 */
public interface Restaurant extends Company {
	/**
	 * Returns the value of the '<em><b>Chefs</b></em>' containment reference list. The list contents are of
	 * type {@link org.eclipse.acceleo.query.tests.anydsl.Chef}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Chefs</em>' containment reference list isn't clear, there really should be
	 * more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Chefs</em>' containment reference list.
	 * @see org.eclipse.acceleo.query.tests.anydsl.AnydslPackage#getRestaurant_Chefs()
	 * @model containment="true"
	 * @generated
	 */
	EList<Chef> getChefs();

} // Restaurant
