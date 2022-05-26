public class LLVM_Converter {

    private SymbolTable st;

    public LLVM_Converter(SymbolTable st) {
        this.st = st;
    }

    public void generateLLVM(){
        st.showVTable();

        st.getClassTable().values().forEach(c -> c.getMethods().values().forEach(m -> {
                m.setLLVM_method_head();
                System.out.println(m.getLLVM_method_head());
                System.out.printf("{\n\t---\n}\n\n");
        }));
    }

}
