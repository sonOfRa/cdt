/*
 * Copyright (c) 2013 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.cdt.qt.tests;

import org.eclipse.cdt.qt.core.index.IQObject;
import org.eclipse.cdt.qt.core.index.QtIndex;

public class QtIndexTests extends BaseQtTestCase {

	private static final String Filename_testCache = "testCache.hh";

	// #include "junit-QObject.hh"
	// class B : public QObject
	// {
	// Q_OBJECT
	// };
	public void testLookup() throws Exception {
		loadComment(Filename_testCache);

		QtIndex qtIndex = QtIndex.getIndex(fProject);
		assertNotNull(qtIndex);

		// make sure the instance can be found
		IQObject qobj1 = qtIndex.findQObject(new String[]{ "B" });
		assertNotNull(qobj1);
		assertEquals("B", qobj1.getName());

		// make sure the instance is still found after the content changes
		changeBDecl();
		IQObject qobj2 = qtIndex.findQObject(new String[]{ "B" });
		assertNotNull(qobj2);
		assertEquals("B", qobj2.getName());
	}

	// #include "junit-QObject.hh"
	// class B : public QObject
	// {
	// Q_OBJECT
	// Q_PROPERTY(bool allowed READ isAllowed())
	// };
	public void changeBDecl() throws Exception {
		loadComment(Filename_testCache);
	}
}
