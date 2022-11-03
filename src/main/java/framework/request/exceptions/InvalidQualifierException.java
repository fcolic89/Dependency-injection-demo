package framework.request.exceptions;

public class InvalidQualifierException extends RuntimeException{
    public InvalidQualifierException(String className, String fieldName){
        super("Invalid @Qualifier value in class: " + className + " on field: " + fieldName);
    }
}
