
            ----------------------------------
                    Compilers Project 2
                            ---
            MiniJava Storage and Type Checking
                            ---
            by  Pavlos Smith - sdi1800181
            ----------------------------------


                        Instructions
                      ----------------

Compilation:
> make

Execution:
> java -classpath out/production/Compilers2/ Main [inputFiles]

Cleanup:
> make clean


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


                      Explanation
                     -------------

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
