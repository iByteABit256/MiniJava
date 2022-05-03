import java.util.HashMap;

public class Class {

    private String name;
    private HashMap<String, MiniJavaDatatype> fields = new HashMap<>();
    private HashMap<String, Method> methods = new HashMap<>();
    private int currentFieldOffset = 0;
    private int currentMethodOffset = 0;
    private HashMap<String, Integer> fieldOffsets = new HashMap<>();
    private HashMap<String, Integer> methodOffsets = new HashMap<>();
    private Class parent;

    public Class(){

    }

    @Override
    public String toString() {
        String str = "";

        for(String fieldName : fields.keySet()){
            str += name + "." + fieldName + " : " + fieldOffsets.get(fieldName) + "\n";
        }

        for(String methodName : methods.keySet()){
            str += name + "." + methodName + " : " + methodOffsets.get(methodName) + "\n";
        }

        return str;
    }

    public HashMap<String, MiniJavaDatatype> getFields() {
        return fields;
    }

    public void setFields(HashMap<String, MiniJavaDatatype> fields) {
        this.fields = fields;
    }

    public void insertField(String str, MiniJavaDatatype type){
        fields.put(str, type);
        updateFieldOffsets(str, type);
    }

    public HashMap<String, Method> getMethods() {
        return methods;
    }

    public void setMethods(HashMap<String, Method> methods) {
        this.methods = methods;
    }

    public void insertMethod(String str, Method method){
        methods.put(str, method);
        updateMethodOffsets(str, method);
    }

    public Class getParent() {
        return parent;
    }

    public void setParent(Class parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void updateFieldOffsets(String str, MiniJavaDatatype type){
        fieldOffsets.put(str, currentFieldOffset);
        currentFieldOffset += DatatypeMapper.datatypeToBytes(type);
    }

    public void updateMethodOffsets(String str, Method method){
        methodOffsets.put(str, currentMethodOffset);
        currentMethodOffset += DatatypeMapper.datatypeToBytes(MiniJavaDatatype.METHOD);
    }
}
