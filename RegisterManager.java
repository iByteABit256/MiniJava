import syntaxtree.Type;

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
        String ref = reference(type);
        System.out.println("\t" + currentReg() + " = alloca " + type);
        System.out.println("\tstore " + type + " " + DatatypeMapper.datatypeToDefaultVal(type) + ", " + ref + " " + currentReg());
        return new TypeRegisterPair(ref, "%_" + registerCounter++);
    }

    public TypeRegisterPair allocateRegisterWithValue(String type, String value){
        type = DatatypeMapper.datatypeToLLVM(type);
        String ref = reference(type);
        System.out.println("\t" + currentReg() + " = alloca " + type);
        System.out.println("\tstore " + type + " " + value + ", " + ref + " " + currentReg());
        return new TypeRegisterPair(ref, "%_" + registerCounter++);
    }

    public TypeRegisterPair loadRegister(String id, TypeRegisterPair typeRegisterPair){
        TypeRegisterPair reg = loadRegister(typeRegisterPair);
        registers.put(id, reg);
        return reg;
    }

    public TypeRegisterPair loadRegister(TypeRegisterPair typeRegisterPair){
        String type = DatatypeMapper.datatypeToLLVM(typeRegisterPair.getType());
        String dereferencedType = dereference(type);

        String reg = typeRegisterPair.getRegister();
        System.out.println("\t" + currentReg() + " = load " + dereferencedType + ", " + type + " " + reg);
        return new TypeRegisterPair(dereferencedType, "%_" + registerCounter++);
    }

    public void storeInRegister(TypeRegisterPair left, TypeRegisterPair right){
        if(right.getType().equals(left.getType())){
            right = loadRegister(right);
        }
        System.out.println("\tstore " + right.getType() + " " + right.getRegister() + ", " + reference(right.getType()) + " " + left.getRegister());
    }

    public TypeRegisterPair addRegisters(TypeRegisterPair left, TypeRegisterPair right){
        TypeRegisterPair loadedLeft = loadRegister(left);
        TypeRegisterPair loadedRight = loadRegister(right);
        System.out.println("\t" + currentReg() + " = add " + loadedLeft.getType() + " " + loadedLeft.getRegister() + ", " + loadedRight.getRegister());
        return new TypeRegisterPair(loadedLeft.getType(), "%_" + registerCounter++);
    }

    public TypeRegisterPair getRegisterFromID(String id){
        return registers.get(id);
    }

    private String dereference(String type){
        if(type.contains("*")){
            return type.substring(0, type.length()-1);
        }
        return type;
    }

    private String reference(String type) { return type + "*"; }

    private String currentReg(){
        return "%_" + registerCounter;
    }

    public void reset(){
        this.registerCounter = 0;
        this.registers.clear();
    }

}
