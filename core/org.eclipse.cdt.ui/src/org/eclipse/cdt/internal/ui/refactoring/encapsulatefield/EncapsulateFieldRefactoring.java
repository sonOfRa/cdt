package org.eclipse.cdt.internal.ui.refactoring.encapsulatefield;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEditGroup;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassTemplate;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameter;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;

import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTBinaryExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCompoundStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTConditionalExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTExpressionStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFieldReference;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionCallExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDefinition;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTLiteralExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamedTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTParameterDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTQualifiedName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTReferenceOperator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTReturnStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleTypeTemplateParameter;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTemplateDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTemplateId;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTypeId;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTUnaryExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor;

import org.eclipse.cdt.internal.ui.refactoring.CRefactoring;
import org.eclipse.cdt.internal.ui.refactoring.CRefactoringDescriptor;
import org.eclipse.cdt.internal.ui.refactoring.ClassMemberInserter;
import org.eclipse.cdt.internal.ui.refactoring.ModificationCollector;
import org.eclipse.cdt.internal.ui.refactoring.utils.DefinitionFinder;
import org.eclipse.cdt.internal.ui.refactoring.utils.VisibilityEnum;

/**
 * @since 5.9
 */
public class EncapsulateFieldRefactoring extends CRefactoring {

	// TODO: Add test cases in org.eclipse.cdt.ui.tests.

	/**
	 * This ID is used the Eclipse GUI.
	 */
	public static final String ID = "org.eclipse.cdt.internal.ui.refactoring.encapsulatefield.EncapsulateFieldRefactoring"; //$NON-NLS-1$

	/**
	 * Name of the field declaration.
	 */
	private IASTName fieldName;

	/**
	 * Field declaration whose references are to be replaced by setters/getters.
	 */
	private IASTDeclaration fieldDeclaration;

	/**
	 * Denotes whether the field we are trying to encapsulate is copyable. Needed to determine whether we
	 * should generate a regular or a forwarding setter.
	 */
	private boolean copyable = false;

	/**
	 * The template parameter to use if we are generating a forwarding setter.
	 */
	private String forwardTemplateParameter = null;

	/**
	 * Name for the getter
	 */
	private String getterName = null;

	/**
	 * Name for the setter
	 */
	private String setterName = null;

	/**
	 * Copyability resolver for this instance
	 */
	private CopyabilityResolver resolver = new CopyabilityResolver();

	private boolean foundReferenceError = false;

