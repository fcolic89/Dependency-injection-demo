package server.components;

import framework.core.annotations.Autowired;
import framework.core.annotations.Bean;

//@Bean(scope = "singleton")

@Bean(scope = "prototype")
public class AutowiredTest {

//    @Autowired
    public int atAttr;
    public String radi(){
        return "AutowiredTest radi!";
    }
}
