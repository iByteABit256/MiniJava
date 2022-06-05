import java.util.ArrayList;
import java.util.HashMap;

public class RegisterManager {
    private HashMap<String, TypeRegisterPair> registers = new HashMap<>();
    private int registerCounter;

    public RegisterManager(){
        this.registerCounter = 0;
    }

    public TypeRegisterPair allocateRegister(String id, String type, String VTableRef, String VTableType, int size, int offset, String argumentId){
        type = DatatypeMapper.datatypeToLLVM(type);
        TypeRegisterPair reg = allocateRegister(type, argumentId);
        reg.setVTableRef(VTableRef);
        reg.setVTableType(VTableType);
        reg.setSize(size);
        reg.setOffset(offset);
        registers.put(id, reg);
        return reg;
    }

    public TypeRegisterPair allocateRegister(String id, String type, SymbolTable st, String argumentId){
        Class c = st.getClassTable().get(type);

        if(c != null) {
            if (c.getMethods().containsKey("main")) {
                return new TypeRegisterPair("i8", id);
            }
            String VTableRef = c.getVTableRef();
            String VTableType = c.getVTableType();
            return allocateRegister(id, type, VTableRef, VTableType, c.size(), 0, argumentId);
        }

        type = DatatypeMapper.datatypeToLLVM(type);
        TypeRegisterPair reg = allocateRegister(type, argumentId);
        registers.put(id, reg);
        return reg;
    }

    public TypeRegisterPair allocateRegister(String type, String argumentId){
        type = DatatypeMapper.datatypeToLLVM(type);
        String ref = reference(type);

        if(argumentId != null){
            return new TypeRegisterPair(type, "%" + argumentId);
        }
        String registerName = "%_" + registerCounter++;

        System.out.println("\t" + registerName + " = alloca " + type);
        System.out.println("\tstore " + type + " " + DatatypeMapper.datatypeToDefaultVal(type) + ", " + ref + " " + registerName);
        return new TypeRegisterPair(ref, registerName);
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
        System.err.println(left.getType());
        System.err.println(right.getType());

        if(right.getType().equals(left.getType())){
            right = loadRegister(right);
        }
        System.out.println("\tstore " + right.getType() + " " + right.getRegister() + ", " + reference(right.getType()) + " " + left.getRegister());
    }

    public TypeRegisterPair addRegisters(TypeRegisterPair left, TypeRegisterPair right){
        TypeRegisterPair loadedLeft = left;
        TypeRegisterPair loadedRight = right;
        if(left.getType().contains("*")){
            loadedLeft = loadRegister(left);
        }
        if(right.getType().contains("*")) {
            loadedRight = loadRegister(right);
        }
        System.out.println("\t" + currentReg() + " = add " + loadedLeft.getType() + " " + loadedLeft.getRegister() + ", " + loadedRight.getRegister());
        return new TypeRegisterPair(loadedLeft.getType(), "%_" + registerCounter++);
    }

    public TypeRegisterPair subRegisters(TypeRegisterPair left, TypeRegisterPair right){
        TypeRegisterPair loadedLeft = left;
        TypeRegisterPair loadedRight = right;
        if(left.getType().contains("*")){
            loadedLeft = loadRegister(left);
        }
        if(right.getType().contains("*")) {
            loadedRight = loadRegister(right);
        }
        System.out.println("\t" + currentReg() + " = sub " + loadedLeft.getType() + " " + loadedLeft.getRegister() + ", " + loadedRight.getRegister());
        return new TypeRegisterPair(loadedLeft.getType(), "%_" + registerCounter++);
    }

    public TypeRegisterPair mulRegisters(TypeRegisterPair left, TypeRegisterPair right){
        TypeRegisterPair loadedLeft = left;
        TypeRegisterPair loadedRight = right;
        if(left.getType().contains("*")){
            loadedLeft = loadRegister(left);
        }
        if(right.getType().contains("*")) {
            loadedRight = loadRegister(right);
        }
        System.out.println("\t" + currentReg() + " = mul " + loadedLeft.getType() + " " + loadedLeft.getRegister() + ", " + loadedRight.getRegister());
        return new TypeRegisterPair(loadedLeft.getType(), "%_" + registerCounter++);
    }

    public TypeRegisterPair xorRegister(TypeRegisterPair reg){
        TypeRegisterPair loaded = reg;
        if(reg.getType().contains("*")) {
            loaded = loadRegister(reg);
        }
        System.out.println("\t" + currentReg() + " = xor " + loaded.getType() + " " + loaded.getRegister() + ", true");
        return new TypeRegisterPair(loaded.getType(), "%_" + registerCounter++);
    }

