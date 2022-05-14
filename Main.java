import syntaxtree.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        if(args.length == 0){
            System.err.println("Usage: java Main <inputFiles>");
            System.exit(1);
        }

        for(int i = 0; i < args.length; i++) {

            FileInputStream fis = null;
            FileInputStream fis2 = null;
            try {
                fis = new FileInputStream(args[i]);
                MiniJavaParser parser = new MiniJavaParser(fis);
                Goal root = parser.Goal();
                SymbolTableVisitor stv = new SymbolTableVisitor();
                root.accept(stv, null);
                SymbolTable st = stv.getSymbolTable();

                fis2 = new FileInputStream(args[i]);
                MiniJavaParser parser2 = new MiniJavaParser(fis2);
                Goal root2 = parser2.Goal();
                TypeCheckVisitor tcv = new TypeCheckVisitor(st);
                root2.accept(tcv, null);

                stv.showSymbolTable();
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
