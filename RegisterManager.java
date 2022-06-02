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

    public TypeRegisterPair subRegisters(TypeRegisterPair left, TypeRegisterPair right){
        TypeRegisterPair loadedLeft = loadRegister(left);
        TypeRegisterPair loadedRight = loadRegister(right);
        System.out.println("\t" + currentReg() + " = sub " + loadedLeft.getType() + " " + loadedLeft.getRegister() + ", " + loadedRight.getRegister());
        return new TypeRegisterPair(loadedLeft.getType(), "%_" + registerCounter++);
    }

    public TypeRegisterPair mulRegisters(TypeRegisterPair left, TypeRegisterPair right){
        TypeRegisterPair loadedLeft = loadRegister(left);
        TypeRegisterPair loadedRight = loadRegister(right);
        System.out.println("\t" + currentReg() + " = mul " + loadedLeft.getType() + " " + loadedLeft.getRegister() + ", " + loadedRight.getRegister());
        return new TypeRegisterPair(loadedLeft.getType(), "%_" + registerCounter++);
    }

    public TypeRegisterPair xorRegister(TypeRegisterPair reg){
        TypeRegisterPair loaded = loadRegister(reg);
        System.out.println("\t" + currentReg() + " = xor " + loaded.getType() + " " + loaded.getRegister() + ", true");
        return new TypeRegisterPair(loaded.getType(), "%_" + registerCounter++);
    }

//    public TypeRegisterPair calloc(String type, TypeRegisterPair size){
//        size = loadRegister(size);
//        System.out.println("\t%_" + registerCounter++ + " = call i8* @calloc(i32 " +
//                DatatypeMapper.datatypeToBytes(type) + ", i32 " + size.getRegister() + ")");
//        System.out.println("\t" + currentReg() + " = bitcast i8* %_" + (registerCounter-1) + " to " + reference(type));
//        return new TypeRegisterPair(reference(type), "%_" + registerCounter++);
//    }

    public TypeRegisterPair callocIntArray(String type, TypeRegisterPair size){
        size = loadRegister(size);
        System.out.println("\t%_" + registerCounter++ + " = call i8* @calloc(i32 " +
                DatatypeMapper.datatypeToBytes(type) + ", i32 " + size.getRegister() + ")");
        System.out.println("\t" + currentReg() + " = bitcast i8* %_" + (registerCounter-1) + " to " + reference(type));
        return new TypeRegisterPair(reference(type), "%_" + registerCounter++);
    }

    public TypeRegisterPair getArrayElement(TypeRegisterPair arr, TypeRegisterPair idx){
        TypeRegisterPair loadedIdx = loadRegister(idx);
        String type = arr.getType();
        String dereferencedType = dereference(type);
        System.out.println("\t" + currentReg() + " = getelementptr " + dereferencedType + ", " +
                type + " " + arr.getRegister() + ", i32 0, i32 " + loadedIdx.getRegister());
        return new TypeRegisterPair(dereferencedType, "%_" + registerCounter++);
    }

    public TypeRegisterPair getRegisterFromID(String id){
        return registers.get(id);
    }

    public void print(TypeRegisterPair typeRegisterPair){
        typeRegisterPair = loadRegister(typeRegisterPair);
        System.out.println("\tcall void (i32) @print_int(i32 " + typeRegisterPair.getRegister() + ")");
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
