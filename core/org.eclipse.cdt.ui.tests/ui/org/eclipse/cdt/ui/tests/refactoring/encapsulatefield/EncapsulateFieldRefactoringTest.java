package org.eclipse.cdt.ui.tests.refactoring.encapsulatefield;

import junit.framework.Test;

import org.eclipse.ltk.core.refactoring.Refactoring;

import org.eclipse.cdt.ui.tests.refactoring.RefactoringTestBase;

import org.eclipse.cdt.internal.ui.refactoring.encapsulatefield.EncapsulateFieldRefactoring;

public class EncapsulateFieldRefactoringTest extends RefactoringTestBase {

	private EncapsulateFieldRefactoring refactoring;

	public EncapsulateFieldRefactoringTest() {
		super();
	}

	public EncapsulateFieldRefactoringTest(String name) {
		super(name);
	}

	public static Test suite() {
		return suite(EncapsulateFieldRefactoringTest.class);
	}

	@Override
	protected Refactoring createRefactoring() {
		refactoring = new EncapsulateFieldRefactoring(
				getSelectedTranslationUnit(), getSelection(), getCProject());
		return refactoring;
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

	// A.h
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A();
	// virtual ~A();
	// /*$*/int foo()/*$$*/;
	// };
	//
	// #endif /*A_H_*/

	// A.cpp
	// #include "A.h"
	// A::A() {
	// }
	//
	// A::~A() {
	// }
	//
	// int A::foo() {
	// return 1;
	// }
	public void testEncapsulateMethod() throws Exception {
		assertRefactoringFailure();
	}

	// A.h
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A();
	// virtual ~A();
	// /*$*/static int foo()/*$$*/;
	// };
	//
	// #endif /*A_H_*/

	// A.cpp
	// #include "A.h"
	// A::A() {
	// }
	//
	// A::~A() {
	// }
	//
	// int A::foo() {
	// return 1;
	// }
	public void testEncapsulateStaticMethod() throws Exception {
		assertRefactoringFailure();
	}

	// A.h
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A() {
	// foo = 1;
	// int bar = foo;
	// }
	//
	// virtual ~A() {
	// }
	//
	// /*$*/int foo;/*$$*/
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
	// setFoo(1);
	// int bar = getFoo();
	// }
	//
	// virtual ~A() {
	// }
	//
	// int getFoo() {
	// return foo;
	// }
	//
	// void setFoo(int foo_) {
	// foo = foo_;
	// }
	// private:
	// int foo;
	// };
	//
	// #endif /*A_H_*/
	public void testEncapsulateFieldUseInHeaderOnly() throws Exception {
		assertRefactoringSuccess();
	}

	// A.h
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A();
	//
	// virtual ~A();
	//
	// /*$*/int foo;/*$$*/
	// };
	//
	// #endif /*A_H_*/
	// ====================
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A();
	//
	// virtual ~A();
	//
	// inline int getFoo() {
	// return foo;
	// }
	//
	// inline void setFoo(int foo) {
	// this->foo = foo;
	// }
	// private:
	// int foo;
	// };
	//
	// #endif /*A_H_*/

	// A.cpp
	// #include "A.h"
	//
	// A::A() {
	// foo = 1;
	// int bar = foo;
	// }
	//
	// A::~A() {
	// }
	// ====================
	// #include "A.h"
	//
	// A::A() {
	// setFoo(1);
	// int bar = getFoo();
	// }
	//
	// A::~A() {
	// }
	public void testEncapsulateFieldUseInDefinition() throws Exception {
		assertRefactoringSuccess();
	}

	// A.h
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A();
	//
	// virtual ~A();
	//
	// /*$*/int foo;/*$$*/
	// };
	//
	// #endif /*A_H_*/
	// ====================
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A();
	//
	// virtual ~A();
	//
	// inline int getFoo() {
	// return foo;
	// }
	//
	// inline void setFoo(int foo) {
	// this->foo = foo;
	// }
	// private:
	// int foo;
	// };
	//
	// #endif /*A_H_*/

	// A.cpp
	// #include "A.h"
	// A::A() {
	// }
	//
	// A::~A() {
	// }

	// B.h
	// #ifndef B_H_
	// #define B_H_
	//
	// class B {
	// public:
	// B();
	//
	// virtual ~B();
	//
	// };
	//
	// #endif /*B_H_*/

	// B.cpp
	// #include "B.h"
	// #include "A.h"
	//
	// B::B() {
	// A a = A();
	// a.foo = 1;
	// int b = a.foo;
	// }
	//
	// B::~B() {
	// }
	// ====================
	// #include "B.h"
	// #include "A.h"
	//
	// B::B() {
	// A a = A();
	// a.setFoo(1);
	// int b = a.getFoo();
	// }
	//
	// B::~B() {
	// }
	public void testEncapsulateFieldUseInOtherClass() throws Exception {
		assertRefactoringSuccess();
	}

	// A.h
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A();
	//
	// virtual ~A();
	//
	// /*$*/int foo;/*$$*/
	// };
	//
	// #endif /*A_H_*/
	// ====================
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A();
	//
	// virtual ~A();
	//
	// inline int getFoo() {
	// return foo;
	// }
	//
	// inline void setFoo(int foo) {
	// this->foo = foo;
	// }
	// private:
	// int foo;
	// };
	//
	// #endif /*A_H_*/

	// A.cpp
	// #include "A.h"
	// A::A() {
	// }
	//
	// A::~A() {
	// }

	// B.h
	// #ifndef B_H_
	// #define B_H_
	//
	// #include "A.h"
	//
	// class B : public A {
	// public:
	// B();
	//
	// virtual ~B();
	//
	// };
	//
	// #endif /*B_H_*/

	// B.cpp
	// #include "B.h"
	// #include "A.h"
	//
	// B::B() {
	// A a = A();
	// a.foo = 1;
	// int b = a.foo;
	// }
	//
	// B::~B() {
	// }
	// ====================
	// #include "B.h"
	// #include "A.h"
	//
	// B::B() {
	// A a = A();
	// a.setFoo(1);
	// int b = a.getFoo();
	// }
	//
	// B::~B() {
	// }
	public void testEncapsulateFieldUseInSubClass() throws Exception {
		assertRefactoringSuccess();
	}

	// A.h
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A();
	//
	// virtual ~A();
	//
	// /*$*/int foo;/*$$*/
	// };
	//
	// #endif /*A_H_*/
	// ====================
	// #ifndef A_H_
	// #define A_H_
	//
	// class A {
	// public:
	// A();
	//
	// virtual ~A();
	//
	// inline int getFoo() {
	// return foo;
	// }
	//
	// inline void setFoo(int foo) {
	// this->foo = foo;
	// }
	// private:
	// int foo;
	// };
	//
	// #endif /*A_H_*/

	// A.cpp
	// #include "A.h"
	// A::A() {
	// }
	//
	// A::~A() {
	// }

	// main.cpp
	// #include "A.h"
	//
	// int main() {
	// A a = A();
	// a.foo = 1;
	// int b = a.foo;
	// }
	// ====================
	// #include "A.h"
	//
	// int main() {
	// A a = A();
	// a.setFoo(1);
	// int b = a.getFoo();
	// }
	public void testEncapsulateFieldUseInNoClass() throws Exception {
		assertRefactoringSuccess();
	}

	public void testEncapsulateFieldIncrement() throws Exception {
		fail();
	}

	public void testEncapsulateFieldDecrement() throws Exception {
		fail();
	}

	public void testEncapsulateFieldAddAssign() throws Exception {
		fail();
	}

	public void testEncapsulateFieldSubtractAssign() throws Exception {
		fail();
	}

	public void testEncapsulateFieldMultAssign() throws Exception {
		fail();
	}

	public void testEncapsulateFieldDivAssign() throws Exception {
		fail();
	}

	public void testEncapsulateFieldModAssign() throws Exception {
		fail();
	}

	public void testEncapsulateFieldAndAssign() throws Exception {
		fail();
	}

	public void testEncapsulateFieldOrAssign() throws Exception {
		fail();
	}

	public void testEncapsulateFieldXorAssign() throws Exception {
		fail();
	}

	public void testEncapsulateFieldShiftLAssign() throws Exception {
		fail();
	}

	public void testEncapsulateFieldShiftRAssign() throws Exception {
		fail();
	}
}
