import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class InputParser {
    private ArrayList<String> files = new ArrayList<>();
    private boolean showSymbolTable = false;
    private boolean showLLVM = false;
    private Writer writer;

    public boolean getShowSymbolTable() {
        return showSymbolTable;
    }

    public boolean getShowLLVM() {
        return showLLVM;
    }

    public ArrayList<String> getFiles() {
        return files;
    }

    public InputParser(String[] args) throws IOException {
        HashMap<String, String> params = new HashMap<>();

        if(args.length == 0){
            handleInputArgumentError("Usage: java Main <inputFiles>");
        }

        for(int i = 0; i < args.length; i++){
            String arg = args[i];

            if(arg.startsWith("-")){
                if(arg.length() < 2){
                    handleInputArgumentError(null);
                }

                if(arg.contains("=")){
                    String[] opt = arg.split("=");
                    if(opt[1].isEmpty()){
                        handleInputArgumentError("No option passed to " + opt[0]);
                    }
                    params.put(opt[0], opt[1]);
                    continue;
                }
                params.put(arg, "");
            }else{
                if(Files.notExists(Paths.get(arg))){
                    handleInputArgumentError("File '" + arg + "' does not exist");
                }
                files.add(arg);
            }
        }

        setFlags(params);
    }

    private void setFlags(HashMap<String, String> params) throws IOException {
        if(params.containsKey("-st")){
            showSymbolTable = true;
        }
        if(params.containsKey("-llvm")){
            showLLVM = true;
        }
        if(params.containsKey("-o")){
            writer = new FileWriter(params.get("-o"));
        }else{
            writer = new OutputStreamWriter(System.out);
        }
    }

    private void handleInputArgumentError(String errorMessage){
        if(errorMessage != null && !errorMessage.isEmpty()){
            System.err.println(errorMessage);
        }else{
            System.err.println("Invalid input arguments");
        }
        System.exit(1);
    }

    public Writer getWriter() {
        return writer;
    }
}
