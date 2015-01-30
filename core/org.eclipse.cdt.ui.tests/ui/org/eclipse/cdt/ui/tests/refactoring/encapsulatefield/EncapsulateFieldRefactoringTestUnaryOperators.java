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

	// A.h
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A() {
	// foo++;
	// }
	//
	// virtual ~A() {
	// }
	//
	// /*$*/int foo = 0;/*$$*/
	// };
	//
	// #endif /*A_H_*/
	// ====================
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A() {
	// setFoo(getFoo() + 1);
	// }
	//
	// virtual ~A() {
	// }
	//
	// int getFoo() const {
	// return foo;
	// }
	//
	// void setFoo(int foo_) {
	// foo = foo_;
	// }
	// private:
	// int foo = 0;
	// };
	//
	// #endif /*A_H_*/
	public void testBasicPostIncrement() throws Exception {
		assertRefactoringSuccess();
	}

	// A.h
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A() {
	// ++foo;
	// }
	//
	// virtual ~A() {
	// }
	//
	// /*$*/int foo = 0;/*$$*/
	// };
	//
	// #endif /*A_H_*/
	// ====================
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A() {
	// setFoo(getFoo() + 1);
	// }
	//
	// virtual ~A() {
	// }
	//
	// int getFoo() const {
	// return foo;
	// }
	//
	// void setFoo(int foo_) {
	// foo = foo_;
	// }
	// private:
	// int foo = 0;
	// };
	//
	// #endif /*A_H_*/
	public void testBasicPreIncrement() throws Exception {
		assertRefactoringSuccess();
	}

	// A.h
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A() {
	// foo--;
	// }
	//
	// virtual ~A() {
	// }
	//
	// /*$*/int foo = 0;/*$$*/
	// };
	//
	// #endif /*A_H_*/
	// ====================
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A() {
	// setFoo(getFoo() - 1);
	// }
	//
	// virtual ~A() {
	// }
	//
	// int getFoo() const {
	// return foo;
	// }
	//
	// void setFoo(int foo_) {
	// foo = foo_;
	// }
	// private:
	// int foo = 0;
	// };
	//
	// #endif /*A_H_*/
	public void testBasicPostDecrement() throws Exception {
		assertRefactoringSuccess();
	}

	// A.h
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A() {
	// --foo;
	// }
	//
	// virtual ~A() {
	// }
	//
	// /*$*/int foo = 0;/*$$*/
	// };
	//
	// #endif /*A_H_*/
	// ====================
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A() {
	// setFoo(getFoo() - 1);
	// }
	//
	// virtual ~A() {
	// }
	//
	// int getFoo() const {
	// return foo;
	// }
	//
	// void setFoo(int foo_) {
	// foo = foo_;
	// }
	// private:
	// int foo = 0;
	// };
	//
	// #endif /*A_H_*/
	public void testBasicPreDecrement() throws Exception {
		assertRefactoringSuccess();
	}

	// A.h
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A() {
	// int array[5] = { 1, 2, 3, 4, 5 };
	// int bar = array[foo++];
	// }
	//
	// virtual ~A() {
	// }
	//
	// /*$*/int foo = 2;/*$$*/
	// };
	//
	// #endif /*A_H_*/
	// ====================
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A() {
	// int array[5] = { 1, 2, 3, 4, 5 };
	// int bar = array[getFoo()];
	// setFoo(getFoo() + 1);
	// }
	//
	// virtual ~A() {
	// }
	//
	// int getFoo() const {
	// return foo;
	// }
	//
	// void setFoo(int foo_) {
	// foo = foo_;
	// }
	// private:
	// int foo = 2;
	// };
	//
	// #endif /*A_H_*/
	public void testPostIncrementUsage() throws Exception {
		assertRefactoringSuccess();
	}

	// A.h
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A() {
	// int array[5] = { 1, 2, 3, 4, 5 };
	// int bar = array[++foo];
	// }
	//
	// virtual ~A() {
	// }
	//
	// /*$*/int foo = 2;/*$$*/
	// };
	//
	// #endif /*A_H_*/
	// ====================
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A() {
	// int array[5] = { 1, 2, 3, 4, 5 };
	// setFoo(getFoo() + 1);
	// int bar = array[getFoo()];
	// }
	//
	// virtual ~A() {
	// }
	//
	// int getFoo() const {
	// return foo;
	// }
	//
	// void setFoo(int foo_) {
	// foo = foo_;
	// }
	// private:
	// int foo = 2;
	// };
	//
	// #endif /*A_H_*/
	public void testPreIncrementUsage() throws Exception {
		assertRefactoringSuccess();
	}

	// A.h
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A() {
	// int array[5] = { 1, 2, 3, 4, 5 };
	// int bar = array[foo--];
	// }
	//
	// virtual ~A() {
	// }
	//
	// /*$*/int foo = 2;/*$$*/
	// };
	//
	// #endif /*A_H_*/
	// ====================
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A() {
	// int array[5] = { 1, 2, 3, 4, 5 };
	// int bar = array[getFoo()];
	// setFoo(getFoo() - 1);
	// }
	//
	// virtual ~A() {
	// }
	//
	// int getFoo() const {
	// return foo;
	// }
	//
	// void setFoo(int foo_) {
	// foo = foo_;
	// }
	// private:
	// int foo = 2;
	// };
	//
	// #endif /*A_H_*/
	public void testPostDecrementUsage() throws Exception {
		assertRefactoringSuccess();
	}

	// A.h
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A() {
	// int array[5] = { 1, 2, 3, 4, 5 };
	// int bar = array[--foo];
	// }
	//
	// virtual ~A() {
	// }
	//
	// /*$*/int foo = 2;/*$$*/
	// };
	//
	// #endif /*A_H_*/
	// ====================
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A() {
	// int array[5] = { 1, 2, 3, 4, 5 };
	// setFoo(getFoo() - 1);
	// int bar = array[getFoo()];
	// }
	//
	// virtual ~A() {
	// }
	//
	// int getFoo() const {
	// return foo;
	// }
	//
	// void setFoo(int foo_) {
	// foo = foo_;
	// }
	// private:
	// int foo = 2;
	// };
	//
	// #endif /*A_H_*/
	public void testPreDecrementUsage() throws Exception {
		assertRefactoringSuccess();
	}

	// A.h
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A() {
	// int bar = foo++ + foo++;
	// }
	//
	// virtual ~A() {
	// }
	//
	// /*$*/int foo = 0;/*$$*/
	// };
	//
	// #endif /*A_H_*/
	public void testUnsequencedPostIncrement() throws Exception {
		assertRefactoringFailure();
	}

	// A.h
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A() {
	// int bar = ++foo + ++foo;
	// }
	//
	// virtual ~A() {
	// }
	//
	// /*$*/int foo = 0;/*$$*/
	// };
	//
	// #endif /*A_H_*/
	public void testUnsequencedPreIncrement() throws Exception {
		assertRefactoringFailure();
	}

	// A.h
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A() {
	// int bar = foo-- + foo--;
	// }
	//
	// virtual ~A() {
	// }
	//
	// /*$*/int foo = 0;/*$$*/
	// };
	//
	// #endif /*A_H_*/
	public void testUnsequencedPostDecrement() throws Exception {
		assertRefactoringFailure();
	}

	// A.h
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A() {
	// int bar = --foo + --foo;
	// }
	//
	// virtual ~A() {
	// }
	//
	// /*$*/int foo = 0;/*$$*/
	// };
	//
	// #endif /*A_H_*/
	public void testUnsequencedPreDecrement() throws Exception {
		assertRefactoringFailure();
	}
}
