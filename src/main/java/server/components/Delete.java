package server.components;

import framework.core.DIEngine;
import framework.core.annotations.Autowired;
import framework.core.annotations.Controller;
import framework.core.annotations.Get;
import framework.core.annotations.Path;
import framework.response.HttpResponse;

@Controller
public class Delete {

    @Autowired
    AutowiredTest at;

    @Get
    @Path(path = "/delete")
    public String delete(){
        return HttpResponse.ok( "delete radi! i " + at.radi() + "i velicina singleton-a je: " + DIEngine.singletons.size());
    }
}
