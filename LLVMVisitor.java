import syntaxtree.*;
import visitor.GJDepthFirst;

import java.util.HashMap;


public class LLVMVisitor extends GJDepthFirst<Object, Object> {
    private SymbolTable st;
    private ContextManager cm = new ContextManager();
    private RegisterManager rm = new RegisterManager();

    public LLVMVisitor(SymbolTable st){
        this.st = st;

        System.out.print(
            "declare i8* @calloc(i32, i32)\n"+
            "declare i32 @printf(i8*, ...)\n"+
            "declare void @exit(i32)\n\n"+

            "@_cint = constant [4 x i8] c\"%d\\0a\\00\"\n"+
            "@_cOOB = constant [15 x i8] c\"Out of bounds\\0a\\00\"\n"+
            "define void @print_int(i32 %i) {\n"+
                "\t%_str = bitcast [4 x i8]* @_cint to i8*\n"+
                "\tcall i32 (i8*, ...) @printf(i8* %_str, i32 %i)\n"+
                "\tret void\n"+
            "}\n\n"+

            "define void @throw_oob() {\n"+
                "\t%_str = bitcast [15 x i8]* @_cOOB to i8*\n"+
                "\tcall i32 (i8*, ...) @printf(i8* %_str)\n"+
                "\tcall void @exit(i32 1)\n"+
                "\tret void\n"+
            "}\n\n"
        );
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    @Override
    public Object visit(MainClass n, Object argu) throws Exception {
        n.f0.accept(this, argu);
        String mainClassName = getNameFromPair(n.f1.accept(this, argu));
        Class mainClass = st.getClassTable().get(mainClassName);
        cm.enterContext(mainClass);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        Method main = mainClass.getMethods().get("main");
        cm.enterContext(main);

        System.out.println("define i32 @main()\n{");

        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        n.f10.accept(this, argu);
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);
        n.f13.accept(this, argu);
        n.f14.accept(this, argu);
        n.f15.accept(this, argu);
        n.f16.accept(this, argu);

        System.out.println("\tret i32 0\n}\n");

        cm.leaveContext();
        rm.reset();

        cm.leaveContext();
        n.f17.accept(this, argu);
        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    @Override
    public Object visit(ClassDeclaration n, Object argu) throws Exception {
        n.f0.accept(this, argu);
        String className = getNameFromPair(n.f1.accept(this, argu));
        Class c = st.getClassTable().get(className);
        cm.enterContext(c);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        cm.leaveContext();
        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    @Override
    public Object visit(ClassExtendsDeclaration n, Object argu) throws Exception {
        n.f0.accept(this, argu);
        String className = getNameFromPair(n.f1.accept(this, argu));
        Class c = st.getClassTable().get(className);
        cm.enterContext(c);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        cm.leaveContext();
        return null;
    }

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    @Override
    public Object visit(MethodDeclaration n, Object argu) throws Exception {
        Object _ret=null;

        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String methodName = getNameFromPair(n.f2.accept(this, argu));
        Class c = cm.getClassCtx();
        Method m = c.getMethods().get(methodName);
        cm.enterContext(m);

        m.setLLVM_method_head();
        System.out.println(m.getLLVM_method_head());
        System.out.println("{");

        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        TypeRegisterPair typeRegisterPair = (TypeRegisterPair) n.f10.accept(this, argu);
        TypeRegisterPair loaded = rm.loadRegister(typeRegisterPair);
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);

        System.out.println("\tret " + loaded.getType() + " " + loaded.getRegister());
        System.out.println("}\n");
        cm.leaveContext();
        rm.reset();

        return _ret;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    @Override
    public Object visit(FormalParameterList n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    @Override
    public Object visit(FormalParameter n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    @Override
    public Object visit(VarDeclaration n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> ( FormalParameterTerm() )*
     */
    @Override
    public Object visit(FormalParameterTail n, Object argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    @Override
    public Object visit(FormalParameterTerm n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */
    @Override
    public Object visit(Type n, Object argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> BooleanArrayType()
     *       | IntegerArrayType()
     */
    @Override
    public Object visit(ArrayType n, Object argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "boolean"
     * f1 -> "["
     * f2 -> "]"
     */
    @Override
    public Object visit(BooleanArrayType n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    @Override
    public Object visit(IntegerArrayType n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "boolean"
     */
    @Override
    public Object visit(BooleanType n, Object argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "int"
     */
    @Override
    public Object visit(IntegerType n, Object argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> Block()
     *       | AssignmentStatement()
     *       | ArrayAssignmentStatement()
     *       | IfStatement()
     *       | WhileStatement()
     *       | PrintStatement()
     */
    @Override
    public Object visit(Statement n, Object argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    @Override
    public Object visit(Block n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    @Override
    public Object visit(AssignmentStatement n, Object argu) throws Exception {
        TypeRegisterPair left = (TypeRegisterPair) n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        TypeRegisterPair right = (TypeRegisterPair) n.f2.accept(this, argu);
        rm.storeInRegister(left, right);
        n.f3.accept(this, argu);
        return null;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Expression()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression()
     * f6 -> ";"
     */
    @Override
    public Object visit(ArrayAssignmentStatement n, Object argu) throws Exception {
        TypeRegisterPair arr = (TypeRegisterPair) n.f0.accept(this, argu);
        TypeRegisterPair idx = (TypeRegisterPair) n.f2.accept(this, argu);
        TypeRegisterPair arrayElement = rm.getArrayElement(arr, idx);
        TypeRegisterPair expr = (TypeRegisterPair) n.f5.accept(this, argu);
        rm.storeInRegister(arrayElement, expr);
        return null;
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    @Override
    public Object visit(IfStatement n, Object argu) throws Exception {
        TypeRegisterPair condition = (TypeRegisterPair) n.f2.accept(this, argu);
        TypeRegisterPair label1 = new TypeRegisterPair();
        TypeRegisterPair label2 = new TypeRegisterPair();
        TypeRegisterPair exit = rm.ifStatement(condition, label1, label2);

        System.out.println(label1.getRegister().substring(1) + ":");
        n.f4.accept(this, argu);
        System.out.println("\tbr label " + exit.getRegister() + "\n");

        System.out.println(label2.getRegister().substring(1) + ":");
        n.f6.accept(this, argu);
        System.out.println("\tbr label " + exit.getRegister() + "\n");

        System.out.println(exit.getRegister().substring(1) + ":");

        return null;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    @Override
    public Object visit(WhileStatement n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    @Override
    public Object visit(PrintStatement n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        TypeRegisterPair typeRegisterPair = (TypeRegisterPair) n.f2.accept(this, argu);
        rm.print(typeRegisterPair);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> AndExpression()
     *       | CompareExpression()
     *       | PlusExpression()
     *       | MinusExpression()
     *       | TimesExpression()
     *       | ArrayLookup()
     *       | ArrayLength()
     *       | MessageSend()
     *       | Clause()
     */
    @Override
    public Object visit(Expression n, Object argu) throws Exception {
        TypeRegisterPair typeRegisterPair = (TypeRegisterPair) n.f0.accept(this, argu);
        return typeRegisterPair;
    }

    /**
     * f0 -> Clause()
     * f1 -> "&&"
     * f2 -> Clause()
     */
    @Override
    public Object visit(AndExpression n, Object argu) throws Exception {
        TypeRegisterPair left = (TypeRegisterPair) n.f0.accept(this, argu);
        TypeRegisterPair right = (TypeRegisterPair) n.f2.accept(this, argu);
        return rm.and(left, right);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    @Override
    public Object visit(CompareExpression n, Object argu) throws Exception {
        TypeRegisterPair left = (TypeRegisterPair) n.f0.accept(this, argu);
        TypeRegisterPair right = (TypeRegisterPair) n.f2.accept(this, argu);
        return rm.lessThan(left, right);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    @Override
    public Object visit(PlusExpression n, Object argu) throws Exception {
        TypeRegisterPair left = (TypeRegisterPair) n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        TypeRegisterPair right = (TypeRegisterPair) n.f2.accept(this, argu);
        return rm.addRegisters(left, right);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    @Override
    public Object visit(MinusExpression n, Object argu) throws Exception {
        TypeRegisterPair left = (TypeRegisterPair) n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        TypeRegisterPair right = (TypeRegisterPair) n.f2.accept(this, argu);
        return rm.subRegisters(left, right);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    @Override
    public Object visit(TimesExpression n, Object argu) throws Exception {
        TypeRegisterPair left = (TypeRegisterPair) n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        TypeRegisterPair right = (TypeRegisterPair) n.f2.accept(this, argu);
        return rm.mulRegisters(left, right);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    @Override
    public Object visit(ArrayLookup n, Object argu) throws Exception {
        TypeRegisterPair arr = (TypeRegisterPair) n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        TypeRegisterPair idx = (TypeRegisterPair) n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return rm.getArrayElement(arr, idx);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    @Override
    public Object visit(ArrayLength n, Object argu) throws Exception {
        TypeRegisterPair arr = (TypeRegisterPair) n.f0.accept(this, argu);
        return rm.getArrayLength(arr);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    @Override
    public Object visit(MessageSend n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Expression()
     * f1 -> ExpressionTail()
     */
    @Override
    public Object visit(ExpressionList n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> ( ExpressionTerm() )*
     */
    @Override
    public Object visit(ExpressionTail n, Object argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    @Override
    public Object visit(ExpressionTerm n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> NotExpression()
     *       | PrimaryExpression()
     */
    @Override
    public Object visit(Clause n, Object argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> IntegerLiteral()
     *       | TrueLiteral()
     *       | FalseLiteral()
     *       | Identifier()
     *       | ThisExpression()
     *       | ArrayAllocationExpression()
     *       | AllocationExpression()
     *       | BracketExpression()
     */
    @Override
    public Object visit(PrimaryExpression n, Object argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    @Override
    public Object visit(IntegerLiteral n, Object argu) throws Exception {
        String val = n.f0.toString();
        return rm.allocateRegisterWithValue("i32", val);
    }

    /**
     * f0 -> "true"
     */
    @Override
    public Object visit(TrueLiteral n, Object argu) throws Exception {
        return rm.allocateRegisterWithValue("i1", "true");
    }

    /**
     * f0 -> "false"
     */
    @Override
    public Object visit(FalseLiteral n, Object argu) throws Exception {
        return rm.allocateRegisterWithValue("i1", "false");
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    @Override
    public TypeRegisterPair visit(Identifier n, Object argu) throws Exception {
        String id = n.f0.toString();
        Class classContext = cm.getClassCtx();
        Method methodContext = cm.getMethodCtx();

        TypeRegisterPair reg = rm.getRegisterFromID(id);
        if(reg != null){
            return reg;
        }

        if(methodContext != null) {
            // Check if identifier is a method local variable or argument
            HashMap<String, String> localVars = methodContext.getLocalVariableTypes();
            HashMap<String, String> args = methodContext.getArgumentTypes();
            if (localVars.containsKey(id)) return rm.allocateRegister(id, localVars.get(id));
//            if (args.containsKey(id)) return rm.newRegister(args.get(id));
            return null;
        }
        if(classContext != null){
//            // Check if identifier is a class field
//            HashMap<String, String> classFields = classContext.getFields();
//            if(classFields.containsKey(id)) return rm.newRegister(classFields.get(id));
//
//            // Check if identifier is a parent class field
//            Class parent = classContext.getParent();
//            while(parent != null){
//                HashMap<String, String> parentClassFields = parent.getFields();
//                if(parentClassFields.containsKey(id)) return rm.newRegister(parentClassFields.get(id));
//                parent = parent.getParent();
//            }
        }
        // Check if identifier is a class or method
        if(st.getClassTable().containsKey(id)) return new TypeRegisterPair("class", id);
        for(Class c : st.getClassTable().values()){
            if(c.getMethods().containsKey(id)) return new TypeRegisterPair("method", id);
        }
        // Identifier was not declared
        throw new MiniJavaException("No declared identifier " + id);
    }

    /**
     * f0 -> "this"
     */
    @Override
    public Object visit(ThisExpression n, Object argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> BooleanArrayAllocationExpression()
     *       | IntegerArrayAllocationExpression()
     */
    @Override
    public Object visit(ArrayAllocationExpression n, Object argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "new"
     * f1 -> "boolean"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    @Override
    public Object visit(BooleanArrayAllocationExpression n, Object argu) throws Exception {
        TypeRegisterPair size = (TypeRegisterPair) n.f3.accept(this, argu);
        return rm.calloc("i1", size);
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    @Override
    public Object visit(IntegerArrayAllocationExpression n, Object argu) throws Exception {
        TypeRegisterPair size = (TypeRegisterPair) n.f3.accept(this, argu);
        return rm.calloc("i32", size);
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    @Override
    public Object visit(AllocationExpression n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "!"
     * f1 -> Clause()
     */
    @Override
    public Object visit(NotExpression n, Object argu) throws Exception {
        TypeRegisterPair typeRegisterPair = (TypeRegisterPair) n.f1.accept(this, argu);
        return rm.xorRegister(typeRegisterPair);
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    @Override
    public Object visit(BracketExpression n, Object argu) throws Exception {
        return n.f1.accept(this, argu);
    }

    private String getNameFromPair(Object o) {
        // Used for class and method identifiers only
        try {
            TypeRegisterPair pair = (TypeRegisterPair) o;
            return pair.getRegister();
        }catch(Exception e){
            return "";
        }
    }

}
