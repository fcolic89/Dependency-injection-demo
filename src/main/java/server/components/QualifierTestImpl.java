package server.components;

import framework.core.annotations.Bean;
import framework.core.annotations.Qualifier;

@Qualifier(value = "test")
@Bean(scope = "prototype")
public class QualifierTestImpl implements QualifierTestInt {
    @Override
    public String qualifierTest() {
        return "Qualifier test passed!";
    }
}
