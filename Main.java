import syntaxtree.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        InputParser inputParser = new InputParser(args);

        for(String inputFile : inputParser.getFiles()) {

            FileInputStream fis = null;
            FileInputStream fis2 = null;
            try {
                fis = new FileInputStream(inputFile);
                MiniJavaParser parser = new MiniJavaParser(fis);
                Goal root = parser.Goal();

                SymbolTableVisitor stv = new SymbolTableVisitor();
                root.accept(stv, null);
                SymbolTable st = stv.getSymbolTable();

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
                System.out.println(ex.getMessage());
            } catch (FileNotFoundException ex) {
                System.err.println(ex.getMessage());
            } finally {
                try {
                    if (fis != null) fis.close();
                    if (fis2 != null) fis2.close();
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }
}
