public class TypeRegisterPair {
    private String type;
    private String register;

    public TypeRegisterPair(){

    }

    public TypeRegisterPair(String type, String register){
        this.type = type;
        this.register = register;
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

}
