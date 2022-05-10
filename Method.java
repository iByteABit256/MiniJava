import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

public class Method {
    private String name;

    private String returnType;

    private LinkedHashMap<String, String> argumentTypes = new LinkedHashMap<>();

    private LinkedHashMap<String, String> localVariableTypes = new LinkedHashMap<>();

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String type) {
        this.returnType = type;
    }

    public LinkedHashMap<String, String> getArgumentTypes() {
        return argumentTypes;
    }

    public void insertArgument(String str, String type) throws MiniJavaException{
        if(argumentTypes.put(str, type) != null){
            throw new MiniJavaException("Argument with name \"" + str + "\" already exists.");
        }
    }

    public void insertArguments(ArrayList<TypeIdentifierPair> args) throws MiniJavaException{
        if(args.isEmpty()) return;

        for(TypeIdentifierPair arg : args){
            insertArgument(arg.name, arg.type);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedHashMap<String, String> getLocalVariableTypes() {
        return localVariableTypes;
    }

    public void insertLocalVariable(String str, String type) throws MiniJavaException{
        if(localVariableTypes.containsKey(str) || argumentTypes.containsKey(str)){
            throw new MiniJavaException("Local variable with name \"" + str + "\" already exists.");
        }
        localVariableTypes.put(str, type);
    }

    public void insertLocalVariables(ArrayList<TypeIdentifierPair> vars) throws MiniJavaException{
        if(vars.isEmpty()) return;

        for(TypeIdentifierPair var : vars){
            insertLocalVariable(var.name, var.type);
        }
    }

    public boolean canOverload(Method m){
        if(argumentTypes.size() != m.getArgumentTypes().size()){
            return false;
        }
        ArrayList<String> types = new ArrayList<>(argumentTypes.values());
        ArrayList<String> parentTypes = new ArrayList<>(m.getArgumentTypes().values());
        for(int i = 0; i < types.size(); i++){
            if(!types.get(i).equals(parentTypes.get(i))){
                return false;
            }
        }
        return returnType == m.getReturnType();
    }
}
