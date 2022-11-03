package server.components;

import framework.core.annotations.*;

@Controller
public class TestContoller {

    @Autowired
    AutowiredTest at;

    @Autowired
    @Qualifier(value = "test")
    QualifierTestInt q;

    @Get
    @Path(path = "/test1")
    public String test1(){
        return "Test1";
    }

    @Get
    @Path(path = "/test2")
    public String test2(){
        return at.radi();
    }


    @Get
    @Path(path = "/test3")
    public String test3(){
        return q.qualifierTest();
    }
}
