import java.util.LinkedHashMap;

public class Class {

    private String name;
    private LinkedHashMap<String, String> fields = new LinkedHashMap<>();
    private LinkedHashMap<String, Method> methods = new LinkedHashMap<>();
    private int currentFieldOffset = 0;
    private int currentMethodOffset = 0;
    private LinkedHashMap<String, Integer> fieldOffsets = new LinkedHashMap<>();
    private LinkedHashMap<String, Integer> methodOffsets = new LinkedHashMap<>();
    private Class parent;

    public Class(){

    }

    @Override
    public String toString() {
        String str = "";

        str += "-----------Class " + name + "-----------\n";

        str += "--Variables---\n";
        for(String fieldName : fields.keySet()){
            str += name + "." + fieldName + " : " + fieldOffsets.get(fieldName) + "\n";
        }
        str += "---Methods---\n";
        for(String methodName : methods.keySet()){
            str += name + "." + methodName + " : " + methodOffsets.get(methodName) + "\n";
        }

        return str;
    }

    public LinkedHashMap<String, String> getFields() {
        return fields;
    }

    public void setFields(LinkedHashMap<String, String> fields) {
        this.fields = fields;
    }

    public void insertField(String str, String type){
        fields.put(str, type);
        updateFieldOffsets(str, type);
    }

    public LinkedHashMap<String, Method> getMethods() {
        return methods;
    }

    public void setMethods(LinkedHashMap<String, Method> methods) {
        this.methods = methods;
    }

    public void insertMethod(String str, Method method){
        methods.put(str, method);
        updateMethodOffsets(str);
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

    public void updateFieldOffsets(String str, String type){
        fieldOffsets.put(str, currentFieldOffset);
        currentFieldOffset += DatatypeMapper.datatypeToBytes(type);
    }

    public void updateMethodOffsets(String str){
        methodOffsets.put(str, currentMethodOffset);
        currentMethodOffset += DatatypeMapper.datatypeToBytes("method");
    }

    public void canExtendParent() throws MiniJavaException{
        for(Method childMethod : methods.values()){
            Method parentMethod = parent.getMethods().get(childMethod.getName());
            if(parentMethod == null) continue;

            if(!childMethod.canOverload(parentMethod)){
                throw new MiniJavaException("Method " + childMethod.getName() + " cannot overload parent method.");
            }
        }
    }
}
