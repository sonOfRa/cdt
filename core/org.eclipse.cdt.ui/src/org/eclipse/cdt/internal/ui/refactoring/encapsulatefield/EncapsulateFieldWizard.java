package org.eclipse.cdt.internal.ui.refactoring.encapsulatefield;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

/**
 * @since 5.9
 */
public class EncapsulateFieldWizard extends RefactoringWizard {

	public EncapsulateFieldWizard(Refactoring refactoring) {
		super(refactoring, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
	}

	@Override
	protected void addUserInputPages() {
		// No pages to add
	}

}
