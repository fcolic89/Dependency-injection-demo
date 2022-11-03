package framework.request.exceptions;

public class AutowiredMissingQualifierException extends RuntimeException{
    public AutowiredMissingQualifierException(String className, String fieldName){
        super("Missing @Qualifier annotation in class: " + className + ", on field: " + fieldName);
    }
}
