import syntaxtree.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        if(args.length != 1){
            System.err.println("Usage: java Main <inputFile>");
            System.exit(1);
        }

        FileInputStream fis = null;
        try{
            fis = new FileInputStream(args[0]);
            MiniJavaParser parser = new MiniJavaParser(fis);

            Goal root = parser.Goal();
            System.err.println("Program parsed successfully.");

            SymbolTableVisitor stv = new SymbolTableVisitor();
            root.accept(stv, null);
            SymbolTable st = stv.getSymbolTable();
            System.out.println("# of Classes: " + st.getClassTable().size());

            TypeCheckVisitor tcv = new TypeCheckVisitor(st);
            root.accept(tcv, null);

            stv.showSymbolTable();
        }
        catch(ParseException ex){
            System.out.println(ex.getMessage());
        }
        catch(FileNotFoundException ex){
            System.err.println(ex.getMessage());
        }
        finally{
            try{
                if(fis != null) fis.close();
            }
            catch(IOException ex){
                System.err.println(ex.getMessage());
            }
        }
    }
}
