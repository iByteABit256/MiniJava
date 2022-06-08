import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Class {

    private String name;
    private LinkedHashMap<String, String> fields = new LinkedHashMap<>();
    private LinkedHashMap<String, Method> methods = new LinkedHashMap<>();
    private int currentFieldOffset = 0;
    private int currentMethodOffset = 0;
    private LinkedHashMap<String, Integer> fieldOffsets = new LinkedHashMap<>();
    private LinkedHashMap<String, Integer> methodOffsets = new LinkedHashMap<>();
    private Class parent;
    private Class child;
    private String VTableEntry;

    public Class(){

    }

    private boolean parentContainsMethod(String id){
        Class currentParent = parent;
        while(currentParent != null){
            if(currentParent.getMethods().containsKey(id)) return true;
            currentParent = currentParent.getParent();
        }
        return false;
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
            if(parentContainsMethod(methodName)) continue;
            str += name + "." + methodName + " : " + methodOffsets.get(methodName) + "\n";
        }
        str += "\n";

        return str;
    }

    public int size() {
        int sum = this.fields.values().stream().map(DatatypeMapper::datatypeToBytes).mapToInt(Integer::intValue).sum();
        return sum + DatatypeMapper.datatypeToBytes("i8*");
    }

    public LinkedHashMap<String, String> getFields() {
        return fields;
    }

    public void insertField(String str, String type){
        fields.put(str, type);
        updateFieldOffsets(str, type);
    }

    public LinkedHashMap<String, Method> getMethods() {
        return methods;
    }

    public void insertMethod(String str, Method method){
        methods.put(str, method);
        updateMethodOffsets(str);
    }

    public Class getParent() {
        return parent;
    }

    public void setParent(Class parent){
        this.parent = parent;
        currentFieldOffset = parent.currentFieldOffset;
        currentMethodOffset = parent.currentMethodOffset;
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

    public String getVTableRef() {
        return VTableEntry.split(" ")[0];
    }

    public String getVTableType() {
        Pattern pattern = Pattern.compile("\\[[0-9]+ x i8\\*]");
        Matcher matcher = pattern.matcher(VTableEntry);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public String getVTableEntry() {
        return VTableEntry;
    }

    public void setVTableEntry() {
        ArrayList<Method> methods = new ArrayList<>(
                getAllMethods().values().stream().collect(Collectors.toList()));
        VTableEntry = "@." + name + "_vtable = global [" + methods.size() + " x i8*] [";
        for(int i = 0; i < methods.size(); i ++){
            methods.get(i).setVTableEntry();
            VTableEntry += methods.get(i).getVTableEntry();
            if(i != methods.size()-1){
                VTableEntry += ", ";
            }
        }
        VTableEntry += "]";
    }

    private LinkedHashMap<String, Method> getAllMethods(){
        LinkedHashMap<String, Method> methods = new LinkedHashMap<>();

        Class root = this;
        while(root.getParent() != null){
            root = root.getParent();
        }

        Class c = root;
        while(c.getName() != this.name){
            methods.putAll(c.getMethods());
            c = c.getChild();
        }
        methods.putAll(c.getMethods());

        return methods;
    }

    public Class getChild() {
        return child;
    }

    public void setChild(Class child) {
        this.child = child;
    }

    public int getFieldOffset(String id) {
        return fieldOffsets.get(id);
    }

    public int getMethodOffset(String id) {
        return methodOffsets.get(id);
    }

}
