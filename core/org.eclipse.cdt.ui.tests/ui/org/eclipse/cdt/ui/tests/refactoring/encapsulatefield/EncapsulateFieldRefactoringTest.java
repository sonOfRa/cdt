package org.eclipse.cdt.ui.tests.refactoring.encapsulatefield;

public class EncapsulateFieldRefactoringTest extends
		EncapsulateFieldRefactoringTestBase {

	public EncapsulateFieldRefactoringTest() {
		super();
	}

	public EncapsulateFieldRefactoringTest(String name) {
		super(name);
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
	// int getFoo() const {
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
	// inline int getFoo() const {
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
	// inline int getFoo() const {
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
	// inline int getFoo() const {
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
	// inline int getFoo() const {
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
	// inline int getFoo() const {
	// return foo;
	// }
	//
	// inline void setFoo(int foo) {
	// this->foo = foo;
	// }
	// private:
	// int foo = 0;
	// };
	//
	// #endif /*A_H_*/

	// A.cpp
	// #include "A.h"
	// A::A() {
	// foo += 2;
	// }
	//
	// A::~A() {
	// }
	// ====================
	// #include "A.h"
	// A::A() {
	// setFoo(getFoo() + 2);
	// }
	//
	// A::~A() {
	// }
	public void testEncapsulateFieldAddAssign() throws Exception {
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
	// inline int getFoo() const {
	// return foo;
	// }
	//
	// inline void setFoo(int foo) {
	// this->foo = foo;
	// }
	// private:
	// int foo = 0;
	// };
	//
	// #endif /*A_H_*/

	// A.cpp
	// #include "A.h"
	// A::A() {
	// foo -= 2;
	// }
	//
	// A::~A() {
	// }
	// ====================
	// #include "A.h"
	// A::A() {
	// setFoo(getFoo() - 2);
	// }
	//
	// A::~A() {
	// }
	public void testEncapsulateFieldSubtractAssign() throws Exception {
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
	// inline int getFoo() const {
	// return foo;
	// }
	//
	// inline void setFoo(int foo) {
	// this->foo = foo;
	// }
	// private:
	// int foo = 0;
	// };
	//
	// #endif /*A_H_*/

	// A.cpp
	// #include "A.h"
	// A::A() {
	// foo *= 2;
	// }
	//
	// A::~A() {
	// }
	// ====================
	// #include "A.h"
	// A::A() {
	// setFoo(getFoo() * 2);
	// }
	//
	// A::~A() {
	// }
	public void testEncapsulateFieldMultAssign() throws Exception {
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
	// inline int getFoo() const {
	// return foo;
	// }
	//
	// inline void setFoo(int foo) {
	// this->foo = foo;
	// }
	// private:
	// int foo = 0;
	// };
	//
	// #endif /*A_H_*/

	// A.cpp
	// #include "A.h"
	// A::A() {
	// foo /= 2;
	// }
	//
	// A::~A() {
	// }
	// ====================
	// #include "A.h"
	// A::A() {
	// setFoo(getFoo() / 2);
	// }
	//
	// A::~A() {
	// }
	public void testEncapsulateFieldDivAssign() throws Exception {
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
	// inline int getFoo() const {
	// return foo;
	// }
	//
	// inline void setFoo(int foo) {
	// this->foo = foo;
	// }
	// private:
	// int foo = 0;
	// };
	//
	// #endif /*A_H_*/

	// A.cpp
	// #include "A.h"
	// A::A() {
	// foo %= 2;
	// }
	//
	// A::~A() {
	// }
	// ====================
	// #include "A.h"
	// A::A() {
	// setFoo(getFoo() % 2);
	// }
	//
	// A::~A() {
	// }
	public void testEncapsulateFieldModAssign() throws Exception {
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
	// inline int getFoo() const {
	// return foo;
	// }
	//
	// inline void setFoo(int foo) {
	// this->foo = foo;
	// }
	// private:
	// int foo = 0;
	// };
	//
	// #endif /*A_H_*/

	// A.cpp
	// #include "A.h"
	// A::A() {
	// foo &= 2;
	// }
	//
	// A::~A() {
	// }
	// ====================
	// #include "A.h"
	// A::A() {
	// setFoo(getFoo() & 2);
	// }
	//
	// A::~A() {
	// }
	public void testEncapsulateFieldAndAssign() throws Exception {
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
	// inline int getFoo() const {
	// return foo;
	// }
	//
	// inline void setFoo(int foo) {
	// this->foo = foo;
	// }
	// private:
	// int foo = 0;
	// };
	//
	// #endif /*A_H_*/

	// A.cpp
	// #include "A.h"
	// A::A() {
	// foo |= 2;
	// }
	//
	// A::~A() {
	// }
	// ====================
	// #include "A.h"
	// A::A() {
	// setFoo(getFoo() | 2);
	// }
	//
	// A::~A() {
	// }
	public void testEncapsulateFieldOrAssign() throws Exception {
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
	// inline int getFoo() const {
	// return foo;
	// }
	//
	// inline void setFoo(int foo) {
	// this->foo = foo;
	// }
	// private:
	// int foo = 0;
	// };
	//
	// #endif /*A_H_*/

	// A.cpp
	// #include "A.h"
	// A::A() {
	// foo ^= 2;
	// }
	//
	// A::~A() {
	// }
	// ====================
	// #include "A.h"
	// A::A() {
	// setFoo(getFoo() ^ 2);
	// }
	//
	// A::~A() {
	// }
	public void testEncapsulateFieldXorAssign() throws Exception {
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
	// inline int getFoo() const {
	// return foo;
	// }
	//
	// inline void setFoo(int foo) {
	// this->foo = foo;
	// }
	// private:
	// int foo = 0;
	// };
	//
	// #endif /*A_H_*/

	// A.cpp
	// #include "A.h"
	// A::A() {
	// foo <<= 2;
	// }
	//
	// A::~A() {
	// }
	// ====================
	// #include "A.h"
	// A::A() {
	// setFoo(getFoo() << 2);
	// }
	//
	// A::~A() {
	// }
	public void testEncapsulateFieldShiftLAssign() throws Exception {
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
	// inline int getFoo() const {
	// return foo;
	// }
	//
	// inline void setFoo(int foo) {
	// this->foo = foo;
	// }
	// private:
	// int foo = 0;
	// };
	//
	// #endif /*A_H_*/

	// A.cpp
	// #include "A.h"
	// A::A() {
	// foo >>= 2;
	// }
	//
	// A::~A() {
	// }
	// ====================
	// #include "A.h"
	// A::A() {
	// setFoo(getFoo() >> 2);
	// }
	//
	// A::~A() {
	// }
	public void testEncapsulateFieldShiftRAssign() throws Exception {
		assertRefactoringSuccess();
	}
}
