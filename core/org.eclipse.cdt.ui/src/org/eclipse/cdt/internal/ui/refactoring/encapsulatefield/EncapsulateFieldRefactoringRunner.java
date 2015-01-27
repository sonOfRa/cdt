package org.eclipse.cdt.internal.ui.refactoring.encapsulatefield;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.IShellProvider;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;

import org.eclipse.cdt.internal.ui.refactoring.RefactoringRunner;
import org.eclipse.cdt.internal.ui.refactoring.RefactoringSaveHelper;

/**
 * @since 5.9
 */
public class EncapsulateFieldRefactoringRunner extends RefactoringRunner {

	public EncapsulateFieldRefactoringRunner(ICElement element, ISelection selection,
			IShellProvider shellProvider, ICProject cProject) {
		super(element, selection, shellProvider, cProject);
	}

	@Override
	public void run() {
		EncapsulateFieldRefactoring refactoring = new EncapsulateFieldRefactoring(element, selection, project);
		EncapsulateFieldWizard wizard = new EncapsulateFieldWizard(refactoring);
		run(wizard, refactoring, RefactoringSaveHelper.SAVE_NOTHING);
	}

}