	public EncapsulateFieldRefactoring(ICElement element, ISelection selection, ICProject project) {
		super(element, selection, project);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.cdt.internal.ui.refactoring.CRefactoring#getRefactoringDescriptor()
	 */
	@Override
	protected RefactoringDescriptor getRefactoringDescriptor() {
		Map<String, String> arguments = getArgumentMap();
		return new EncapsulateFieldRefactoringDescriptor(project.getProject().getName(),
				"Encapsulate Field Refactoring", "Encapsulate " + fieldName.getRawSignature(), arguments); //$NON-NLS-1$//$NON-NLS-2$
	}

	public void test(int foo) {

	}

	/**
	 * The arguments required for this refactoring (point of selection).
	 * 
	 * @return rguments required for this refactoring (point of selection)
	 */
	private Map<String, String> getArgumentMap() {
		Map<String, String> arguments = new HashMap<String, String>();
		arguments.put(CRefactoringDescriptor.FILE_NAME, tu.getLocationURI().toString());
		arguments.put(CRefactoringDescriptor.SELECTION,
				selectedRegion.getOffset() + "," + selectedRegion.getLength()); //$NON-NLS-1$
		return arguments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.cdt.internal.ui.refactoring.CRefactoring#checkFinalConditions(org.eclipse.core.runtime.
	 * IProgressMonitor, org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext)
	 */
	@Override
	protected RefactoringStatus checkFinalConditions(IProgressMonitor subProgressMonitor,
			CheckConditionsContext checkContext) throws CoreException, OperationCanceledException {
		// FIXME: Maybe it is necessary to run checks as to whether refactoring is
		// actually applicable.
		return super.checkFinalConditions(subProgressMonitor, checkContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.cdt.internal.ui.refactoring.CRefactoring#checkInitialConditions(org.eclipse.core.runtime
	 * .IProgressMonitor)
	 * 
	 * The refactoring may be applicable if the selected code is a non-private field declaration within a
	 * class.
	 * 
	 * Copied and adjusted from HideMethodRefactoring.
	 */
	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		SubMonitor sm = SubMonitor.convert(pm, 10);
		try {
			super.checkInitialConditions(sm.newChild(8));

			if (initStatus.hasFatalError()) {
				return initStatus;
			}

			if (isProgressMonitorCanceled(sm, initStatus))
				return initStatus;

			List<IASTName> names = findAllMarkedNames();
			if (names.isEmpty()) {
				initStatus.addFatalError(Messages.EncapsulateFieldRefactoring_NoNameSelected);
				return initStatus;
			}
			IASTName name = names.get(names.size() - 1);

			fieldName = DefinitionFinder.getMemberDeclaration(name, refactoringContext, sm.newChild(1));

			if (fieldName == null) {
				initStatus.addFatalError(Messages.EncapsulateFieldRefactoring_NoFieldNameSelected);
				return initStatus;
			}

			IASTDeclarator decl = (IASTDeclarator) fieldName.getParent();
			decl = CPPVisitor.findOutermostDeclarator(decl);
			fieldDeclaration = (IASTDeclaration) decl.getParent();

			// are we at the class-level?
			if (fieldDeclaration == null
					|| !(fieldDeclaration.getParent() instanceof ICPPASTCompositeTypeSpecifier)) {
				initStatus.addFatalError(Messages.EncapsulateFieldRefactoring_CanOnlyEncapsulateFields);
				return initStatus;
			}

			if (isProgressMonitorCanceled(sm, initStatus))
				return initStatus;

			// field declarations are represented by IASTSimpleDecaration (CPPASTSimpleDeclaration,
			// more precisely)
			if (fieldDeclaration instanceof IASTSimpleDeclaration) {
				// TODO: What is this loop needed for? Is it required only for method declarations
				// which may have multiple declarations?
				for (IASTDeclarator declarator : ((IASTSimpleDeclaration) fieldDeclaration).getDeclarators()) {
					if (declarator.getName().getRawSignature().equals(name.getRawSignature())) {
						if (declarator instanceof IASTFunctionDeclarator) {
							initStatus
									.addFatalError(Messages.EncapsulateFieldRefactoring_CanOnlyEncapsulateFields);
							return initStatus;
						}
					}
				}
				IASTDeclSpecifier declSpec = ((IASTSimpleDeclaration) fieldDeclaration).getDeclSpecifier();
				if (declSpec instanceof ICPPASTNamedTypeSpecifier) {
					ICPPASTNamedTypeSpecifier namedDeclSpec = (ICPPASTNamedTypeSpecifier) declSpec;
					IBinding binding = namedDeclSpec.getName().getBinding();

					if (binding instanceof ITypedef) {
						ITypedef def = (ITypedef) binding;
						if (def.getType() instanceof ICPPClassType) {
							binding = (ICPPClassType) def.getType();
						} else {
							copyable = true;
						}
					}
					if (binding instanceof ICPPClassType) {
						if (binding instanceof ICPPClassSpecialization) {
							resolver.setStartingPoint(fieldDeclaration);
						}
						copyable = resolver.isCopyable((ICPPClassType) binding);
					}
				} else {
					copyable = true;
				}
			} else {
				initStatus.addFatalError(Messages.EncapsulateFieldRefactoring_CanOnlyEncapsulateFields);
				return initStatus;
			}

			// the class containing this field
			IASTCompositeTypeSpecifier classNode = CPPVisitor.findAncestorWithType(fieldName,
					IASTCompositeTypeSpecifier.class);
			if (classNode == null) {
				initStatus.addFatalError(Messages.EncapsulateFieldRefactoring_EnclosingClassNotFound);
				return initStatus;
			}

			// collect template parameters if field is not copyable
			if (!copyable) {
				IBinding classBinding = classNode.getName().getBinding();
				if (classBinding instanceof ICPPClassTemplate) {
					ICPPClassTemplate template = (ICPPClassTemplate) classBinding;
					Set<String> templateParameters = new HashSet<>();
					for (ICPPTemplateParameter templateParameter : template.getTemplateParameters()) {
						templateParameters.add(templateParameter.getName());
					}
					boolean foundParameter = false;
					for (char current = 'A'; current <= 'Z'; current++) {
						String parameter = "" + current; //$NON-NLS-1$
						if (!templateParameters.contains(parameter)) {
							this.forwardTemplateParameter = parameter;
							foundParameter = true;
							break;
						}
					}
					if (!foundParameter) {
						initStatus
								.addFatalError(Messages.EncapsulateFieldRefactoring_NoTemplateParameterAvailable);
					}
				} else {
					this.forwardTemplateParameter = "A"; //$NON-NLS-1$
				}
			}

			// is this field already private in that class?
			if (checkIfPrivate(classNode, fieldDeclaration)) {
				initStatus.addError(Messages.EncapsulateFieldRefactoring_IsAlreadyPrivate);
			}

			checkReferencesForProblems(initStatus);
			return initStatus;
		} finally {
			sm.done();
		}
	}

	/**
	 * Check all references to the field we are refactoring for possibly undefined behaviour, or other cases
	 * the refactoring cannot currently handle
	 * 
	 * @param initStatus
	 */
	private void checkReferencesForProblems(RefactoringStatus initStatus) {
		// all write/read references to field declaration
		final Map<String, ReferencesInFile> references = findReferences().references;

		for (String filename : references.keySet()) {
			// System.out.println("EncapsulateFieldRefactoring.replaceAccesses() handling file " + filename);
			ITranslationUnit tu = getTranslationUnit(filename);

			if (tu == null) { // no translation unit available for this file
				System.err.println("EncapsulateFieldRefactoring.replaceAccesses(): no AST for file " //$NON-NLS-1$
						+ filename);
			} else {
				// create index
				IIndex index;
				try {
					index = CCorePlugin.getIndexManager().getIndex(tu.getCProject());

					// lock the index for read access
					index.acquireReadLock();
					try {
						// create index based AST

						IASTTranslationUnit ast = getAST(tu, null);
						if (ast == null) {
							// file has no AST
							System.err.println("No AST for file " + filename); //$NON-NLS-1$
							break;
						} else {
							checkReferencesForProblemsInUnit(initStatus, ast, references.get(filename));
							if (foundReferenceError) {
								break;
							}
						}
					} finally {
						index.releaseReadLock();
					}
				} catch (Exception e) {
					System.err.println(e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
		}
	}

	private void checkReferencesForProblemsInUnit(final RefactoringStatus initStatus,
			final IASTTranslationUnit ast, final ReferencesInFile referencesInFile) {
		ast.accept(new ASTVisitor() {
			{
				shouldVisitNames = true;
			}

			@Override
			public int visit(IASTName name) {
				final int nodeOffset = name.getFileLocation().getNodeOffset();
				if (referencesInFile.isRead(nodeOffset) || referencesInFile.isWrite(nodeOffset)) {
					// node found
					checkReference(initStatus, ast, name, referencesInFile.isRead(nodeOffset),
							referencesInFile.isWrite(nodeOffset));
					if (foundReferenceError) {
						return PROCESS_ABORT;
					}
				}
				return super.visit(name);
			}
		});

	}

	/**
	 * Check a single reference for errors, and add errors to the init status
	 * 
	 * @param initStatus
	 *            initStatus for error checking
	 * @param ast
	 *            index ast to operator on
	 * @param name
	 *            AST node for this reference
	 * @param read
	 *            reference is read reference
	 * @param write
	 *            reference is write reference
	 */
	private void checkReference(RefactoringStatus initStatus, IASTTranslationUnit ast, IASTNode name,
			boolean read, boolean write) {
		if (name.getPropertyInParent() == ICPPASTFieldReference.FIELD_NAME) {
			if (write) {
				if (isUsedInComplexExpression((ICPPASTFieldReference) name.getParent())) {
					initStatus.addFatalError("Cannot refactor write accesses used in complex expressions.");
					foundReferenceError = true;
					return;
				}
			}
			if (read && write) {
				if (name.getParent().getPropertyInParent() != ICPPASTUnaryExpression.OPERAND) {
					initStatus
							.addFatalError("Cannot refactor read-write references that aren't unary operands (you may be using a pointer or a reference to a field)");
					foundReferenceError = true;
					return;
				}
				int writeCounter = 0;
				IASTNode parent = name.getParent();
				while (parent.getPropertyInParent() == ICPPASTUnaryExpression.OPERAND) {
					ICPPASTUnaryExpression unExp = (ICPPASTUnaryExpression) parent.getParent();
					if (usesUnaryAssignmentOperators(unExp)) {
						++writeCounter;
					}
					parent = parent.getParent();
				}

				if (writeCounter > 1) {
					initStatus
							.addFatalError("Cannot refactor unary operand references with more than one write");
					foundReferenceError = true;
					return;
				}

				while (parent.getPropertyInParent() != IASTExpressionStatement.EXPRESSION) {
					ASTNodeProperty propInParent = parent.getPropertyInParent();
					if (propInParent == ICPPASTBinaryExpression.OPERAND_ONE
							|| propInParent == ICPPASTBinaryExpression.OPERAND_TWO) {
						ICPPASTBinaryExpression binExp = (ICPPASTBinaryExpression) parent.getParent();
						boolean isAssignment = usesAssignmentOperator(binExp);
						if (propInParent == ICPPASTBinaryExpression.OPERAND_TWO) {
							if (isAssignment) {
								initStatus
										.addFatalError("Cannot safely refactor assignments to a write-reference.");
								foundReferenceError = true;
								return;
							}
						} else {
							IASTExpression lhs = binExp.getOperand1();
							if (lhs instanceof ICPPASTFieldReference
									&& isAssignment
									&& name.toString().equals(
											((ICPPASTFieldReference) lhs).getFieldName().toString())
									&& ((ICPPASTFieldReference) lhs).getFieldOwnerType().equals(
											((ICPPASTFieldReference) name.getParent()).getFieldOwnerType())) {
								initStatus
										.addFatalError("Cannot safely refactor assignments to field with write occurrences to same field on the right hand side.");
								foundReferenceError = true;
								return;

							}
						}
					}
					parent = parent.getParent();
				}
			}
		}
	}

	/**
	 * Checks whether the given Field reference is used in a complex expression.
	 * 
	 * This is used only for write-references. A write reference can only be refactored if it is a simple
	 * expression like "++foo.bar", or a simple assignment like foo.bar = 10
	 * 
	 * @param fieldReference
	 *            the field reference to check
	 * @return true if the field reference is used in a complex expression
	 */
	private boolean isUsedInComplexExpression(ICPPASTFieldReference fieldReference) {
		IASTNode cursor = fieldReference;
		while (cursor.getPropertyInParent() == ICPPASTUnaryExpression.OPERAND) {
			cursor = cursor.getParent();
		}

		if (cursor.getPropertyInParent() == IASTExpressionStatement.EXPRESSION) {
			return false;
		}
		while (isBinaryExpressionArgument(cursor)) {

			if (cursor.getPropertyInParent() == ICPPASTBinaryExpression.OPERAND_TWO) {
				return true;
			}

			cursor = cursor.getParent();
		}

		ASTNodeProperty prop = cursor.getPropertyInParent();
		return prop == IASTEqualsInitializer.INITIALIZER || prop == IASTInitializerList.NESTED_INITIALIZER
				|| prop == IASTFunctionCallExpression.ARGUMENT;
	}

	/**
	 * @param cursor
	 * @return
	 */
	private boolean isBinaryExpressionArgument(IASTNode cursor) {
		return cursor.getPropertyInParent() == ICPPASTBinaryExpression.OPERAND_ONE
				|| cursor.getPropertyInParent() == ICPPASTBinaryExpression.OPERAND_TWO;
	}

	/**
	 * @param unExp
	 *            a unary expression
	 * @return true if the unary expression uses an operator like ++ or --
	 */
	private boolean usesUnaryAssignmentOperators(ICPPASTUnaryExpression unExp) {
		return unExp.getOperator() == ICPPASTUnaryExpression.op_prefixIncr
				|| unExp.getOperator() == ICPPASTUnaryExpression.op_prefixDecr
				|| unExp.getOperator() == ICPPASTUnaryExpression.op_postFixIncr
				|| unExp.getOperator() == ICPPASTUnaryExpression.op_postFixDecr;
	}

	/**
	 * @param binExp
	 *            a binary expression
	 * @return true if the binary expression uses any form of assignment operator
	 */
	private boolean usesAssignmentOperator(ICPPASTBinaryExpression binExp) {
		return binExp.getOperator() == ICPPASTBinaryExpression.op_assign
				|| binExp.getOperator() == ICPPASTBinaryExpression.op_binaryAndAssign
				|| binExp.getOperator() == ICPPASTBinaryExpression.op_binaryOrAssign
				|| binExp.getOperator() == ICPPASTBinaryExpression.op_binaryXorAssign
				|| binExp.getOperator() == ICPPASTBinaryExpression.op_divideAssign
				|| binExp.getOperator() == ICPPASTBinaryExpression.op_minusAssign
				|| binExp.getOperator() == ICPPASTBinaryExpression.op_moduloAssign
				|| binExp.getOperator() == ICPPASTBinaryExpression.op_multiplyAssign
				|| binExp.getOperator() == ICPPASTBinaryExpression.op_plusAssign
				|| binExp.getOperator() == ICPPASTBinaryExpression.op_shiftLeftAssign
				|| binExp.getOperator() == ICPPASTBinaryExpression.op_shiftRightAssign;
	}

	/**
	 * True if given declaration is a private declaration in given class.
	 * 
	 * Copied and adjusted from HideMethodRefactoring.
	 * 
	 * @param classNode
	 *            a class
	 * @param decl
	 *            a member declaration of this class
	 * @return true if given declaration is a private declaration in given class
	 */
	private boolean checkIfPrivate(IASTCompositeTypeSpecifier classNode, IASTDeclaration decl) {
		IASTDeclaration[] members = classNode.getMembers();
		int currentVisibility = ICPPASTVisibilityLabel.v_private;
		if (IASTCompositeTypeSpecifier.k_struct == classNode.getKey()) {
			currentVisibility = ICPPASTVisibilityLabel.v_public;
		}
		for (IASTDeclaration declaration : members) {
			if (declaration instanceof ICPPASTVisibilityLabel) {
				currentVisibility = ((ICPPASTVisibilityLabel) declaration).getVisibility();
			}

			if (declaration != null) {
				if (decl == declaration) {
					break;
				}
			}
		}
		if (ICPPASTVisibilityLabel.v_private == currentVisibility) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.cdt.internal.ui.refactoring.CRefactoring#collectModifications(org.eclipse.core.runtime.
	 * IProgressMonitor, org.eclipse.cdt.internal.ui.refactoring.ModificationCollector)
	 */
	@Override
	protected void collectModifications(IProgressMonitor pm, ModificationCollector collector)
			throws CoreException, OperationCanceledException {
		if (fieldName != null) {
			if (fieldDeclaration != null && fieldDeclaration instanceof IASTSimpleDeclaration) {
				TextEditGroup editGroup = new TextEditGroup(
						Messages.EncapsulateFieldRefactoring_FILE_CHANGE_TEXT + fieldName.getRawSignature());
				// create getter and create setter
				createSetterGetter(collector, editGroup);

				// replace all reads and writes by getters/setters (except for references within the
				// getters/setters)
				replaceAccesses(collector, editGroup);

				// make field private
				makeFieldPrivate(collector, editGroup);
			} else {
				System.err.println("collectModifications(): fieldDeclaration=" + fieldDeclaration); //$NON-NLS-1$
			}
		} else {
			System.err.println("collectModifications(): fieldName=" + fieldName); //$NON-NLS-1$
		}
	}

	/**
	 * Makes field declaration private.
	 * 
	 * Copied and adjusted from HideMethodRefactoring.collectModifications.
	 * 
	 * @param collector
	 *            collector of changes
	 * @param editGroup
	 *            group of edits shown to the user in the GUI
	 */
	private void makeFieldPrivate(ModificationCollector collector, TextEditGroup editGroup) {
		// FIXME: This currently moves the entire declaration: int foo, bar, baz;, even if only "bar" is
		// selected.
		ASTRewrite rewriter = collector.rewriterForTranslationUnit(fieldName.getTranslationUnit());
		ICPPASTCompositeTypeSpecifier classDefinition = (ICPPASTCompositeTypeSpecifier) fieldDeclaration
				.getParent();
		ClassMemberInserter.createChange(classDefinition, VisibilityEnum.v_private, fieldDeclaration, false,
				collector);
		rewriter.remove(fieldDeclaration, editGroup);
	}

	/**
	 * Creates a getter and setter for field declaration.
	 * 
	 * @param collector
	 *            collector of the changes
	 * @param editGroup
	 *            group of edits shown to the user in the GUI
	 */
	private void createSetterGetter(ModificationCollector collector, TextEditGroup editGroup) {
		ICPPASTCompositeTypeSpecifier classDefinition = (ICPPASTCompositeTypeSpecifier) fieldDeclaration
				.getParent();
		String uppercaseName = fieldName.toString();
		uppercaseName = Character.toUpperCase(uppercaseName.charAt(0)) + uppercaseName.substring(1);
		IASTSimpleDeclaration decl = (IASTSimpleDeclaration) fieldDeclaration;

		generateGetter(collector, classDefinition, uppercaseName, decl);
		generateSetter(collector, classDefinition, uppercaseName, decl, editGroup);
	}

	private void generateGetter(ModificationCollector collector,
			ICPPASTCompositeTypeSpecifier classDefinition, String uppercaseName, IASTSimpleDeclaration decl) {
		IASTDeclSpecifier getterDeclSpec = decl.getDeclSpecifier().copy(CopyStyle.withoutLocations);
		getterDeclSpec.setInline(true);

		/*
		 * If the type is not copyable, we need a ref-to-const getter. Set the const part here.
		 */
		if (!copyable) {
			getterDeclSpec.setConst(true);
		}

		getterName = "get" + uppercaseName; //$NON-NLS-1$
		IASTName getterASTName = new CPPASTName(getterName.toCharArray());
		ICPPASTFunctionDeclarator getterDeclarator = new CPPASTFunctionDeclarator(getterASTName);
		getterDeclarator.setConst(true);

		/*
		 * If the type is not copyable, we need a ref-to-const getter. Set the ref part here.
		 */
		if (!copyable) {
			getterDeclarator.addPointerOperator(new CPPASTReferenceOperator(false));
		}

		IASTReturnStatement getterStatement = new CPPASTReturnStatement(new CPPASTIdExpression(
				fieldName.copy()));
		IASTCompoundStatement getterBlock = new CPPASTCompoundStatement();
		getterBlock.addStatement(getterStatement);

		ICPPASTFunctionDefinition getterDef = new CPPASTFunctionDefinition(getterDeclSpec, getterDeclarator,
				getterBlock);
		ClassMemberInserter.createChange(classDefinition, VisibilityEnum.v_public, getterDef, false,
				collector);
	}

	private void generateSetter(ModificationCollector collector,
			ICPPASTCompositeTypeSpecifier classDefinition, String uppercaseName, IASTSimpleDeclaration decl,
			TextEditGroup editGroup) {
		IASTSimpleDeclSpecifier setterDeclSpec = new CPPASTSimpleDeclSpecifier();
		setterDeclSpec.setType(IASTSimpleDeclSpecifier.t_void);
		setterDeclSpec.setInline(true);

		setterName = "set" + uppercaseName; //$NON-NLS-1$
		IASTName setterASTName = new CPPASTName(setterName.toCharArray());
		ICPPASTFunctionDeclarator setterDeclarator = new CPPASTFunctionDeclarator(setterASTName);

		ICPPASTFieldReference fieldRef = new CPPASTFieldReference();
		fieldRef.setIsPointerDereference(true);
		CPPASTLiteralExpression lit = new CPPASTLiteralExpression(CPPASTLiteralExpression.lk_this,
				"this".toCharArray()); //$NON-NLS-1$
		fieldRef.setFieldOwner(lit);
		fieldRef.setFieldName(fieldName.copy(CopyStyle.withoutLocations));

		ICPPASTBinaryExpression assignment = new CPPASTBinaryExpression();
		assignment.setOperator(ICPPASTBinaryExpression.op_assign);
		assignment.setOperand1(fieldRef);

		IASTNode setterDef = null;
		ICPPASTFunctionDefinition setterFunctionDefinition = new CPPASTFunctionDefinition();
		setterFunctionDefinition.setDeclSpecifier(setterDeclSpec);
		if (copyable) {
			ICPPASTParameterDeclaration parameterDecl = new CPPASTParameterDeclaration(decl
					.getDeclSpecifier().copy(CopyStyle.withoutLocations), new CPPASTDeclarator(
					fieldName.copy(CopyStyle.withoutLocations)));
			setterDeclarator.addParameterDeclaration(parameterDecl);

			assignment.setOperand2(new CPPASTIdExpression(fieldName.copy(CopyStyle.withoutLocations)));
			IASTCompoundStatement setterBlock = new CPPASTCompoundStatement();
			setterBlock.addStatement(new CPPASTExpressionStatement(assignment));

			setterFunctionDefinition.setDeclarator(setterDeclarator);
			setterFunctionDefinition.setBody(setterBlock);

			setterDef = setterFunctionDefinition;
		} else {
			ICPPASTSimpleTypeTemplateParameter templateParameter = new CPPASTSimpleTypeTemplateParameter();
			templateParameter.setParameterType(ICPPASTSimpleTypeTemplateParameter.st_typename);
			templateParameter.setName(new CPPASTName(forwardTemplateParameter.toCharArray()));

			ICPPASTTemplateDeclaration templateDeclaration = new CPPASTTemplateDeclaration();
			templateDeclaration.addTemplateParameter(templateParameter);

			ICPPASTDeclarator paramDeclarator = new CPPASTDeclarator(
					fieldName.copy(CopyStyle.withoutLocations));
			paramDeclarator.addPointerOperator(new CPPASTReferenceOperator(true));
			ICPPASTParameterDeclaration parameterDecl = new CPPASTParameterDeclaration();
			parameterDecl.setDeclarator(paramDeclarator);
			parameterDecl.setDeclSpecifier(new CPPASTNamedTypeSpecifier(templateParameter.getName()));
			setterDeclarator.addParameterDeclaration(parameterDecl);

			ICPPASTTypeId typeId = new CPPASTTypeId();
			ICPPASTNamedTypeSpecifier typeSpec = new CPPASTNamedTypeSpecifier(templateParameter.getName());
			typeId.setDeclSpecifier(typeSpec);
			ICPPASTTemplateId templateId = new CPPASTTemplateId();
			templateId.setTemplateName(new CPPASTName("forward".toCharArray())); //$NON-NLS-1$
			templateId.addTemplateArgument(typeId);
			ICPPASTFunctionCallExpression forwardCall = new CPPASTFunctionCallExpression();
			ICPPASTQualifiedName qualifiedName = new CPPASTQualifiedName(new CPPASTName("std".toCharArray())); //$NON-NLS-1$
			qualifiedName.addName(templateId);
			forwardCall.setFunctionNameExpression(new CPPASTIdExpression(qualifiedName));
			IASTInitializerClause init = new CPPASTIdExpression(fieldName.copy(CopyStyle.withoutLocations));
			IASTInitializerClause[] inits = { init };
			forwardCall.setArguments(inits);
			assignment.setOperand2(forwardCall);
			IASTCompoundStatement setterBlock = new CPPASTCompoundStatement();
			setterBlock.addStatement(new CPPASTExpressionStatement(assignment));

			setterFunctionDefinition.setDeclarator(setterDeclarator);
			setterFunctionDefinition.setBody(setterBlock);
			templateDeclaration.setDeclaration(setterFunctionDefinition);

			setterDef = templateDeclaration;

			/*
			 * Since we generated code that uses std::forward, we also need to include the <utility> header.
			 * Check if it is already included, if not, find a suitable insertion point. If there already are
			 * includes in the file, it is inserted before the first include directive. Otherwise
			 */
			IASTNode insertBefore = null;
			boolean utilityIncluded = false;
			for (IASTPreprocessorIncludeStatement includeStmt : fieldName.getTranslationUnit()
					.getIncludeDirectives()) {
				if (insertBefore == null) {
					insertBefore = includeStmt;
				}

				if (includeStmt.isSystemInclude() && includeStmt.getName().toString().equals("utility")) { //$NON-NLS-1$
					utilityIncluded = true;
				}
			}

			if (!utilityIncluded) {
				String lineSeparator = System.lineSeparator();
				if (insertBefore == null) {
					insertBefore = fieldName.getTranslationUnit().getChildren()[0];
					lineSeparator = lineSeparator + System.lineSeparator();
				}

				// FIXME: The CDT does not support ASTRewrites with Preprocessor nodes.
				InsertEdit edit = new InsertEdit(insertBefore.getFileLocation().getNodeOffset(),
						"#include <utility>" + lineSeparator); //$NON-NLS-1$
				editGroup.addTextEdit(edit);
			}
		}

		ClassMemberInserter.createChange(classDefinition, VisibilityEnum.v_public, setterDef, false,
				collector);
	}

	/**
	 * Triggers the replacement of all references to field declaration in all files where that is necessary.
	 * 
	 * @param collector
	 *            collector of the changes
	 * @param editGroup
	 *            group of edits shown to the user in the GUI
	 */
	private void replaceAccesses(ModificationCollector collector, TextEditGroup editGroup) {

		// all write/read references to field declaration
		final Map<String, ReferencesInFile> references = findReferences().references;

		for (String filename : references.keySet()) {
			// System.out.println("EncapsulateFieldRefactoring.replaceAccesses() handling file " + filename);
			ITranslationUnit tu = getTranslationUnit(filename);

			if (tu == null) { // no translation unit available for this file
				System.err.println("EncapsulateFieldRefactoring.replaceAccesses(): no AST for file " //$NON-NLS-1$
						+ filename);
			} else {
				// create index
				IIndex index;
				try {
					index = CCorePlugin.getIndexManager().getIndex(tu.getCProject());

					// lock the index for read access
					index.acquireReadLock();
					try {
						// create index based AST
						IASTTranslationUnit ast = getAST(tu, null);
						if (ast == null) {
							// file has no AST
							System.err.println("No AST for file " + filename); //$NON-NLS-1$
							break;
						} else {
							replaceAccessInUnit(collector, editGroup, ast, references.get(filename));
						}
					} finally {
						index.releaseReadLock();
					}
				} catch (Exception e) {
					System.err.println(e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Rewrites the AST by replacing every read/write field access by getter/setter.
	 * 
	 * @param collector
	 *            to collect the changes
	 * @param editGroup
	 *            group of edits shown to the user in the GUI
	 * @param ast
	 *            the AST to be rewritten
	 * @param node
	 *            node at which the rewrite is to be applied
	 * @param isRead
	 *            tells us whether node is read
	 * @param isWrite
	 *            tells us whether node is written
	 */
	private void replaceAccess(ModificationCollector collector, TextEditGroup editGroup,
			IASTTranslationUnit ast, IASTNode node, boolean isRead, boolean isWrite) {
		IASTNode cursor = node;
		ASTRewrite rewrite = collector.rewriterForTranslationUnit(ast);
		// now we can investigate the syntactic context
		if (cursor.getPropertyInParent() == IASTFieldReference.FIELD_NAME) {
			// context is an access to the field

			if (isRead) {
				if (isWrite) {
					IASTFieldReference fRef = (IASTFieldReference) cursor.getParent();
					if (fRef.getPropertyInParent() == IASTUnaryExpression.OPERAND) {
						IASTUnaryExpression unExp = (IASTUnaryExpression) fRef.getParent();
						ICPPASTExpression setterReceiver = (ICPPASTExpression) fRef.getFieldOwner().copy(
								CopyStyle.withoutLocations);

						ICPPASTName setterMethodName = new CPPASTName(setterName.toCharArray());
						ICPPASTFieldReference setterReference = new CPPASTFieldReference(setterMethodName,
								setterReceiver);

						ICPPASTFunctionCallExpression setterCall = new CPPASTFunctionCallExpression();
						setterCall.setFunctionNameExpression(setterReference);

						ICPPASTExpression getterReceiver = (ICPPASTExpression) fRef.getFieldOwner().copy(
								CopyStyle.withoutLocations);

						ICPPASTName getterMethodName = new CPPASTName(getterName.toCharArray());
						ICPPASTFieldReference getterReference = new CPPASTFieldReference(getterMethodName,
								getterReceiver);

						ICPPASTFunctionCallExpression getterCall = new CPPASTFunctionCallExpression();
						getterCall.setFunctionNameExpression(getterReference);

						ICPPASTBinaryExpression setterArgument = new CPPASTBinaryExpression();
						setterArgument.setOperand1(getterCall);
						setterArgument.setOperand2(new CPPASTLiteralExpression(
								CPPASTLiteralExpression.lk_integer_constant, new char[] { '1' }));

						int op = unExp.getOperator();
						boolean operatorOk = false;
						if (op == IASTUnaryExpression.op_prefixIncr
								|| op == IASTUnaryExpression.op_postFixIncr) {
							setterArgument.setOperator(ICPPASTBinaryExpression.op_plus);
							operatorOk = true;
						} else if (op == IASTUnaryExpression.op_prefixDecr
								|| op == IASTUnaryExpression.op_postFixDecr) {
							setterArgument.setOperator(ICPPASTBinaryExpression.op_minus);
							operatorOk = true;
						}
						setterCall.setArguments(new IASTInitializerClause[] { setterArgument });
						if (operatorOk) {
							rewrite.replace(unExp, setterCall, editGroup);
						}
					}
				} else {
					ICPPASTExpression receiver = (ICPPASTExpression) ((ICPPASTFieldReference) cursor
							.getParent()).getFieldOwner().copy(CopyStyle.withoutLocations);

					ICPPASTName calledMethod = new CPPASTName(getterName.toCharArray());
					ICPPASTFieldReference newReference = new CPPASTFieldReference(calledMethod, receiver);

					ICPPASTFunctionCallExpression call = new CPPASTFunctionCallExpression();
					call.setFunctionNameExpression(newReference);

					rewrite.replace(cursor.getParent(), call, editGroup);
				}
			} else {

				if (cursor.getParent().getParent() instanceof ICPPASTBinaryExpression) {
					ICPPASTBinaryExpression binExp = (ICPPASTBinaryExpression) cursor.getParent().getParent();
					if (binExp.getOperator() == ICPPASTBinaryExpression.op_assign) {
						/*
						 * We have an assignment, ready the setter
						 */
						ICPPASTExpression receiver = (ICPPASTExpression) ((ICPPASTFieldReference) cursor
								.getParent()).getFieldOwner().copy(CopyStyle.withoutLocations);

						ICPPASTName calledMethod = new CPPASTName(setterName.toCharArray());
						ICPPASTFieldReference newReference = new CPPASTFieldReference(calledMethod, receiver);

						ICPPASTFunctionCallExpression call = new CPPASTFunctionCallExpression();
						call.setFunctionNameExpression(newReference);

						IASTExpression expr = assembleSetterArgument(binExp.getOperand2(),
								(ICPPASTFieldReference) cursor.getParent());
						call.setArguments(new IASTInitializerClause[] { expr });

						rewrite.replace(binExp, call, editGroup);
					}
				}

			}
		}
	}

	/**
	 * Generates an IASTExpression with all field accesses replaced with getters.
	 *
	 * @param rhs
	 *            the right hand side of an assignment
	 * @return an IASTExpression to be used as an argument for a setter call
	 */
	private IASTExpression assembleSetterArgument(IASTExpression rhs,
			ICPPASTFieldReference originalFieldReference) {
		if (rhs instanceof IASTIdExpression || rhs instanceof IASTLiteralExpression) {
			/*
			 * We just have a variable, an expansion of a simple macro, or a literal value (macro expansion is
			 * covered under literal)
			 */
			return rhs.copy(CopyStyle.withoutLocations);
		}

		if (rhs instanceof ICPPASTFieldReference) {
			/*
			 * We have a field reference, check whether it is a reference to the same field we already
			 * referenced before
			 */
			ICPPASTFieldReference rhsFieldReference = (ICPPASTFieldReference) rhs;
			if (rhsFieldReference.getFieldOwnerType().isSameType(originalFieldReference.getFieldOwnerType())) {
				if (rhsFieldReference.getFieldName().toString()
						.equals(originalFieldReference.getFieldName().toString())) {

					ICPPASTExpression receiver = (ICPPASTExpression) rhsFieldReference.getFieldOwner().copy(
							CopyStyle.withoutLocations);

					ICPPASTName calledMethod = new CPPASTName(getterName.toCharArray());
					ICPPASTFieldReference newReference = new CPPASTFieldReference(calledMethod, receiver);

					ICPPASTFunctionCallExpression call = new CPPASTFunctionCallExpression();
					call.setFunctionNameExpression(newReference);
					return call;
				}
			}

			/*
			 * This is some other field reference (other field of same class, field of other class, member
			 * function call etc)
			 */
			return rhs;
		}

		/*
		 * Expression is a binary expression, recurse on both operands and assemble.
		 */
		if (rhs instanceof ICPPASTBinaryExpression) {
			ICPPASTBinaryExpression binExp = (ICPPASTBinaryExpression) rhs;
			return new CPPASTBinaryExpression(binExp.getOperator(), assembleSetterArgument(
					binExp.getOperand1(), originalFieldReference).copy(CopyStyle.withoutLocations),
					assembleSetterArgument(binExp.getOperand2(), originalFieldReference).copy(
							CopyStyle.withoutLocations));
		}

		/*
		 * Expression is a unary expression, recurse on operand and assemble.
		 */
		if (rhs instanceof ICPPASTUnaryExpression) {
			ICPPASTUnaryExpression unExp = (ICPPASTUnaryExpression) rhs;
			return new CPPASTUnaryExpression(unExp.getOperator(), assembleSetterArgument(unExp.getOperand(),
					originalFieldReference).copy(CopyStyle.withoutLocations));
		}

		/*
		 * Expression is a conditional expression, like X ? Y : Z. Recurse on all three expressions and
		 * assemble.
		 */
		if (rhs instanceof IASTConditionalExpression) {
			IASTConditionalExpression condExp = (IASTConditionalExpression) rhs;
			return new CPPASTConditionalExpression(assembleSetterArgument(
					condExp.getLogicalConditionExpression(), originalFieldReference).copy(
					CopyStyle.withoutLocations), assembleSetterArgument(
					condExp.getPositiveResultExpression(), originalFieldReference).copy(
					CopyStyle.withoutLocations), assembleSetterArgument(
					condExp.getNegativeResultExpression(), originalFieldReference).copy(
					CopyStyle.withoutLocations));
		}

		/*
		 * Cannot currently think of other cases that might be here, return the raw expression
		 */
		return rhs;
	}

	/**
	 * Emits cursor and all its transitive parents and emits their role in their parents. Useful to understand
	 * a context.
	 * 
	 * @param cursor
	 *            where to start the bottom-up traversal of the AST
	 */
	private void emitParents(IASTNode cursor) {
		while (cursor != null) {
			System.out.println("  " + cursor.getClass() + " " + cursor.getPropertyInParent()); //$NON-NLS-1$ //$NON-NLS-2$
			cursor = cursor.getParent();
		}
	}

	/**
	 * Replaces a field reference (fieldReferencesInThisAST) in given AST by a getter/setter call. The whole
	 * AST is traversed to locate the position for this rewrite.
	 * 
	 * @param collector
	 *            collector for the changes
	 * @param editGroup
	 *            group of edits shown to the user in the GUI
	 * @param ast
	 *            AST to be rewritten
	 * @param fieldReferencesInThisAST
	 *            a field reference in this AST
	 */
	private void replaceAccessInUnit(final ModificationCollector collector, final TextEditGroup editGroup,
			final IASTTranslationUnit ast, final ReferencesInFile fieldReferencesInThisAST) {

		// print AST for debugging/understanding purposes
		// ASTPrinter.print(ast);

		// visit all given references in the AST using a visitor to locate the node
		// that needs to be rewritten. It is identified by the node offset.
		ast.accept(new ASTVisitor() {
			{
				shouldVisitNames = true;
			}

			@Override
			public int visit(IASTName name) {
				final int nodeOffset = name.getFileLocation().getNodeOffset();
				if (fieldReferencesInThisAST.isRead(nodeOffset)
						|| fieldReferencesInThisAST.isWrite(nodeOffset)) {
					// node found
					replaceAccess(collector, editGroup, ast, name,
							fieldReferencesInThisAST.isRead(nodeOffset),
							fieldReferencesInThisAST.isWrite(nodeOffset));
				}
				return super.visit(name);
			}
		});

	}

	/**
	 * Returns the file location information of given node as a string.
	 * 
	 * @param node
	 *            any AST node
	 * @return file location information
	 */
	private String locationToString(IASTNode node) {
		return locationToString(node.getFileLocation()) + node.getClass().getName() + " "; //$NON-NLS-1$
	}

	/**
	 * Returns the file location information as a string.
	 * 
	 * @param location
	 *            any AST node
	 * @return file location information
	 */
	private String locationToString(final IASTFileLocation location) {
		return location.getFileName() + ":" + location.getStartingLineNumber() + "(" //$NON-NLS-1$ //$NON-NLS-2$
				+ location.getNodeOffset() + ")" + ": "; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Returns the translation unit for filename. File must be source code part of the project.
	 * 
	 * @param filename
	 *            Filename of the translation unit
	 * @return translation unit for filename (may be null)
	 */
	private ITranslationUnit getTranslationUnit(String filename) {
		IPath path = new Path(filename);
		try {
			// only this seem to work reliably
			return CoreModelUtil.findTranslationUnitForLocation(path, project);
		} catch (CModelException e) {
			e.printStackTrace();
		}

		// try alternatives to obtain the translation unit
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		if (file == null) {
			System.err.println("EncapsulateFieldRefactoring.replaceAccesses(): no file " + filename); //$NON-NLS-1$
			return null;
		} else {
			ITranslationUnit tu = CoreModelUtil.findTranslationUnit(file);
			if (tu == null) {
				tu = CoreModel.getDefault().createTranslationUnitFrom(project, file.getLocation());
			}
			if (tu == null) {
				tu = (ITranslationUnit) CoreModel.getDefault().create(file);
			}
			return tu;
		}
	}

	/**
	 * Represents a reference of a name that denotes a field. It is possible that a reference represents both
	 * a write and a read of the field.
	 *
	 */
	private class FieldReference {
		public FieldReference(int nodeOffset, boolean isRead, boolean isWrite) {
			this.nodeOffset = nodeOffset;
			this.isWrite = isWrite;
			this.isRead = isRead;
		}

		/**
		 * The node offset of the reference within its file.
		 */
		public int nodeOffset;

		/**
		 * True iff reference represents a write to the field.
		 */
		public boolean isWrite;
		/**
		 * True iff reference represents a read to the field.
		 */
		public boolean isRead;
	}

	/**
	 * References to a field in one particular file classified as reads and writes (reading and writing can be
	 * true at the same time).
	 *
	 */
	private class ReferencesInFile {
		public Set<FieldReference> references = new HashSet<FieldReference>();

		public boolean isWrite(int nodeOffset) {
			for (FieldReference reference : references) {
				if (reference.nodeOffset == nodeOffset) {
					return reference.isWrite;
				}
			}
			return false;
		}

		public boolean isRead(int nodeOffset) {
			for (FieldReference reference : references) {
				if (reference.nodeOffset == nodeOffset) {
					return reference.isRead;
				}
			}
			return false;
		}

		public void add(FieldReference reference) {
			references.add(reference);
		}
	}

	/**
	 * Global references to a particular field declaration (in any file).
	 */
	private class References {

		// filename -> set of references within that file
		public Map<String, ReferencesInFile> references = new HashMap<String, ReferencesInFile>();

		public void add(String filename, FieldReference reference) {
			if (references.containsKey(filename)) {
				references.get(filename).add(reference);
			} else {
				ReferencesInFile set = new ReferencesInFile();
				set.add(reference);
				references.put(filename, set);
			}
		}
	}

	/**
	 * Returns all read/write references to field declaration.
	 * 
	 * @return all read/write references to field declaration
	 */
	private References findReferences() {
		References result = new References();

		try {
			// create index
			IIndex index = CCorePlugin.getIndexManager().getIndex(project);
			IIndexName[] names = index.findReferences(fieldName.resolveBinding());
			for (int i = 0; i < names.length; i++) {
				final IIndexName name = names[i];
				final IASTFileLocation fileLocation = name.getFileLocation();
				if (name.isReference()) {
					result.add(fileLocation.getFileName(), new FieldReference(fileLocation.getNodeOffset(),
							name.isReadAccess(), name.isWriteAccess()));
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Returns all names marked by the user in the Eclipse editor.
	 * 
	 * @return all names marked by the user in the Eclipse editor
	 * @throws OperationCanceledException
	 *             in case the operation is canceled by the user
	 * @throws CoreException
	 *             in case of CDT problems
	 */
	private List<IASTName> findAllMarkedNames() throws OperationCanceledException, CoreException {
		return super.findAllMarkedNames(getAST(tu, null));
	}
}
