public class TypeRegisterPair {
    private String type;
    private String register;
    private String VTableRef;
    private String VTableType;
    private int size;
    private int offset;

    public TypeRegisterPair(){

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
}
