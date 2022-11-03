package framework.request.exceptions;

public class DependencyEngineException extends RuntimeException{
    public DependencyEngineException(){
        super("Error while initializing dependency engine!");
    }
}
