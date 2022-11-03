package framework.core;

import framework.core.annotations.Controller;
import framework.core.annotations.Get;
import framework.core.annotations.Path;
import framework.core.annotations.Post;
import framework.core.model.StringPair;
import framework.request.exceptions.ExistingMethodAndPathException;
import server.Server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DIEngine {

    private String packageName = "server.components";
    private List<String> classes = new ArrayList<>();

    public DIEngine(){
        this.classes.addAll(getClassNames());
        init();
    }

    private void init(){
        createControllers();
    }

    private void createControllers() {
        //pronolazenje kontrolera
        for (String comp : classes) {
            try {
                Class<?> cls = Class.forName(packageName + "." + comp);

                if(cls.isAnnotationPresent(Controller.class)){
                    Server.singletons.put(comp, cls.getConstructor().newInstance());
                    if(!Server.controllers.contains(cls))
                        Server.controllers.add(cls);
                    //instanciranje putanji
                    Method[] methods = cls.getDeclaredMethods();
                    for(Method m: methods){
                        if(m.isAnnotationPresent(Path.class)){
                            StringPair value = new StringPair(comp, m.getName());
                            StringPair key = null;

                            if(m.isAnnotationPresent(Get.class))
                                key = new StringPair("GET", m.getAnnotation(Path.class).path());
                            if (m.isAnnotationPresent(Post.class))
                                key = new StringPair("POST", m.getAnnotation(Path.class).path());

                            if(Server.routes.keySet().contains(key))
                                throw new ExistingMethodAndPathException(cls.getName());
                            else Server.routes.put(key, value);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void dependencyInjection(){

    }

    private List<String> getClassNames(){

        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(name -> name.substring(0, name.lastIndexOf(".")))
                .collect(Collectors.toList());
    }
}
