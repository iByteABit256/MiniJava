import syntaxtree.*;
import visitor.GJDepthFirst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class TypeCheckVisitor extends GJDepthFirst<Object, Object> {

    private SymbolTable st;
    private ContextManager cm = new ContextManager();

    public TypeCheckVisitor(SymbolTable st){
        this.st = st;
    }

    /** Checks implemented:
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
     */

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
    public Object visit(MainClass n, Object argu) throws Exception {
        n.f0.accept(this, argu);
        String mainClassName = getNameFromPair(n.f1.accept(this, argu));
        Class mainClass = st.getClassTable().get(mainClassName);
        cm.enterContext(mainClass);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        cm.enterContext(mainClass.getMethods().get("main"));

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
        cm.leaveContext();
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
    public Object visit(MethodDeclaration n, Object argu) throws Exception {
        n.f0.accept(this, argu);
        String returnType = (String) n.f1.accept(this, argu);
        String methodName = getNameFromPair(n.f2.accept(this, argu));
        Method m = cm.getClassCtx().getMethods().get(methodName);
        cm.enterContext(m);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        String type = (String) n.f10.accept(this, argu);
        if(!typesMatch(returnType, type)){
            throw new MiniJavaException("Return type is " + returnType + " but " + type + " was returned");
        }
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);
        cm.leaveContext();
        return null;
    }

    /**
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */
    public Object visit(Type n, Object argu) throws Exception {
        Object o = n.f0.accept(this, argu);
        if(o instanceof TypeIdentifierPair){
            return getTypeFromPair(o);
        }
        return o;
    }

    /**
     * f0 -> BooleanArrayType()
     *       | IntegerArrayType()
     */
    public Object visit(ArrayType n, Object argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "boolean"
     * f1 -> "["
     * f2 -> "]"
     */
    public Object visit(BooleanArrayType n, Object argu) throws Exception {
        return "boolean[]";
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public Object visit(IntegerArrayType n, Object argu) throws Exception {
        return "int[]";
    }

    /**
     * f0 -> "boolean"
     */
    public Object visit(BooleanType n, Object argu) throws Exception {
        return "boolean";
    }

    /**
     * f0 -> "int"
     */
    public Object visit(IntegerType n, Object argu) throws Exception {
        return "int";
    }

    /**
     * f0 -> Block()
     *       | AssignmentStatement()
     *       | ArrayAssignmentStatement()
     *       | IfStatement()
     *       | WhileStatement()
     *       | PrintStatement()
     */
    public Object visit(Statement n, Object argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public Object visit(AssignmentStatement n, Object argu) throws Exception {
        String type = getTypeFromPair(n.f0.accept(this, argu));
        n.f1.accept(this, argu);
        String valueType = (String) n.f2.accept(this, argu);
        if(!typesMatch(type, valueType)){
            throw new MiniJavaException("Cannot assign value of type " + valueType + " to variable of type " + type);
        }
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
    public Object visit(ArrayAssignmentStatement n, Object argu) throws Exception {
        String arrayType = getTypeFromPair(n.f0.accept(this, argu));
        n.f1.accept(this, argu);
        String index_type = (String) n.f2.accept(this, argu);
        if(!index_type.equals("int")){
            throw new MiniJavaException("Invalid index type");
        }
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        String valueType = (String) n.f5.accept(this, argu);
        if(!typesMatch(arrayType.replace("[]", ""), valueType)){
            throw new MiniJavaException("Cannot insert value of type " + valueType + " to array of type " + arrayType);
        }
        n.f6.accept(this, argu);
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
    public Object visit(IfStatement n, Object argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String type = (String) n.f2.accept(this, argu);
        if(!type.equals("boolean")){
            throw new MiniJavaException("Invalid if-statement condition");
        }
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        return null;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public Object visit(WhileStatement n, Object argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String type = (String) n.f2.accept(this, argu);
        if(!type.equals("boolean")){
            throw new MiniJavaException("Invalid while-loop condition");
        }
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return null;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public Object visit(PrintStatement n, Object argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String type = (String) n.f2.accept(this, argu);
        if(!isPrintable(type)){
            throw new MiniJavaException("Type " + type + " is not printable");
        }
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return null;
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
    public Object visit(Expression n, Object argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> Clause()
     * f1 -> "&&"
     * f2 -> Clause()
     */
    public Object visit(AndExpression n, Object argu) throws Exception {
        String type1 = (String) n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String type2 = (String) n.f2.accept(this, argu);
        if(!type1.equals("boolean") || !type2.equals("boolean")){
            throw new MiniJavaException("Invalid types for operation && : " + type1 + " , " + type2);
        }
        return "boolean";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public Object visit(CompareExpression n, Object argu) throws Exception {
        String type1 = (String) n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String type2 = (String) n.f2.accept(this, argu);
        if(!type1.equals("int") || !type2.equals("int")){
            throw new MiniJavaException("Invalid types for operation < : " + type1 + " , " + type2);
        }
        return "boolean";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    public Object visit(PlusExpression n, Object argu) throws Exception {
        String type1 = (String) n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String type2 = (String) n.f2.accept(this, argu);
        if(!type1.equals("int") || !type2.equals("int")){
            throw new MiniJavaException("Invalid types for operation + : " + type1 + " , " + type2);
        }
        return "int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public Object visit(MinusExpression n, Object argu) throws Exception {
        String type1 = (String) n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String type2 = (String) n.f2.accept(this, argu);
        if(!type1.equals("int") || !type2.equals("int")){
            throw new MiniJavaException("Invalid types for operation - : " + type1 + " , " + type2);
        }
        return "int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public Object visit(TimesExpression n, Object argu) throws Exception {
        String type1 = (String) n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String type2 = (String) n.f2.accept(this, argu);
        if(!type1.equals("int") || !type2.equals("int")){
            throw new MiniJavaException("Invalid types for operation * : " + type1 + " , " + type2);
        }
        return "int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public Object visit(ArrayLookup n, Object argu) throws Exception {
        String type = (String) n.f0.accept(this, argu);
        if(!type.matches("[A-Z]*[a-z]*\\[\\]")){
            throw new MiniJavaException("Cannot access index of non-array type");
        }
        String index_type = (String) n.f2.accept(this, argu);
        if(!index_type.equals("int")){
            throw new MiniJavaException("Invalid index type");
        }
        return type.replace("[]", "");
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public Object visit(ArrayLength n, Object argu) throws Exception {
        String type = (String) n.f0.accept(this, argu);
        if(!type.matches("int\\[]|boolean\\[]")){
            throw new MiniJavaException("No length attribute for type " + type);
        }

        return "int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    public Object visit(MessageSend n, Object argu) throws Exception {
        String type = (String) n.f0.accept(this, argu);
        Class c = st.getClassTable().get(type);
        n.f1.accept(this, argu);
        String methodName = getNameFromPair(n.f2.accept(this, argu));
        Method m = findMethod(c, methodName);
        if(m == null){
            throw new MiniJavaException("No method " + methodName + " on class " + type);
        }
        ArrayList<String> actualTypes = new ArrayList<>(m.getArgumentTypes().values());
        n.f3.accept(this, argu);
        ArrayList<String> types = n.f4.present()?
                (ArrayList<String>) n.f4.accept(this, argu) : new ArrayList<>();
        if(types.size() != actualTypes.size()){
            throw new MiniJavaException("Method with " + actualTypes.size() + " arguments called with " + types.size() + " parameters");
        }

        for(int i = 0; i < types.size(); i++){
            if(!typesMatch(actualTypes.get(i), types.get(i))){
                throw new MiniJavaException("Argument " + i + " expected " + actualTypes.get(i) + " but was " + types.get(i));
            }
        }
        n.f5.accept(this, argu);
        return m.getReturnType();
    }

    /**
     * f0 -> Expression()
     * f1 -> ExpressionTail()
     */
    public Object visit(ExpressionList n, Object argu) throws Exception {
        ArrayList<String> types = new ArrayList<>();
        types.add((String) n.f0.accept(this, argu));
        for(Node node : (Vector<Node>) n.f1.accept(this, argu)){
            types.add((String) node.accept(this, argu));
        }
        return types;
    }

    /**
     * f0 -> ( ExpressionTerm() )*
     */
    public Object visit(ExpressionTail n, Object argu) throws Exception {
        return n.f0.nodes;
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    public Object visit(ExpressionTerm n, Object argu) throws Exception {
        n.f0.accept(this, argu);
        return n.f1.accept(this, argu);
    }

    /**
     * f0 -> NotExpression()
     *       | PrimaryExpression()
     */
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
    public Object visit(PrimaryExpression n, Object argu) throws Exception {
        Object o = n.f0.accept(this, argu);
        if(o instanceof TypeIdentifierPair){
            return getTypeFromPair(o);
        }
        return o;
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public Object visit(IntegerLiteral n, Object argu) throws Exception {
        return "int";
    }

    /**
     * f0 -> "true"
     */
    public Object visit(TrueLiteral n, Object argu) throws Exception {
        return "boolean";
    }

    /**
     * f0 -> "false"
     */
    public Object visit(FalseLiteral n, Object argu) throws Exception {
        return "boolean";
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public TypeIdentifierPair visit(Identifier n, Object argu) throws Exception {
        String id = n.f0.toString();
        Class classContext = cm.getClassCtx();
        Method methodContext = cm.getMethodCtx();

        if(methodContext != null) {
            // Check if identifier is a method local variable or argument
            HashMap<String, String> localVars = methodContext.getLocalVariableTypes();
            HashMap<String, String> args = methodContext.getArgumentTypes();
            if (localVars.containsKey(id)) return new TypeIdentifierPair(localVars.get(id), id);
            if (args.containsKey(id)) return new TypeIdentifierPair(args.get(id), id);
        }
        if(classContext != null){
            // Check if identifier is a class field
            HashMap<String, String> classFields = classContext.getFields();
            if(classFields.containsKey(id)) return new TypeIdentifierPair(classFields.get(id), id);

            // Check if identifier is a parent class field
            Class parent = classContext.getParent();
            while(parent != null){
                HashMap<String, String> parentClassFields = parent.getFields();
                if(parentClassFields.containsKey(id)) return new TypeIdentifierPair(parentClassFields.get(id), id);
                parent = parent.getParent();
            }
        }
        // Check if identifier is a class or method
        if(st.getClassTable().containsKey(id)) return new TypeIdentifierPair(id, id);
        for(Class c : st.getClassTable().values()){
            if(c.getMethods().containsKey(id)) return new TypeIdentifierPair("method", id);
        }
        // Identifier was not declared
        throw new MiniJavaException("No declared identifier " + id);
    }


    /**
     * f0 -> "this"
     */
    public Object visit(ThisExpression n, Object argu) throws Exception {
        Class c = cm.getClassCtx();
        if(c == null){
            throw new MiniJavaException("Internal Error: \"this\" refers to no class");
        }
        return c.getName();
    }

    /**
     * f0 -> BooleanArrayAllocationExpression()
     *       | IntegerArrayAllocationExpression()
     */
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
    public Object visit(BooleanArrayAllocationExpression n, Object argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        String type = (String) n.f3.accept(this, argu);
        if(!type.equals("int")){
            throw new MiniJavaException(type + " is not a valid array size type");
        }
        n.f4.accept(this, argu);
        return "boolean[]";
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public Object visit(IntegerArrayAllocationExpression n, Object argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        String type = (String) n.f3.accept(this, argu);
        if(!type.equals("int")){
            throw new MiniJavaException(type + " is not a valid array size type");
        }
        n.f4.accept(this, argu);
        return "int[]";
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public Object visit(AllocationExpression n, Object argu) throws Exception {
        n.f0.accept(this, argu);
        String className = getNameFromPair(n.f1.accept(this, argu));
        if(!st.getClassTable().containsKey(className)){
            throw new MiniJavaException("Class with name " + className + " was not declared");
        }
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return className;
    }

    /**
     * f0 -> "!"
     * f1 -> Clause()
     */
    public Object visit(NotExpression n, Object argu) throws Exception {
        n.f0.accept(this, argu);
        String type1 = (String) n.f1.accept(this, argu);
        if(!type1.equals("boolean")){
            throw new MiniJavaException("Invalid type for operation ! : " + type1);
        }
        return "boolean";
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public Object visit(BracketExpression n, Object argu) throws Exception {
        n.f0.accept(this, argu);
        String type = (String) n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return type;
    }

    private String getNameFromPair(Object o) throws Exception{
        try {
            TypeIdentifierPair pair = (TypeIdentifierPair) o;
            return pair.name;
        }catch(Exception e){
            return "";
        }
    }

    private String getTypeFromPair(Object o) throws Exception{
        try {
            TypeIdentifierPair pair = (TypeIdentifierPair) o;
            return pair.type;
        }catch(Exception e){
            return "";
        }
    }

    private Method findMethod(Class c, String methodName){
        if(!c.getMethods().containsKey(methodName)){
            Class parent = c.getParent();
            while(parent != null){
                if(parent.getMethods().containsKey(methodName)){
                    return parent.getMethods().get(methodName);
                }
                parent = parent.getParent();
            }
            return null;
        }
        return c.getMethods().get(methodName);
    }

    private boolean typesMatch(String a, String b){
        if(!a.equals(b)){
            Class c = st.getClassTable().get(b);
            if(c != null){
                Class parent = c.getParent();
                while(parent != null){
                    if(a.equals(parent.getName())){
                        return true;
                    }
                    parent = parent.getParent();
                }
            }
            return false;
        }
        return true;
    }

    private boolean isPrintable(String type){ // according to our version of Java
        return type.equals("int");
    }

}
