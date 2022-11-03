package framework.request.exceptions;

public class ExistingMethodAndPathException extends RuntimeException{
    public ExistingMethodAndPathException(String className){
        super("There already exists a method with the same HTTP method and path in " + className + "!");
    }
}
