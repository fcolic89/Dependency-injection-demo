package framework.request.exceptions;

public class DuplicateQualifierException extends RuntimeException{
    public DuplicateQualifierException(String cls1, String cls2){
        super("Duplicate qualifier value in class: " + cls1 + " and class: " + cls2);
    }
}
