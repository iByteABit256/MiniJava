
     --------------------------------------------------------
                    Compilers Project 2 & 3
                            ---
            Project2: MiniJava Storage and Type Checking
            Project3: MiniJava to LLVM compiler
                            ---
                by  Pavlos Smith - sdi1800181
     --------------------------------------------------------


                        Instructions
                      ----------------

Compilation:
> make

Execution:
> java -classpath out/production/MiniJava/ Main [-st|-llvm|-o=<OUTPUT_FILE>] [inputFiles]

Flags:
-st: shows symbol table output
-llvm: compiles to llvm
-o: output file to store produced llvm file in
    (Output is printed in standard output if flag is absent)

Examples:
> java -classpath out/production/MiniJava/ Main -st myInputFile.java
> java -classpath out/production/MiniJava/ Main -llvm -o=outputs/myInputFile.ll myInputFile.java
> java -classpath out/production/MiniJava/ Main -llvm myInputFile.java

Cleanup:
> make clean

Notes:
The .jar files for the javacc and jtb tools must
be in a ./lib directory.



                      Explanation
                     -------------

Project 3
----------


    Notes
   -------

- Registers are named like '%_i' where 'i' is the current
register number in the Register Manager. Exceptions to this are
method arguments and the 'this' keyword, since they are named
as '%argName' and '%this' respectively.

- Labels used for if statements and while loops are called
'label<i>' where '<i>' is the current register number.

- On integer and boolean arrays, 4 extra bytes are allocated
in order to store a length integer in them for use with the
'.length' operator.


    Class (continued)
   -------------------

Stores its VTable representation and calls the VTable setters on Method.


    DatatypeMapper (continued)
   ----------------------------

Returns default value according to LLVM type.


    Emitter
   ---------

Imitates some PrintStream methods to use with Writer.


    InputParser
   -------------

Reads command line input and parses the flags and arguments.


    LLVMVisitor
   -------------

Visits all the nodes of the parse tree and generates the
relevant LLVM code for each one.


    Method (continued)
   --------------------

Stores its VTable representation.


    RegisterManager
   -----------------

Keeps track of the assigned registers and does the
main processing with helper methods for most nodes of
the parse tree.


    SymbolTable (continued)
   -------------------------

Prints the VTable.


    TypeRegisterPair
   ------------------

Stores a <String:Type, String:Register> pair along with
other information that are needed in some instances.
Specifically, it can also store a VTable reference and type,
a method return type, a Method object, sizes, and offsets.



Project 2
----------


                      Checks implemented
                    ----------------------

    Storage checks
   ----------------

 * Same class names
 * Parent class not existing
 * Same method names
 * Same field names
 * Same method argument names
 * Same local variable names
 * Local variable with same name as method argument
 * Overloaded methods are valid

   Type checks
  -------------

 * Array index is of type 'int'
 * length attribute on valid types
 * Valid types for all arithmetic and logical operations
 * Valid condition expressions for while-loops and if-statements
 * Valid assignment type to variable
 * Method after dot operator exists
 * Valid expression for array size declaration
 * Constructor called on declared classes
 * Cannot access index of non-array type
 * Method parameters have right number and type
 * Returned value matches return type
 * Only ints are printable
 * Identifier was declared


    Class
   -------

Representation of MiniJava classes, responsible for
mapping class field names to types, method names to Methods,
managing offset table and class extensions.


    ContextManager
   ----------------

Used in TypeCheckVisitor, keeps the current class/method context.
For instance, if the visitor is currently inside Class A's method foo(),
the ContextManager is responsible for storing Class A and method foo().
It works similarly to a stack, by entering a class context, then a method context,
and leaving those contexts in the reverse order.


    DatatypeMapper
   ----------------

Maps data types to bytes in memory.


    Main
   ------

Reads input files as file streams, parses them once for the symbol table to be
initialized and once again for the type checking to occur.


    Makefile
   ----------

Automates compilation/cleanup process.


    Method
   --------

Representation of MiniJava methods, maps argument types to types and
local variables to types.


    minijava.jj
   -------------

MiniJava grammar.


    MiniJavaException
   -------------------

Used to separate internal errors to errors found by
the Storage and Type Checker.


    SymbolTable
   -------------

Maps class names to Classes, creates output in case
of successful parse.


    SymbolTableVisitor
   --------------------

Responsible for creating the Symbol Table and finding any
storage errors in the meantime.


    TypeCheckVisitor
   ------------------

Responsible for checking for all kinds of type errors.


    TypeIdentifierPair
   --------------------

A pair of datatype and identifier.
