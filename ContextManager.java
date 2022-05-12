public class ContextManager {
    private Class currClass;
    private Method currMethod;

    public void enterContext(Class c){
        currClass = c;
    }

    public void enterContext(Method m){
        currMethod = m;
    }

    public void leaveContext() throws MiniJavaException{
        if(currMethod != null){
            currMethod = null;
        }else if(currClass != null){
            currClass = null;
        }else{
            throw new MiniJavaException("Internal Error Encountered.");
        }
    }

    public Class getClassCtx(){
        return currClass;
    }

    public Method getMethodCtx(){
        return currMethod;
    }
}
