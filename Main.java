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

                fis2 = new FileInputStream(inputFile);
                MiniJavaParser parser2 = new MiniJavaParser(fis2);
                Goal root2 = parser2.Goal();
                TypeCheckVisitor tcv = new TypeCheckVisitor(st);
                root2.accept(tcv, null);

                if(inputParser.getShowSymbolTable()){
                    stv.showSymbolTable();
                }

                if(inputParser.getShowLLVM()){
                    LLVM_Converter llvm_converter = new LLVM_Converter(st);
                    llvm_converter.generateLLVM();
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
