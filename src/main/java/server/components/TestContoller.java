package server.components;

import framework.core.annotations.Autowired;
import framework.core.annotations.Controller;
import framework.core.annotations.Get;
import framework.core.annotations.Path;

@Controller
public class TestContoller {

    @Autowired
    AutowiredTest at;

    @Get
    @Path(path = "/test1")
    public String test123(){
        return "Test1";
    }

    @Get
    @Path(path = "/test2")
    public String test12(){
        return at.radi();
    }
}
