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

	// Foo.cpp
	// struct Foo {
	// /*$*/int bar /*$$*/;
	// };
	//
	// int main() {
	// Foo foo;
	// foo.bar++;
	// }
	// ====================
	// struct Foo {
	// inline int getBar() const {
	// return bar;
	// }
	//
	// inline void setBar(int bar) {
	// this->bar = bar;
	// }
	//
	// private:
	// int bar;
	// };
	//
	// int main() {
	// Foo foo;
	// foo.setBar(foo.getBar() + 1);
	// }
	public void testEncapsulatePostfixIncrement() throws Exception {
		assertRefactoringSuccess();
	}

	// Foo.cpp
	// struct Foo {
	// /*$*/int bar /*$$*/;
	// };
	//
	// int main() {
	// Foo foo;
	// ++foo.bar;
	// }
	// ====================
	// struct Foo {
	// inline int getBar() const {
	// return bar;
	// }
	//
	// inline void setBar(int bar) {
	// this->bar = bar;
	// }
	//
	// private:
	// int bar;
	// };
	//
	// int main() {
	// Foo foo;
	// foo.setBar(foo.getBar() + 1);
	// }
	public void testEncapsulatePrefixIncrement() throws Exception {
		assertRefactoringSuccess();
	}

	// Foo.cpp
	// struct Foo {
	// /*$*/int bar /*$$*/;
	// };
	//
	// int main() {
	// Foo foo;
	// --foo.bar;
	// }
	// ====================
	// struct Foo {
	// inline int getBar() const {
	// return bar;
	// }
	//
	// inline void setBar(int bar) {
	// this->bar = bar;
	// }
	//
	// private:
	// int bar;
	// };
	//
	// int main() {
	// Foo foo;
	// foo.setBar(foo.getBar() - 1);
	// }
	public void testEncapsulatePrefixDecrement() throws Exception {
		assertRefactoringSuccess();
	}

	// Foo.cpp
	// struct Foo {
	// /*$*/int bar /*$$*/;
	// };
	//
	// int main() {
	// Foo foo;
	// foo.bar--;
	// }
	// ====================
	// struct Foo {
	// inline int getBar() const {
	// return bar;
	// }
	//
	// inline void setBar(int bar) {
	// this->bar = bar;
	// }
	//
	// private:
	// int bar;
	// };
	//
	// int main() {
	// Foo foo;
	// foo.setBar(foo.getBar() - 1);
	// }
	public void testEncapsulatePostfixDecrement() throws Exception {
		assertRefactoringSuccess();
	}

	// Foo.cpp
	// struct Foo {
	// /*$*/int bar /*$$*/;
	// };
	//
	// int main() {
	// Foo foo;
	// foo.bar = foo.bar--;
	// }
	public void testRightHandSideWriteExpression() throws Exception {
		assertRefactoringFailure();
	}

	// Foo.cpp
	// struct Foo {
	// /*$*/int bar /*$$*/;
	// };
	//
	// int main() {
	// Foo foo;
	// int a = foo.bar;
	// }
	// ====================
	// struct Foo {
	// inline int getBar() const {
	// return bar;
	// }
	//
	// inline void setBar(int bar) {
	// this->bar = bar;
	// }
	//
	// private:
	// int bar;
	// };
	//
	// int main() {
	// Foo foo;
	// int a = foo.getBar();
	// }
	public void testSimpleGet() throws Exception {
		assertRefactoringSuccess();
	}

	// Foo.cpp
	// struct Foo {
	// /*$*/int bar /*$$*/;
	// };
	//
	// int main() {
	// Foo foo;
	// foo.bar = 1;
	// }
	// ====================
	// struct Foo {
	// inline int getBar() const {
	// return bar;
	// }
	//
	// inline void setBar(int bar) {
	// this->bar = bar;
	// }
	//
	// private:
	// int bar;
	// };
	//
	// int main() {
	// Foo foo;
	// foo.setBar(1);
	// }
	public void testSimpleSet() throws Exception {
		assertRefactoringSuccess();
	}

	// Foo.cpp
	// struct Foo {
	// /*$*/int bar /*$$*/;
	// };
	//
	// int main() {
	// Foo foo;
	// foo.bar = 1 + foo.bar * 2;
	// }
	// ====================
	// struct Foo {
	// inline int getBar() const {
	// return bar;
	// }
	//
	// inline void setBar(int bar) {
	// this->bar = bar;
	// }
	//
	// private:
	// int bar;
	// };
	//
	// int main() {
	// Foo foo;
	// foo.setBar(1 + foo.getBar() * 2);
	// }
	public void testComplexSetWithGets() throws Exception {
		assertRefactoringSuccess();
	}

	// Foo.cpp
	// struct Foo {
	// /*$*/int bar /*$$*/;
	// };
	// void test(int param) {}
	// int main() {
	// Foo foo;
	// test(foo.bar++);
	// }
	public void testWriteWithFunctionCall() throws Exception {
		assertRefactoringFailure();
	}

	// Foo.cpp
	// struct Foo {
	// /*$*/int bar /*$$*/;
	// };
	// void test(int param) {}
	// int main() {
	// Foo foo;
	// int foo[1] = { foo.bar++ };
	// }
	public void testWriteWithArrayInitializer() throws Exception {
		assertRefactoringFailure();
	}
}
