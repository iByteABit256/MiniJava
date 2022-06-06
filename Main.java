import syntaxtree.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;

public class Main {
    public static void main(String[] args) throws Exception {
        InputParser inputParser = new InputParser(args);
        Emitter emitter = new Emitter(inputParser.getWriter());

        for(String inputFile : inputParser.getFiles()) {

            FileInputStream fis = null;
            try {
                fis = new FileInputStream(inputFile);
                MiniJavaParser parser = new MiniJavaParser(fis);
                Goal root = parser.Goal();

                SymbolTableVisitor stv = new SymbolTableVisitor();
                root.accept(stv, null);
                SymbolTable st = stv.getSymbolTable();
                st.setEmitter(emitter);

                TypeCheckVisitor tcv = new TypeCheckVisitor(st);
                root.accept(tcv, null);

                if(inputParser.getShowSymbolTable()){
                    stv.showSymbolTable();
                }

                if(inputParser.getShowLLVM()){
                    st.showVTable();

                    LLVMVisitor llvmVisitor = new LLVMVisitor(st);
                    root.accept(llvmVisitor, null);
                }

            } catch (ParseException ex) {
                System.err.println(ex.getMessage());
            } catch (FileNotFoundException ex) {
                System.err.println(ex.getMessage());
            } finally {
                try {
                    if (fis != null) fis.close();
                    emitter.close();
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }
}
