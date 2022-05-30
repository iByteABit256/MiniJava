import javax.xml.crypto.Data;
import java.util.HashMap;

public class RegisterManager {
    private HashMap<String, String> registers;
    private int registerCounter;

    public RegisterManager(){
        this.registerCounter = 0;
    }

    public TypeRegisterPair newRegister(String id, String type){
        type = DatatypeMapper.datatypeToLLVM(type);
        registers.put(id, type);
        return newRegister(type);
    }

    public TypeRegisterPair newRegister(String type){
        type = DatatypeMapper.datatypeToLLVM(type);
        allocateNewRegister(type);
        return new TypeRegisterPair(type, "%_" + registerCounter++);
    }

    private void allocateNewRegister(String type){
        System.out.println("\t" + currentReg() + " = alloca " + type);
        System.out.println("\tstore " + type + " " + DatatypeMapper.datatypeToDefaultVal(type) + ", " + type + "* " + currentReg());
    }

    private String currentReg(){
        return "%_" + registerCounter;
    }

    public void reset(){
        this.registerCounter = 0;
    }

}
