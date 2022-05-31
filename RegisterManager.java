import java.util.HashMap;

public class RegisterManager {
    private HashMap<String, TypeRegisterPair> registers = new HashMap<>();
    private int registerCounter;

    public RegisterManager(){
        this.registerCounter = 0;
    }

    public TypeRegisterPair allocateRegister(String id, String type){
        type = DatatypeMapper.datatypeToLLVM(type);
        TypeRegisterPair reg = allocateRegister(type);
        registers.put(id, reg);
        return reg;
    }

    public TypeRegisterPair allocateRegister(String type){
        type = DatatypeMapper.datatypeToLLVM(type);
        System.out.println("\t" + currentReg() + " = alloca " + type);
        System.out.println("\tstore " + type + " " + DatatypeMapper.datatypeToDefaultVal(type) + ", " + type + "* " + currentReg());
        return new TypeRegisterPair(type, "%_" + registerCounter++);
    }

    public TypeRegisterPair allocateRegisterWithValue(String type, String value){
        type = DatatypeMapper.datatypeToLLVM(type);
        System.out.println("\t" + currentReg() + " = alloca " + type);
        System.out.println("\tstore " + type + " " + value + ", " + type + "* " + currentReg());
        return new TypeRegisterPair(type, "%_" + registerCounter++);
    }

    public TypeRegisterPair loadRegister(String id, TypeRegisterPair typeRegisterPair){
        String type = DatatypeMapper.datatypeToLLVM(typeRegisterPair.getType());
        TypeRegisterPair reg = loadRegister(typeRegisterPair);
        registers.put(id, reg);
        return reg;
    }

    public TypeRegisterPair loadRegister(TypeRegisterPair typeRegisterPair){
        String type = DatatypeMapper.datatypeToLLVM(typeRegisterPair.getType());
        String reg = typeRegisterPair.getRegister();
        System.out.println("\t" + currentReg() + " = load " + type + ", " + type + "* " + reg);
        return new TypeRegisterPair(type, "%_" + registerCounter++);
    }

    public void storeInRegister(TypeRegisterPair left, TypeRegisterPair right){
        TypeRegisterPair loadedRegister = loadRegister(right);
        System.out.println("\tstore " + left.getType() + " " + loadedRegister.getRegister() + ", " + loadedRegister.getType() + "* " + left.getRegister());
    }

    public TypeRegisterPair getRegisterFromID(String id){
        return registers.get(id);
    }

    private String currentReg(){
        return "%_" + registerCounter;
    }

    public void reset(){
        this.registerCounter = 0;
        this.registers.clear();
    }

}
