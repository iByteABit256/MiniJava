import syntaxtree.*;
import syntaxtree.Type;
import visitor.GJDepthFirst;

import java.util.ArrayList;
import java.util.Vector;

public class SymbolTableVisitor extends GJDepthFirst<Object, Object> {
    private SymbolTable st = new SymbolTable();

    public void showSymbolTable(){
        st.showClassTable();
    }

    public SymbolTable getSymbolTable(){
        return st;
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
    public Object visit(MainClass n, Object argu) throws Exception {
        Object _ret=null;

        Class mainClass = new Class();
        Method mainMethod = new Method();

        n.f0.accept(this, argu);
        mainClass.setName(n.f1.accept(this, argu).toString());
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        mainMethod.setReturnType("void");
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        n.f10.accept(this, argu);
        String argsId = n.f11.toString();
        n.f12.accept(this, argu);
        n.f13.accept(this, argu);
        n.f14.accept(this, argu);
        n.f15.accept(this, argu);
        n.f16.accept(this, argu);
        n.f17.accept(this, argu);

        st.addClass(mainClass);
        mainMethod.insertArgument(argsId, "String[]");
        mainMethod.setName("main");
        mainClass.insertMethod("main", mainMethod);

        return _ret;
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
        Object _ret=null;

        Class c = new Class();

        n.f0.accept(this, argu);
        c.setName(n.f1.accept(this, argu).toString());
        n.f2.accept(this, argu);
        for(Node node : n.f3.nodes){
            TypeIdentifierPair pair = (TypeIdentifierPair) node.accept(this, argu);
            c.insertField(pair.name, pair.type);
        }
        for(Node node : n.f4.nodes){
            Method method = (Method) node.accept(this, argu);
            c.insertMethod(method.getName(), method);
        }
        n.f5.accept(this, argu);

        st.addClass(c);

        return _ret;
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
        Object _ret=null;

        Class c = new Class();

        n.f0.accept(this, argu);
        c.setName(n.f1.accept(this, argu).toString());
        n.f2.accept(this, argu);
        String parentName = n.f3.accept(this, argu).toString();
        c.setParent(st.getClassTable().stream().filter(cl -> cl.getName().equals(parentName)).findFirst().get());
        n.f4.accept(this, argu);
        for(Node node : n.f5.nodes){
            TypeIdentifierPair pair = (TypeIdentifierPair) node.accept(this, argu);
            c.insertField(pair.name, pair.type);
        }
        for(Node node : n.f6.nodes){
            Method method = (Method) node.accept(this, argu);
            c.insertMethod(method.getName(), method);
        }
        n.f7.accept(this, argu);

        st.addClass(c);

        return _ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public Object visit(VarDeclaration n, Object argu) throws Exception {
        String type = (String) n.f0.accept(this, argu);
        String name = n.f1.accept(this, argu).toString();
        return new TypeIdentifierPair(type, name);
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
        Method method = new Method();

        n.f0.accept(this, argu);
        method.setReturnType((String) n.f1.accept(this, argu));
        method.setName(n.f2.accept(this, argu).toString());
        n.f3.accept(this, argu);
        ArrayList<TypeIdentifierPair> args = n.f4.present()?
                (ArrayList<TypeIdentifierPair>) n.f4.accept(this, argu) : new ArrayList<>();
        method.insertArguments(args);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        n.f10.accept(this, argu);
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);

        return method;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    public Object visit(FormalParameterList n, Object argu) throws Exception {
        ArrayList<TypeIdentifierPair> pairs = new ArrayList<>();
        pairs.add((TypeIdentifierPair) n.f0.accept(this, argu));
        Vector<Node> nodes = (Vector<Node>) n.f1.accept(this, argu);
        for(int i = 0; i < nodes.size(); i++){
            pairs.add((TypeIdentifierPair) nodes.elementAt(i).accept(this, argu));
        }
        return pairs;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    public Object visit(FormalParameter n, Object argu) throws Exception {
        String type = (String) n.f0.accept(this, argu);
        String name = n.f1.accept(this, argu).toString();
        return new TypeIdentifierPair(type, name);
    }

    /**
     * f0 -> ( FormalParameterTerm() )*
     */
    public Object visit(FormalParameterTail n, Object argu) throws Exception {
        return n.f0.nodes;
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    public Object visit(FormalParameterTerm n, Object argu) throws Exception {
        return n.f1.accept(this, argu);
    }

    /**
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */
    public Object visit(Type n, Object argu) throws Exception {
        return n.f0.accept(this, argu);
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
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
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
    public Object visit(AssignmentStatement n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return _ret;
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
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        return _ret;
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
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
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
    public Object visit(PrintStatement n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
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
    public Object visit(Expression n, Object argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> Clause()
     * f1 -> "&&"
     * f2 -> Clause()
     */
    public Object visit(AndExpression n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public Object visit(CompareExpression n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    public Object visit(PlusExpression n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public Object visit(MinusExpression n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public Object visit(TimesExpression n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public Object visit(ArrayLookup n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public Object visit(ArrayLength n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
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
    public Object visit(ExpressionList n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> ( ExpressionTerm() )*
     */
    public Object visit(ExpressionTail n, Object argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
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
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public Object visit(IntegerLiteral n, Object argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "true"
     */
    public Object visit(TrueLiteral n, Object argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "false"
     */
    public Object visit(FalseLiteral n, Object argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public Object visit(Identifier n, Object argu) throws Exception {
        return n.f0.toString();
    }

    /**
     * f0 -> "this"
     */
    public Object visit(ThisExpression n, Object argu) throws Exception {
        return n.f0.accept(this, argu);
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
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public Object visit(IntegerArrayAllocationExpression n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
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
    public Object visit(NotExpression n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public Object visit(BracketExpression n, Object argu) throws Exception {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

}