    public TypeRegisterPair calloc(String type, TypeRegisterPair size){
        type = DatatypeMapper.datatypeToLLVM(type);

        /**
         * Classes
         */
        if(type.equals("i8")) {
            String registerToAllocateTo = currentReg();
            System.out.println("\t%_" + registerCounter++ + " = call i8* @calloc(i32 1, i32 " + size.getSize() + ")");
            System.out.println("\t%_" + registerCounter + " = bitcast i8* %_" + (registerCounter++ - 1) + " to i8***");
            System.out.println("\t%_" + registerCounter++ + " = getelementptr " + size.getVTableType() + ", "
                    + reference(size.getVTableType()) + " " + size.getVTableRef() + ", i32 0, i32 0");
            System.out.println("\tstore i8** %_" + (registerCounter - 1) + ", i8*** %_" + (registerCounter++ - 2));

            return new TypeRegisterPair("i8*", registerToAllocateTo);
        }

        /**
         * Arrays
         */
        if(size.getType().contains("*")) {
            size = loadRegister(size);
        }
        System.out.println("\t" + currentReg() + " = add i32 " + size.getRegister() + ", " + (type.equals("i1")? 4 : 1));
        TypeRegisterPair total = new TypeRegisterPair("i32", "%_" + registerCounter++);
        System.out.println("\t%_" + registerCounter++ + " = call i8* @calloc(i32 " +
                DatatypeMapper.datatypeToBytes(dereference(type)) + ", i32 " + total.getRegister() + ")");
        System.out.println("\t" + currentReg() + " = bitcast i8* %_" + (registerCounter-1) + " to " + reference(type));

        // Size metadata
        if(type.equals("i1")){
            System.out.println("\t%_" + ++registerCounter + " = bitcast i1* %_" + (registerCounter-1) + " to i32*");
            System.out.println("\tstore i32 " + size.getRegister() + ", i32* " + currentReg());
            return new TypeRegisterPair("i1*", "%_" + (registerCounter++-1));
        }

        System.out.println("\tstore " + type + " " + size.getRegister() + ", " + reference(type) + " " + currentReg());

        return new TypeRegisterPair(reference(type), "%_" + registerCounter++);
    }

    public TypeRegisterPair getArrayElement(TypeRegisterPair arr, TypeRegisterPair idx){
        TypeRegisterPair loadedIdx = idx;
        if(idx.getType().contains("*")){
            loadedIdx = loadRegister(idx);
        }
        TypeRegisterPair loadedArr = loadRegister(arr);

        // Check for index out of bounds
        TypeRegisterPair length = getArrayLength(arr);
        TypeRegisterPair condition = lessThan(loadedIdx, length);
        TypeRegisterPair label1 = new TypeRegisterPair();
        TypeRegisterPair label2 = new TypeRegisterPair();
        TypeRegisterPair exit = ifStatement(condition, label1, label2);

        // Valid index
        System.out.println(label1.getRegister().substring(1) + ":");
        System.out.println("\t" + currentReg() + " = add i32 " + loadedIdx.getRegister() + ", " + (loadedArr.getType().equals("i1*")? 4 : 1));
        loadedIdx = new TypeRegisterPair("i32", "%_" + registerCounter++);
        String type = loadedArr.getType();
        String dereferencedType = dereference(type);
        System.out.println("\t" + currentReg() + " = getelementptr " + dereferencedType + ", " +
                type + " " + loadedArr.getRegister() + ", i32 " + loadedIdx.getRegister());
        String elementReg = "%_" + registerCounter++;
        System.out.println("\tbr label " + exit.getRegister() + "\n");

        // Invalid index
        System.out.println(label2.getRegister().substring(1) + ":");
        handleOutOfBoundsException();
        System.out.println("\tbr label " + exit.getRegister() + "\n");

        // Continue execution
        System.out.println(exit.getRegister().substring(1) + ":");

        return new TypeRegisterPair(type, elementReg);
    }

    public void handleOutOfBoundsException(){
        System.out.println("\tcall void @throw_oob()");
    }

    public TypeRegisterPair getArrayLength(TypeRegisterPair arr){
        TypeRegisterPair loadedArr = loadRegister(arr);
        String type = loadedArr.getType();

        if(type.equals("i1*")){
            System.out.println("\t%_" + registerCounter++ + " = bitcast i1* " + loadedArr.getRegister() + " to i32*");
            System.out.println("\t" + currentReg() + " = getelementptr i32, i32* %_" + (registerCounter-1) + ", i32 0");
            return new TypeRegisterPair("i32*", "%_" + registerCounter++);
        }

        String dereferencedType = dereference(type);
        System.out.println("\t" + currentReg() + " = getelementptr " + dereferencedType + ", " +
                type + " " + loadedArr.getRegister() + ", i32 " + 0);
        return new TypeRegisterPair("i32*", "%_" + registerCounter++);
    }

