package framework.core;

import framework.core.annotations.*;
import framework.core.model.StringPair;
import framework.request.exceptions.AutowiredBeanException;
import framework.request.exceptions.AutowiredPrimitiveException;
import framework.request.exceptions.ExistingMethodAndPathException;
import framework.request.exceptions.LoadingDependencyException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DIEngine {

    public static Map<String, Object> singletons = new HashMap<>();
    public static Map<StringPair, StringPair> routes= new HashMap<>();
    public static List<Class> controllers = new ArrayList<>();

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

                    Object controller = dependencyInjection(cls, cls.getConstructor().newInstance());
                    if(controller == null)
                        continue;
                    DIEngine.singletons.put(comp, controller);
                    if(!DIEngine.controllers.contains(cls))
                        DIEngine.controllers.add(cls);
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

                            if(DIEngine.routes.keySet().contains(key))
                                throw new ExistingMethodAndPathException(cls.getName());
                            else DIEngine.routes.put(key, value);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Object dependencyInjection(Class cls, Object obj){
        Field[] fields = cls.getDeclaredFields();

        for(Field f: fields){
            if(f.isAnnotationPresent(Autowired.class)){
                try{
                    Class<?> fieldClass = f.getType();
                    if(fieldClass.isPrimitive())
                        throw new AutowiredPrimitiveException(cls);


                    Object fieldObject = null;
                    if(fieldClass.isAnnotationPresent(Service.class) ||
                            (fieldClass.isAnnotationPresent(Bean.class) && fieldClass.getAnnotation(Bean.class).scope().equals("singleton"))){
                        String[] name = fieldClass.getName().split("\\.");
                        if(DIEngine.singletons.containsKey(name[name.length-1]))
                            fieldObject = DIEngine.singletons.get(name[name.length-1]);
                        else{
                            fieldObject = fieldClass.getConstructor().newInstance();
                            DIEngine.singletons.put(name[name.length-1], fieldObject);
                        }
                    }
                    else if(fieldClass.isAnnotationPresent(Component.class) ||
                            (fieldClass.isAnnotationPresent(Bean.class) && fieldClass.getAnnotation(Bean.class).scope().equals("prototype"))){
                        fieldObject = fieldClass.getConstructor().newInstance();
                    }
                    if(fieldObject == null)
                        throw new AutowiredBeanException(fieldClass);

                    fieldObject = dependencyInjection(fieldClass, fieldObject);
                    f.setAccessible(true);
                    f.set(obj, fieldObject);
                    if(f.getAnnotation(Autowired.class).verbose())
                        System.out.println("Initialized <" + f.getType().toString().split(" ")[0] +"> <" + fieldClass.getName() + "> in <" + cls.getName() + "> on <" + (LocalDateTime.now()) + "> with <" + fieldObject.hashCode() + ">");

                } catch (InvocationTargetException e) {
                    throw new LoadingDependencyException(cls);
//                    throw new RuntimeException(e);
                } catch (InstantiationException e) {
                    throw new LoadingDependencyException(cls);
//                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new LoadingDependencyException(cls);
//                    throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                    throw new LoadingDependencyException(cls);
//                    throw new RuntimeException(e);
                }
            }
        }
        return obj;
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
