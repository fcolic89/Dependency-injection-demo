package framework.request.exceptions;

public class LoadingDependencyException extends RuntimeException{
    public LoadingDependencyException(Class cls){
        super("There was an error while loading dependencies for: " + cls.getName());
    }
}
