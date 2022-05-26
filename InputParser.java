import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class InputParser {
    private ArrayList<String> files = new ArrayList<>();
    private boolean showSymbolTable = false;
    private boolean showLLVM = false;

    public boolean getShowSymbolTable() {
        return showSymbolTable;
    }

    public boolean getShowLLVM() {
        return showLLVM;
    }

    public ArrayList<String> getFiles() {
        return files;
    }

    public InputParser(String[] args){
        HashMap<String, String> params = new HashMap<>();
        Pattern pattern = Pattern.compile("-[a-zA-Z]+");

        if(args.length == 0){
            handleInputArgumentError("Usage: java Main <inputFiles>");
        }

        int i = 0;
        while(i < args.length){
            String arg = args[i];

            if(pattern.matcher(arg).matches()){
                if(arg.length() < 2){
                    handleInputArgumentError(null);
                }

                if(i+1 < args.length){
                    String opt = args[i+1];
                    if(pattern.matcher(opt).matches()){
                        i++;
                        continue;
                    }
                    params.put(arg, opt);
                    i++;
                    continue;
                }
                params.put(arg, "");
                i += 2;
            }else{
                if(Files.notExists(Paths.get(arg))){
                    handleInputArgumentError("File '" + arg + "' does not exist");
                }
                files.add(arg);
                i++;
            }
        }

        setFlags(params);
    }

    private void setFlags(HashMap<String, String> params){
        if(params.containsKey("-st")){
            showSymbolTable = true;
        }
        if(params.containsKey("-llvm")){
            showLLVM = true;
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
}
