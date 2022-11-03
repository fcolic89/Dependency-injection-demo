package framework.request.exceptions;

public class AutowiredBeanException extends RuntimeException{
    public AutowiredBeanException(Class<?> cls) {
        super("Can't put @Autowired on an attribute that is not a @Bean (@Service or @Component)! Error in: " + cls.getName());
    }
}
