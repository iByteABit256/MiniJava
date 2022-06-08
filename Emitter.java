import java.io.IOException;
import java.io.Writer;

public class Emitter {
    private Writer writer;

    public Emitter(Writer writer) {
        this.writer = writer;
    }

    public void emit(Object o){
        try{
            writer.write(o.toString());
        }catch(IOException e){
            System.err.println("Object cannot be emitted");
        }
    }

    public void emitln(Object o){
        try{
            writer.write( o.toString() + "\n");
        }catch(IOException e){
            System.err.println("Object cannot be emitted");
        }
    }

    public void emitln(){
        try{
            writer.write( "\n");
        }catch(IOException e){
            System.err.println("Emitter error");
        }
    }

    public void close() throws IOException {
        this.writer.close();
    }

}
