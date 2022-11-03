package framework.request.exceptions;

public class AutowiredBeanException extends RuntimeException{
    public AutowiredBeanException(Class cls) {
        super("Cant @Autowired an attribute that is not a @Bean (@Service or @Component)! Error in: " + cls.getName());
    }
}
