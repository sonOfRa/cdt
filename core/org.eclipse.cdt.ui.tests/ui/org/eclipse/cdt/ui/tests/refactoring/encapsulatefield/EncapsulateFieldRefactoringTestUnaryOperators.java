package org.eclipse.cdt.ui.tests.refactoring.encapsulatefield;

/**
 * Tests for Encapsulating fields that are used with unary operators
 * 
 * @author Simon Levermann
 *
 */
public class EncapsulateFieldRefactoringTestUnaryOperators extends
		EncapsulateFieldRefactoringTestBase {

	public EncapsulateFieldRefactoringTestUnaryOperators() {
		super();
	}

	public EncapsulateFieldRefactoringTestUnaryOperators(String name) {
		super(name);
	}

	public void testBasicPostIncrement() throws Exception {
		assertRefactoringSuccess();
	}

	public void testBasicPreIncrement() throws Exception {
		assertRefactoringSuccess();
	}

	public void testBasicPostDecrement() throws Exception {
		assertRefactoringSuccess();
	}

	public void testBasicPreDecrement() throws Exception {
		assertRefactoringSuccess();
	}

	public void testPostIncrementUsage() throws Exception {
		assertRefactoringSuccess();
	}

	public void testPreIncrementUsage() throws Exception {
		assertRefactoringSuccess();
	}

	public void testPostDecrementUsage() throws Exception {
		assertRefactoringSuccess();
	}

	public void testPreDecrementUsage() throws Exception {
		assertRefactoringSuccess();
	}

	public void testUnsequencedPostIncrement() throws Exception {
		assertRefactoringFailure();
	}

	public void testUnsequencedPreIncrement() throws Exception {
		assertRefactoringFailure();
	}

	public void testUnsequencedPostDecrement() throws Exception {
		assertRefactoringFailure();
	}

	public void testUnsequencedPreDecrement() throws Exception {
		assertRefactoringFailure();
	}
}
