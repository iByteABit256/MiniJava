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

    public void insertArgument(String str, String type){
        argumentTypes.put(str, type);
    }

    public void insertArguments(ArrayList<TypeIdentifierPair> args){
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
