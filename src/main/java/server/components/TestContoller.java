package server.components;

import framework.core.annotations.*;
import framework.response.HttpResponse;

@Controller
public class TestContoller {

    @Autowired
    AutowiredTest at;

    @Autowired
    @Qualifier(value = "test")
    QualifierTestInt q;

//    @Autowired
    private int a;

    @Get
    @Path(path = "/test1")
    public String test1(){
        return HttpResponse.ok("Test1");
    }

    @Get
    @Path(path = "/test2")
    public String test2(){
      return HttpResponse.ok(at.radi());
    }


    @Get
    @Path(path = "/test3")
    public String test3(){
        return HttpResponse.ok(q.qualifierTest());
    }
}
