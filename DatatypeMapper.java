public class DatatypeMapper {
    public static MiniJavaDatatype strToDatatype(String str) {
        switch (str) {
            case "int":
                return MiniJavaDatatype.INT;
            case "boolean":
                return MiniJavaDatatype.BOOLEAN;
            case "void":
                return MiniJavaDatatype.VOID;
            case "int[]":
                return MiniJavaDatatype.INT_POINTER;
            case "boolean[]":
                return MiniJavaDatatype.BOOLEAN_POINTER;
            case "String[]":
                return MiniJavaDatatype.STRING_POINTER;
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
            case INT_POINTER:case STRING_POINTER:case BOOLEAN_POINTER:case METHOD:
                return 8;
            default:
                return -1;
        }
    }
}