    public TypeRegisterPair and(TypeRegisterPair left, TypeRegisterPair right){
        if(left.getType().contains("*")){
            left = loadRegister(left);
        }
        if(right.getType().contains("*")){
            right = loadRegister(right);
        }

        System.out.println("\t" + currentReg() + " = and i1 " + left.getRegister() + ", " + right.getRegister());
        return new TypeRegisterPair("i1", "%_" + registerCounter++);
    }

    public TypeRegisterPair lessThan(TypeRegisterPair left, TypeRegisterPair right){
        if(left.getType().contains("*")){
            left = loadRegister(left);
        }
        if(right.getType().contains("*")){
            right = loadRegister(right);
        }

        System.out.println("\t" + currentReg() + " = icmp slt i32 " + left.getRegister() + ", " + right.getRegister());
        return new TypeRegisterPair("i1", "%_" + registerCounter++);
    }

    public TypeRegisterPair ifStatement(TypeRegisterPair condition, TypeRegisterPair label1, TypeRegisterPair label2){
        if(condition.getType().contains("*")){
            condition = loadRegister(condition);
        }

        label1.setRegister("%label" + registerCounter++);
        label2.setRegister("%label" + registerCounter++);

        System.out.println("\tbr i1 " + condition.getRegister() + ", label " + label1.getRegister() + ", label " + label2.getRegister());
        System.out.println();

        return new TypeRegisterPair(null, "%label" + registerCounter++);
    }

    public void loopConditionCheck(TypeRegisterPair condition, TypeRegisterPair body, TypeRegisterPair exit){
        if(condition.getType().contains("*")){
            condition = loadRegister(condition);
        }

        System.out.println("\tbr i1 " + condition.getRegister() + ", label " + body.getRegister() + ", label " + exit.getRegister());
        System.out.println();
    }

    public TypeRegisterPair whileStatement(TypeRegisterPair conditionCheck, TypeRegisterPair body){
        conditionCheck.setRegister("%label" + registerCounter++);
        body.setRegister("%label" + registerCounter++);

        System.out.println("\tbr label " + conditionCheck.getRegister());
        System.out.println();

        return new TypeRegisterPair(null, "%label" + registerCounter++);
    }

    public TypeRegisterPair methodCall(TypeRegisterPair c, TypeRegisterPair m, ArrayList<TypeRegisterPair> expressions, Method method){
        if(c.getType().contains("**")){
            c = loadRegister(c);
        }

        ArrayList<String> paramTypes = new ArrayList<>(method.getArgumentTypes().values());
        for(int i = 0; i < expressions.size(); i++){
            if(!expressions.get(i).getType().equals(DatatypeMapper.datatypeToLLVM(paramTypes.get(i)))){
                expressions.set(i, loadRegister(expressions.get(i)));
            }
        }
        System.out.println("\t%_" + registerCounter++ + " = bitcast i8* " + c.getRegister() + " to i8***");
        System.out.println("\t%_" + registerCounter + " = load i8**, i8*** %_" + (registerCounter++-1));
        System.out.println("\t%_" + registerCounter + " = getelementptr i8*, i8** %_" + (registerCounter++-1) + ", i32 " + m.getOffset()/8);
        System.out.println("\t%_" + registerCounter + " = load i8*, i8** %_" + (registerCounter++-1));
        System.out.print("\t%_" + registerCounter + " = bitcast i8* %_" + (registerCounter++-1) + " to " + m.getMethodReturnType() + " (i8*");
        expressions.forEach(e -> System.out.print("," + e.getType()));
        System.out.println(")*");
        System.out.print("\t" + currentReg() + " = call " + m.getMethodReturnType() + " %_" + (registerCounter-1) + "(i8* " + c.getRegister());
        expressions.forEach(e -> System.out.print("," + e.getType() + " " + e.getRegister()));
        System.out.println(")");

        return new TypeRegisterPair(m.getMethodReturnType(), "%_" + registerCounter++);
    }

    public TypeRegisterPair getRegisterFromID(String id){
        return registers.get(id);
    }

    public void print(TypeRegisterPair typeRegisterPair){
        if(typeRegisterPair.getType().contains("*")){
            typeRegisterPair = loadRegister(typeRegisterPair);
        }
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
