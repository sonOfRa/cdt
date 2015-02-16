package org.eclipse.cdt.internal.ui.refactoring.encapsulatefield;

import org.eclipse.osgi.util.NLS;

final class Messages extends NLS {
	public static String EncapsulateFieldRefactoring_NoTemplateParameterAvailable;
	public static String EncapsulateFieldRefactoring_HasExternalReferences;
	public static String EncapsulateFieldRefactoring_ENCAPSULATE_FIELD;
	public static String EncapsulateFieldRefactoring_NoNameSelected;
	public static String EncapsulateFieldRefactoring_NoFieldNameSelected;
	public static String EncapsulateFieldRefactoring_CanOnlyEncapsulateFields;
	public static String EncapsulateFieldRefactoring_FILE_CHANGE_TEXT;
	public static String EncapsulateFieldRefactoring_EnclosingClassNotFound;
	public static String EncapsulateFieldRefactoring_IsAlreadyPrivate;

	static {
		NLS.initializeMessages(Messages.class.getName(), Messages.class);
	}

	// Do not instantiate
	private Messages() {
	}
}
