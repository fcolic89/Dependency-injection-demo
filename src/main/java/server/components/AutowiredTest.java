package server.components;

import framework.core.annotations.Autowired;
import framework.core.annotations.Bean;
import framework.core.annotations.Component;
import framework.core.annotations.Service;

//@Bean(scope = "singleton")
//@Bean(scope = "prototype")
@Service
//@Component
public class AutowiredTest {

//    @Autowired
    public int atAttr;
    public String radi(){
        return "AutowiredTest works!";
    }
}
