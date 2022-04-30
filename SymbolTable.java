import java.util.HashMap;

public class SymbolTable {

    private boolean invalid;

    private HashMap<String, Class> classTable = new HashMap<String, Class>();

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public HashMap<String, Class> getClassTable() {
        return classTable;
    }

    public void showClassTable(){
        this.classTable.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        });
    }

}
