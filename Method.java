import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class Method {

    private String name;
    private String returnType;
    private LinkedHashMap<String, String> argumentTypes = new LinkedHashMap<>();
    private LinkedHashMap<String, String> localVariableTypes = new LinkedHashMap<>();
    private String VTableEntry; // Maybe unnecessary to store in variable
    private String LLVM_method_head;

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

    public void setLLVM_method_head(){
        LLVM_method_head = "define " + DatatypeMapper.datatypeToLLVM(returnType) + " @" + name + "(" + DatatypeMapper.datatypeToLLVM(name) + " %this";
        argumentTypes.forEach((argName, argType) -> LLVM_method_head += ", " + DatatypeMapper.datatypeToLLVM(argType) + " @" + argName);
        LLVM_method_head += ")";
    }

    public String getLLVM_method_head() {
        return LLVM_method_head;
    }

    public void setVTableEntry(String className) {
        String llvm_return_type = DatatypeMapper.datatypeToLLVM(returnType);
        ArrayList<String> llvm_arg_types = new ArrayList<>(argumentTypes.values().stream().map(DatatypeMapper::datatypeToLLVM).collect(Collectors.toList()));

        VTableEntry = "i8* bitcast (" + llvm_return_type + " (i8*";
        for(int i = 0; i < llvm_arg_types.size(); i++){
            VTableEntry += ",";
            VTableEntry += llvm_arg_types.get(i);
        }

        VTableEntry += ")* @" + className + "." + name + " to i8*)";
    }

    public String getVTableEntry() {
        return VTableEntry;
    }

}
