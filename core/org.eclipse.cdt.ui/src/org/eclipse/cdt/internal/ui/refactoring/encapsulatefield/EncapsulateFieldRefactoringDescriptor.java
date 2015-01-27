package org.eclipse.cdt.internal.ui.refactoring.encapsulatefield;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import org.eclipse.cdt.core.model.ICProject;

import org.eclipse.cdt.internal.ui.refactoring.CRefactoring;
import org.eclipse.cdt.internal.ui.refactoring.CRefactoringDescriptor;

/**
 * @since 5.9
 */
public class EncapsulateFieldRefactoringDescriptor extends CRefactoringDescriptor {

	protected EncapsulateFieldRefactoringDescriptor(String project, String description,
			String comment, Map<String, String> arguments) {
		super(EncapsulateFieldRefactoring.ID, project, description, comment,
				RefactoringDescriptor.STRUCTURAL_CHANGE, arguments);
	}

	@Override
	public CRefactoring createRefactoring(RefactoringStatus status) throws CoreException {
		ISelection selection = getSelection();
		ICProject proj = getCProject();
		return new EncapsulateFieldRefactoring(getTranslationUnit(), selection, proj);
	}

}
