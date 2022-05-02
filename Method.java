import java.util.LinkedHashMap;

public class Method {
    private MiniJavaDatatype returnType;
    private LinkedHashMap<String, MiniJavaDatatype> argumentTypes;

    public MiniJavaDatatype getReturnType() {
        return returnType;
    }

    public void setReturnType(String str) {
        this.returnType = DatatypeMapper.strToDatatype(str);
    }

    public void setReturnType(MiniJavaDatatype type) {
        this.returnType = type;
    }

    public LinkedHashMap<String, MiniJavaDatatype> getArgumentTypes() {
        return argumentTypes;
    }

    public void insertArgument(String str, String type){
        argumentTypes.put(str, DatatypeMapper.strToDatatype(type));
    }

    public void insertArgument(String str, MiniJavaDatatype type){
        argumentTypes.put(str, type);
    }
}
