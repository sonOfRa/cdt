package org.eclipse.cdt.ui.refactoring.actions;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.window.IShellProvider;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.IField;
import org.eclipse.cdt.core.model.ISourceReference;
import org.eclipse.cdt.core.model.IWorkingCopy;

import org.eclipse.cdt.internal.ui.refactoring.encapsulatefield.EncapsulateFieldRefactoringRunner;

/**
 * @noextend This class is not intended to be subclassed by clients.
 * @since 5.9
 */
public class EncapsulateFieldAction extends RefactoringAction {
    
    public EncapsulateFieldAction() {
        super(Messages.EncapsulateFieldAction_label); 
    }
    
	@Override
	public void run(IShellProvider shellProvider, ICElement elem) {
		if (elem instanceof ISourceReference) {
			new EncapsulateFieldRefactoringRunner(elem, null, shellProvider, elem.getCProject()).run();
		}
	}

	@Override
	public void run(IShellProvider shellProvider, IWorkingCopy wc, ITextSelection selection) {
		if (wc.getResource() != null) {
			new EncapsulateFieldRefactoringRunner(wc, selection, shellProvider, wc.getCProject()).run();
		}
	}

    @Override
	public void updateSelection(ICElement elem) {
    	super.updateSelection(elem);
    	if (!(elem instanceof IField) || !(elem instanceof ISourceReference) ||
    			((ISourceReference) elem).getTranslationUnit().getResource() == null) {
    		setEnabled(false);
    	}
    }
}
