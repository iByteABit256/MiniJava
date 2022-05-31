import java.util.HashMap;

public class RegisterManager {
    private HashMap<String, String> registers;
    private int registerCounter;

    public RegisterManager(){
        this.registerCounter = 0;
    }

    public TypeRegisterPair allocateRegister(String id, String type){
        type = DatatypeMapper.datatypeToLLVM(type);
        registers.put(id, type);
        return allocateRegister(type);
    }

    public TypeRegisterPair allocateRegister(String type){
        type = DatatypeMapper.datatypeToLLVM(type);
        System.out.println("\t" + currentReg() + " = alloca " + type);
        System.out.println("\tstore " + type + " " + DatatypeMapper.datatypeToDefaultVal(type) + ", " + type + "* " + currentReg());
        return new TypeRegisterPair(type, "%_" + registerCounter++);
    }

    public TypeRegisterPair loadRegister(String id, TypeRegisterPair typeRegisterPair){
        String type = DatatypeMapper.datatypeToLLVM(typeRegisterPair.getType());
        registers.put(id, type);
        return loadRegister(typeRegisterPair);
    }

    public TypeRegisterPair loadRegister(TypeRegisterPair typeRegisterPair){
        String type = DatatypeMapper.datatypeToLLVM(typeRegisterPair.getType());
        String reg = typeRegisterPair.getRegister();
        System.out.println("\t" + currentReg() + " = load " + type + ", " + type + "* " + reg);
        return new TypeRegisterPair(type, "%_" + registerCounter++);
    }

    private String currentReg(){
        return "%_" + registerCounter;
    }

    public void reset(){
        this.registerCounter = 0;
    }

}
