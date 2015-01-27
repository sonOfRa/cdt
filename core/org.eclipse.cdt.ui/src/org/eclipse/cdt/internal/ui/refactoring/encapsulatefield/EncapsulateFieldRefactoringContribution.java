package org.eclipse.cdt.internal.ui.refactoring.encapsulatefield;

import java.util.Map;

import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import org.eclipse.cdt.internal.ui.refactoring.CRefactoringContribution;

/**
 * @since 5.9
 */
public class EncapsulateFieldRefactoringContribution extends CRefactoringContribution {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RefactoringDescriptor createDescriptor(String id, String project, String description,
			String comment, Map arguments, int flags) throws IllegalArgumentException {
		if (id.equals(EncapsulateFieldRefactoring.ID)) {
			return new EncapsulateFieldRefactoringDescriptor(project, description, comment, arguments);
		}
		return null;
	}
}
