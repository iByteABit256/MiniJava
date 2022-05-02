import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {

    private ArrayList<Class> classTable = new ArrayList<>();

    public SymbolTable(){

    }

    public void addClass(Class c){
        classTable.add(c);
    }

    public ArrayList<Class> getClassTable() {
        return classTable;
    }

    public void showClassTable(){
        this.classTable.forEach(c -> {
            System.out.print(c);
        });
    }

}
