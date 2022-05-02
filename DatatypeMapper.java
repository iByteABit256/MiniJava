public class DatatypeMapper {
    public static MiniJavaDatatype strToDatatype(String str) {
        switch (str) {
            case "int":
                return MiniJavaDatatype.INT;
            case "boolean":
                return MiniJavaDatatype.BOOLEAN;
            case "void":
            case "int[]":
            case "boolean[]":
            case "String[]":
                return MiniJavaDatatype.POINTER;
            default:
                return MiniJavaDatatype.INVALID;
        }
    }

    public static int datatypeToBytes(MiniJavaDatatype type){
        switch (type) {
            case INT:
                return 4;
            case BOOLEAN:
                return 1;
            case POINTER:
                return 8;
            default:
                return -1;
        }
    }
}
