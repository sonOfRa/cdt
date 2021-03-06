/*******************************************************************************
 * Copyright (c) 2004, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrew Niefer (IBM Corporation) - initial API and implementation
 *     Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.internal.core.dom.parser.cpp;

import static org.eclipse.cdt.internal.core.dom.parser.cpp.CPPBasicType.UNSPECIFIED_TYPE;

import org.eclipse.cdt.core.dom.ast.IParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassScope;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunctionType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor;

/**
 * Binding for implicit constructors (default and copy constructor).
 */
public class CPPImplicitConstructor extends CPPImplicitMethod implements ICPPConstructor {

	public CPPImplicitConstructor(ICPPClassScope scope, char[] name, ICPPParameter[] params) {
        super(scope, name, createFunctionType(params), params);
    }

	private static ICPPFunctionType createFunctionType(IParameter[] params) {
		return CPPVisitor.createImplicitFunctionType(UNSPECIFIED_TYPE, params, false, false);
	}
}
