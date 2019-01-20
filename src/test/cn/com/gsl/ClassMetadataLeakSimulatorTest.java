package cn.com.gsl;

import cn.com.gsl.jvm8.ClassA;
import cn.com.gsl.jvm8.ClassAImpl;
import cn.com.gsl.jvm8.ClassAInvocationHandler;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * PermGenRemovalValidator
 *
 * @author Pierre-Hugues Charbonneau
 */
public class ClassMetadataLeakSimulatorTest {

    private static Map<String, ClassA> classLeakingMap = new HashMap<String, ClassA>();
    private final static int NB_ITERATIONS_DEFAULT = 50000;

    // FIXME: 2019/1/20 https://blog.csdn.net/yjl33/article/details/78890363
    /**
     * -Xmx3550m -Xms3550m -XX:MaxPermSize=32m
     */
    @Test
    public void test1() {
        System.out.println("Class metadata leak simulator");
        System.out.println("Author: Pierre-Hugues Charbonneau");
        System.out.println("http://javaeesupportpatterns.blogspot.com");

        int nbIterations = NB_ITERATIONS_DEFAULT;
        int index=0;
        try {
            for (int i = 0; i < nbIterations; i++) {
                String fictiousClassloaderJAR = "file:" + i + ".jar";

                URL[] fictiousClassloaderURL = new URL[]{new URL(fictiousClassloaderJAR)};

                // Create a new classloader instance
                URLClassLoader newClassLoader = new URLClassLoader(fictiousClassloaderURL);

                // Create a new Proxy instance
                ClassA t = (ClassA) Proxy.newProxyInstance(newClassLoader,
                        new Class<?>[]{ClassA.class},
                        new ClassAInvocationHandler(new ClassAImpl()));

                // Add the new Proxy instance to the leaking HashMap
                classLeakingMap.put(fictiousClassloaderJAR, t);
                index=i;
            }
        } catch (Throwable any) {
            System.out.println("ERROR: " + any+";i="+index);
        }
        System.out.println("Done!");
    }
}