import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Method {
    private String name;

    private String returnType;

    private LinkedHashMap<String, String> argumentTypes = new LinkedHashMap<>();

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
}
