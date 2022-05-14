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

    /** Checks implemented:
     * Same class names
     * Parent class not existing
     * Same method names
     * Same field names
     * Same method argument names
     * Same local variable names
     * Local variable with same name as method argument
     * Overloaded methods are valid
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
        Class mainClass = new Class();
        Method mainMethod = new Method();

        mainClass.setName(n.f1.accept(this, argu).toString());
        mainMethod.setReturnType("void");
        String argsId = n.f11.accept(this, argu).toString();
        for(Node node : n.f14.nodes){
            TypeIdentifierPair pair = (TypeIdentifierPair) node.accept(this, argu);
            mainMethod.insertLocalVariable(pair.name, pair.type);
        }

        st.addClass(mainClass);
        mainMethod.setName("main");
        mainMethod.insertArgument(argsId, "String[]");
        mainClass.insertMethod("main", mainMethod);

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
        Class c = new Class();

        c.setName(n.f1.accept(this, argu).toString());
        if(st.getClassTable().containsKey(c.getName())){
            throw new MiniJavaException("Class with name \"" + c.getName() + "\" already exists.");
        }
        for(Node node : n.f3.nodes){
            TypeIdentifierPair pair = (TypeIdentifierPair) node.accept(this, argu);
            if(c.getFields().containsKey(pair.name)){
                throw new MiniJavaException("Field with name \"" + pair.name + "\" already exists.");
            }
            c.insertField(pair.name, pair.type);
        }
        for(Node node : n.f4.nodes){
            Method method = (Method) node.accept(this, argu);
            if(c.getMethods().containsKey(method.getName())){
                throw new MiniJavaException("Method with name \"" + method.getName() + "\" already exists");
            }
            c.insertMethod(method.getName(), method);
        }

        st.addClass(c);

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
        Class c = new Class();

        c.setName(n.f1.accept(this, argu).toString());
        if(st.getClassTable().containsKey(c.getName())){
            throw new MiniJavaException("Class with name \"" + c.getName() + "\" already exists.");
        }
        String parentName = n.f3.accept(this, argu).toString();
        Class parent = st.getClassTable().get(parentName);
        if(parent != null) {
            c.setParent(st.getClassTable().get(parentName));
        }else{
            throw new MiniJavaException("Parent class does not exist.");
        }
        for(Node node : n.f5.nodes){
            TypeIdentifierPair pair = (TypeIdentifierPair) node.accept(this, argu);
            if(c.getFields().containsKey(pair.name)){
                throw new MiniJavaException("Field with name \"" + pair.name + "\" already exists.");
            }
            c.insertField(pair.name, pair.type);
        }
        for(Node node : n.f6.nodes){
            Method method = (Method) node.accept(this, argu);
            if(c.getMethods().containsKey(method.getName())){
                throw new MiniJavaException("Method with name \"" + method.getName() + "\" already exists");
            }
            c.insertMethod(method.getName(), method);
        }

        c.canExtendParent();
        st.addClass(c);

        return null;
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
        for(Node node : n.f7.nodes){
            TypeIdentifierPair pair = (TypeIdentifierPair) node.accept(this, argu);
            method.insertLocalVariable(pair.name, pair.type);
        }
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
     * f0 -> <IDENTIFIER>
     */
    public Object visit(Identifier n, Object argu) throws Exception {
        return n.f0.toString();
    }

}
