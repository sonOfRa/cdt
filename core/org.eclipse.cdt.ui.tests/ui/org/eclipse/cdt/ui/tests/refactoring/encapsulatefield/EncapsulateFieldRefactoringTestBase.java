package org.eclipse.cdt.ui.tests.refactoring.encapsulatefield;

import junit.framework.TestSuite;

import org.eclipse.ltk.core.refactoring.Refactoring;

import org.eclipse.cdt.ui.tests.refactoring.RefactoringTestBase;

import org.eclipse.cdt.internal.ui.refactoring.encapsulatefield.EncapsulateFieldRefactoring;

public class EncapsulateFieldRefactoringTestBase extends RefactoringTestBase {

	public EncapsulateFieldRefactoringTestBase() {
		super();
	}

	public EncapsulateFieldRefactoringTestBase(String name) {
		super(name);
	}

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(EncapsulateFieldRefactoringTest.class);
		return suite;
	}

	@Override
	protected Refactoring createRefactoring() {
		return new EncapsulateFieldRefactoring(getSelectedTranslationUnit(),
				getSelection(), getCProject());
	}

	/**
	 * Compares files in a normalized manner.
	 * 
	 * This comparison is looser than the default
	 * {@link RefactoringTestBase#assertRefactoringSuccess()}. This has the
	 * advantage of formatting differences not influencing the outcome of the
	 * result, as they should not be relevant to the correctness of the inserted
	 * and changed code.
	 */
	@Override
	protected void assertRefactoringSuccess() throws Exception {
		executeRefactoring(true);
		compareFilesNormalized();
	}
}
