# MiniJava Static Checking Semantic Analysis

In this project I implement a part of the compiler for MiniJava, a subset of Java. 

MiniJava is designed so that its programs can be compiled by a full Java compiler like javac.

Here is a partial, textual description of the language. Much of it can be safely ignored (most things are well defined in the grammar or derived from the requirement that each MiniJava program is also a Java program):
    MiniJava is fully object-oriented, like Java. It does not allow global functions, only classes, fields and methods. The basic types are int, boolean, and int [] which is an array of int. One can build classes that contain fields of these basic types or of other classes. Classes contain methods with arguments of basic or class types, etc.
    MiniJava supports single inheritance but not interfaces. It does not support function overloading, which means that each method name must be unique. In addition, all methods are inherently polymorphic (i.e., “virtual” in C++ terminology). Also all methods must have a return type–there are no void methods. Fields in the base and derived class are allowed to have the same names, and are essentially different fields.
    All MiniJava methods are “public” and all fields “protected”. A class method cannot access fields of another class, with the exception of its superclasses. Methods are visible, however. A class’s own methods can be called via “this”. Local variables are defined only at the beginning of a method. A name cannot be repeated in local variables (of the same method) and cannot be repeated in fields (of the same class). A local variable x shadows a field x of the surrounding class.
    In MiniJava, constructors and destructors are not defined. The new operator calls a default void constructor. In addition, there are no inner classes and there are no static methods or fields. By exception, the pseudo-static method “main” is handled specially in the grammar. A MiniJava program is a file that begins with a special class that contains the main method and specific arguments that are not used. The special class has no fields. After it, other classes are defined that can have fields and methods.
    Notably, an A class can contain a field of type B, where B is defined later in the file. But when we have “class B extends A”, A must be defined before B. As one can notice in the grammar, MiniJava offers very simple ways to construct expressions and only allows < comparisons. There are no lists of operations, but a method call on one object may be used as an argument for another method call. In terms of logical operators, MiniJava allows the logical and (“&&”) and the logical not (“!”). For int arrays, the assignment and [] operators are allowed, as well as the a.length expression, which returns the size of array a. We have “while” and “if” code blocks. The latter are always followed by an “else”. Finally, the assignment “A a = new B();” when B extends A is correct, and the same applies when a method expects a parameter of type A and a B instance is given instead
The compiler only accepts expressions of type int as the argument of the PrintStatement.


I implement two visitors that take control over the MiniJava input file and tell whether it is semantically correct, or print an error message. 

Also, for every MiniJava file, my program stores and print some useful data for every class such as the names and the offsets of every field and method this class contains. For MiniJava we have only three types of fields (int, boolean and pointers). Ints are stored in 4 bytes, booleans in 1 byte and pointers in 8 bytes (we consider functions and int arrays as pointers).

The application can be compiled as follows:

    make clean all
    
The application can be run as follows:

    java Main [file1] [file2] ... [fileN] , where the [file ] argument can be either a directory or a simple file (in case it's a directory all the java files in it and any subdirectories of it will be semantically checked)
