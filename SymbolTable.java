import java.util.LinkedHashMap;

public class SymbolTable {

    private LinkedHashMap<String, Class> classTable = new LinkedHashMap<>();

    public SymbolTable(){

    }

    public void addClass(Class c){
        classTable.put(c.getName(), c);
    }

    public LinkedHashMap<String, Class> getClassTable() {
        return classTable;
    }

    public void showClassTable(){
        this.classTable.entrySet().forEach(c -> {
            if(!c.getValue().getMethods().containsKey("main")){
                System.out.print(c.getValue());
            }
        });
    }

    public void showVTable(){
        this.classTable.values().forEach(c -> {
            c.setVTableEntry();
            System.out.println(c.getVTableEntry());
        });
    }

}
