import syntaxtree.Type;

public class TypeRegisterPair {
    private String type;
    private String register;
    private String VTableRef;
    private String VTableType;
    private String methodReturnType;
    private Method method;
    private int size;
    private int offset;

    public TypeRegisterPair(){

    }

    public TypeRegisterPair(TypeRegisterPair typeRegisterPair){
        this.type = typeRegisterPair.type;
        this.register = typeRegisterPair.register;
        this.VTableRef = typeRegisterPair.VTableRef;
        this.VTableType = typeRegisterPair.VTableType;
        this.methodReturnType = typeRegisterPair.methodReturnType;
        this.method = typeRegisterPair.method;
        this.size = typeRegisterPair.size;
        this.offset = typeRegisterPair.offset;
    }

    public TypeRegisterPair(String type, String register){
        this.type = type;
        this.register = register;
    }

    public TypeRegisterPair(String type, String register, String VTableRef, String VTableType, int size, int offset){
        this.type = type;
        this.register = register;
        this.VTableRef = VTableRef;
        this.VTableType = VTableType;
        this.offset = offset;
        this.size = size;
    }

    public TypeRegisterPair(String type, String register, Class c, int offset){
        this.type = type;
        this.register = register;
        this.VTableRef = c.getVTableRef();
        this.VTableType = c.getVTableType();
        this.offset = offset;
        this.size = c.size();
        if(c.getMethods().containsKey(register)) {
            this.method = c.getMethods().get(register);
            this.methodReturnType = DatatypeMapper.datatypeToLLVM(method.getReturnType());
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getRegister() {
        return register;
    }

    public void setRegister(String register){
        this.register = register;
    }


    public String getVTableRef() {
        return VTableRef;
    }

    public void setVTableRef(String VTableRef) {
        this.VTableRef = VTableRef;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getVTableType() {
        return VTableType;
    }

    public void setVTableType(String VTableType) {
        this.VTableType = VTableType;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getMethodReturnType() {
        return methodReturnType;
    }

    public void setMethodReturnType(String methodReturnType) {
        this.methodReturnType = methodReturnType;
    }

    public String getName(){
        return VTableRef.replaceAll("(@.|_vtable)", "");
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
