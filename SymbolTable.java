import java.util.LinkedHashMap;

public class SymbolTable {

    private LinkedHashMap<String, Class> classTable = new LinkedHashMap<>();
    private Emitter emitter;

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
                emitter.emit(c.getValue());
            }
        });
    }

    public void showVTable(){
        this.classTable.values().forEach(c -> {
            c.setVTableEntry();
            if(!c.getMethods().containsKey("main")) {
                emitter.emitln(c.getVTableEntry());
            }
        });
        emitter.emitln();
    }

    public Emitter getEmitter() {
        return emitter;
    }

    public void setEmitter(Emitter emitter) {
        this.emitter = emitter;
    }
}
