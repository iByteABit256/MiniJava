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
                return "_i32";
            case "boolean":
                return "_i1";
            case "int[]":
                return "_i32*";
            case "boolean[]":
                return "_i1*";
            default:
                return "_i8*";
        }
    }
}
