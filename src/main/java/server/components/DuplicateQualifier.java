package server.components;

import framework.core.annotations.Bean;
import framework.core.annotations.Qualifier;

@Bean(scope = "prototype")
//@Qualifier(value = "test")
public class DuplicateQualifier implements QualifierTestInt{
    @Override
    public String qualifierTest() {
        return "Qualifier test passed!";
    }
}
