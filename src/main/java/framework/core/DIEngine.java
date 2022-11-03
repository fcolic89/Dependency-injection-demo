package framework.core;

import framework.core.annotations.*;
import framework.core.model.StringPair;
import framework.request.exceptions.*;

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

    private String packageName = "server.components";
    public static Map<String, Object> singletons = new HashMap<>();
    public static Map<StringPair, StringPair> routes= new HashMap<>();
    public static List<Class<?>> controllers = new ArrayList<>();
    private List<String> classes = new ArrayList<>();
    private Map<String, Class<?>> dependencyContainer = new HashMap<>();

    public DIEngine(){
        List<String> tmp = getClassNames();
        if(tmp != null) {
            this.classes.addAll(tmp);
            init();
        }
        else throw new DependencyEngineException();
    }

    private void init(){
        initDependencyContainer();
        createControllers();
    }

    private void initDependencyContainer() {
        for(String c: this.classes){
            try {
                Class<?> cls = Class.forName(packageName + "." + c);

                if(cls.isAnnotationPresent(Qualifier.class)){
                    String qualifier = cls.getAnnotation(Qualifier.class).value();

                    if(dependencyContainer.containsKey(qualifier))
                        throw new DuplicateQualifierException(cls.getName(), dependencyContainer.get(qualifier).getName());
                    else
                        dependencyContainer.put(qualifier, cls);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void createControllers() {
        //pronolazenje kontrolera
        for (String comp : classes) {
            try {
                Class<?> cls = Class.forName(packageName + "." + comp);
                if(cls.isAnnotationPresent(Controller.class)){

                    //Instance controllers
                    Object controller = dependencyInjection(cls, cls.getConstructor().newInstance());
                    if(controller == null)
                        continue;
                    DIEngine.singletons.put(comp, controller);
                    if(!DIEngine.controllers.contains(cls))
                        DIEngine.controllers.add(cls);

                    //Instance routes
                    Method[] methods = cls.getDeclaredMethods();
                    for(Method m: methods){
                        if(m.isAnnotationPresent(Path.class)){
                            StringPair value = new StringPair(comp, m.getName());
                            StringPair key = null;

                            if(m.isAnnotationPresent(Get.class))
                                key = new StringPair("GET", m.getAnnotation(Path.class).path());
                            if (m.isAnnotationPresent(Post.class))
                                key = new StringPair("POST", m.getAnnotation(Path.class).path());

                            if(DIEngine.routes.containsKey(key))
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

    private Object dependencyInjection(Class<?> cls, Object obj){
        Field[] fields = cls.getDeclaredFields();

        for(Field f: fields) {
            if (f.isAnnotationPresent(Autowired.class)) {
                try {
                    Class<?> fieldClass = f.getType();

                    //Primitive type check
                    if (fieldClass.isPrimitive())
                        throw new AutowiredPrimitiveException(cls);

                    //Qualifier check
                    if (fieldClass.isInterface()) {
                        if (f.isAnnotationPresent(Qualifier.class)) {
                            Class<?> cls1 = dependencyContainer.get(f.getAnnotation(Qualifier.class).value());
                            if (cls1 == null) throw new InvalidQualifierException(cls.getName(), f.getName());

                            fieldClass = cls1;
                        } else {
                            throw new AutowiredMissingQualifierException(cls.getName(), f.getName());
                        }
                    }

                    //Instancing object
                    Object fieldObject = null;
                    if (fieldClass.isAnnotationPresent(Service.class) ||
                            (fieldClass.isAnnotationPresent(Bean.class) && fieldClass.getAnnotation(Bean.class).scope().equals("singleton"))) {
                        String[] name = fieldClass.getName().split("\\.");
                        if (DIEngine.singletons.containsKey(name[name.length - 1]))
                            fieldObject = DIEngine.singletons.get(name[name.length - 1]);
                        else {
                            fieldObject = fieldClass.getConstructor().newInstance();
                            DIEngine.singletons.put(name[name.length - 1], fieldObject);
                        }
                    } else if (fieldClass.isAnnotationPresent(Component.class) ||
                            (fieldClass.isAnnotationPresent(Bean.class) && fieldClass.getAnnotation(Bean.class).scope().equals("prototype"))) {
                        fieldObject = fieldClass.getConstructor().newInstance();
                    }
                    if (fieldObject == null)
                        throw new AutowiredBeanException(fieldClass);

                    //Instancing objects dependencies
                    fieldObject = dependencyInjection(fieldClass, fieldObject);
                    if(fieldObject == null)
                        throw new LoadingDependencyException(fieldClass);

                    //Injecting object
                    f.setAccessible(true);
                    f.set(obj, fieldObject);
                    if (f.getAnnotation(Autowired.class).verbose())
                        System.out.println("Initialized <" + f.getType().toString().split(" ")[0] + "> <" + fieldClass.getName() + "> in <" + cls.getName() + "> on <" + (LocalDateTime.now()) + "> with <" + fieldObject.hashCode() + ">");

                } catch (InvocationTargetException | InstantiationException
                         | IllegalAccessException | NoSuchMethodException e) {
                    return null;
//                    throw new LoadingDependencyException(cls);
//                    throw new RuntimeException(e);
                }
            }
        }
        return obj;
    }

    private List<String> getClassNames(){

        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("\\.", "/"));
        BufferedReader reader;
        if(stream != null)
           reader  = new BufferedReader(new InputStreamReader(stream));
        else
            return null;

        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(name -> name.substring(0, name.lastIndexOf(".")))
                .collect(Collectors.toList());
    }
}
