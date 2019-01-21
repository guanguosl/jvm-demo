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
     * jdk7 -Xmx64m -Xms64m  -XX:MaxPermSize=32m
     */
    @Test
    public void test1() {
        this.test();
    }


    /**
     * jdk7 -Xmx64m -Xms64m -Xmn32m -XX:MaxPermSize=32m
     */
    @Test
    public void test2() {
       this.test();
    }
    /**
     * jdk6 -Xmx64m -Xms64m -Xmn32m -XX:MaxPermSize=32m
     */
    @Test
    public void test16() {
        this.test();
    }

    /**
     * -Xmx32m -Xms32m -XX:MaxPermSize=16m -XX:NewRatio=4
     * 设置年轻代（包括Eden和两个Survivor区）与年老代的比值（除去持久代）。
     * 设置为4，则年轻代与年老代所占比值为1：4，年轻代占整个堆栈的1/5
     */
    @Test
    public void testNewRatio() {
        this.test();
    }

    /**
     * -Xmx32m -Xms32m -XX:MaxPermSize=16m -XX:SurvivorRatio=2
     * 设置年轻代（包括Eden和两个Survivor区）与年老代的比值（除去持久代）。
     * 设置为4，则年轻代与年老代所占比值为1：4，年轻代占整个堆栈的1/5
     */
    @Test
    public void testSurvivorRatio() {
        this.test();
    }

    private void test(){
        System.out.println("Class metadata leak simulator");
        System.out.println("Author: Pierre-Hugues Charbonneau");
        System.out.println("http://javaeesupportpatterns.blogspot.com");

        int nbIterations = NB_ITERATIONS_DEFAULT;
        int index = 0;
        try {
            for (int i = 0; i < nbIterations; i++) {
                String fictiousClassloaderJAR = "file:" + i + ".jar";

                URL[] fictiousClassloaderURL = new URL[] { new URL(fictiousClassloaderJAR) };

                // Create a new classloader instance
                URLClassLoader newClassLoader = new URLClassLoader(fictiousClassloaderURL);

                // Create a new Proxy instance
                ClassA t = (ClassA) Proxy.newProxyInstance(
                        newClassLoader,
                        new Class<?>[] { ClassA.class },
                        new ClassAInvocationHandler(new ClassAImpl()));

                // Add the new Proxy instance to the leaking HashMap
                classLeakingMap.put(fictiousClassloaderJAR, t);
                index = i;
                Thread.sleep(10);
            }

        } catch (Throwable any) {
            System.out.println("ERROR: " + any + ";i=" + index);
        }
        System.out.println("Done!");
//        System.gc();
    }
}