import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Method {
    private String name;

    private MiniJavaDatatype returnType;

    private LinkedHashMap<String, MiniJavaDatatype> argumentTypes = new LinkedHashMap<>();

    public MiniJavaDatatype getReturnType() {
        return returnType;
    }

    public void setReturnType(MiniJavaDatatype type) {
        this.returnType = type;
    }

    public LinkedHashMap<String, MiniJavaDatatype> getArgumentTypes() {
        return argumentTypes;
    }

    public void insertArgument(String str, MiniJavaDatatype type){
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
