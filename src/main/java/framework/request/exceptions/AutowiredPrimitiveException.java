package framework.request.exceptions;

public class AutowiredPrimitiveException extends RuntimeException{

    public AutowiredPrimitiveException(Class cls){
        super("Cant use dependency injection on primitive types in: " + cls.getName());
    }

}
