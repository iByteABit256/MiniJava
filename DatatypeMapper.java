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
}
