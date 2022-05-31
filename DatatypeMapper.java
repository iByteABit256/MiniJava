public class DatatypeMapper {

    public static int datatypeToBytes(String type){
        switch (type) {
            case "int":
                return 4;
            case "boolean":
                return 1;
            default:
                return 8;
        }
    }

    public static String datatypeToLLVM(String type){
        switch (type) {
            case "int":
            case "i32":
                return "i32";
            case "boolean":
            case "i1":
                return "i1";
            case "int[]":
            case "i32*":
                return "i32*";
            case "boolean[]":
            case "i1*":
                return "i1*";
            case "void":
            case "static void":
                return "void";
            default:
                return "i8*";
        }
    }

    public static String datatypeToDefaultVal(String type){
        switch (type) {
            case "i32":
                return "0";
            case "i1":
                return "false";
            default:
                return "null";
        }
    }
}
