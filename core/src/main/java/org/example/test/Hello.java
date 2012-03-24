package org.example.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicInteger;

public class Hello {

    private static final Logger log = LoggerFactory.getLogger( Hello.class );

    private AtomicInteger counter = new AtomicInteger();

//    private static int b = 2;
//    private String b = "omg";
//    private int c = 1;
//    private boolean d = true;
//    private float e = 5;
//    private Object d = new Object();
//    private int d = 1;
//    private int e = 5;
//    private Lol lol = new Lol();

    public String hello( String something ) {
//        log.info( "b=" + b );
//        b++;
//        log.info( "b=" + b );
//        b++;
//        log.info( "b=" + b );

//        log.info( "b=" + b );
//        log.info( "c=" + c );
//        log.info( "d=" + d );
//        log.info( "e=" + e );
//        return "Hello, " + rofl() + " " + something + "! ";
        return "Hello, " + something + "! ";
    }

    public void dump() {
        try {
            Class<? extends Hello> c = getClass();
            log.info( "class " + c.getName() );
            Method[] ms = c.getDeclaredMethods();
            for( Method m: ms ) {
                String params = "";
                for( Class cc: m.getParameterTypes() ) {
                    params += cc.getName() + ", ";
                }
                log.info( "method " + Modifier.toString( m.getModifiers() ) + " " + m.getReturnType().getName() + " " + m.getName() + "(" + params + ")" );
            }
            for( Field f: c.getDeclaredFields() ) {
                log.info( "field " + Modifier.toString( f.getModifiers() ) + " " + f.getType().getName() + " " + f.getName() + " = " + f.get( this ) );
            }
            for( Class cc: c.getDeclaredClasses() ) {
                log.info( "inner class " + cc.getName() );
            }

//            String s = rofl();

//            ClassPool aDefault = ClassPool.getDefault();
//
//            CtClass ctClass = aDefault.get( "org.example.test.Hello" );
//            CtMethod[] declaredMethods = ctClass.getDeclaredMethods();
//            declaredMethods.getClass();
//            ctClass.defrost();
//            ctClass.toBytecode();
//            declaredMethods = ctClass.getDeclaredMethods();
//            declaredMethods.getClass();

//            String d = s;
        } catch( Throwable e ) {
            e.printStackTrace();
        }
    }

    public void lol() {
        Exception exception = new Exception();

        Object o = exception;
    }

//    public void a( String b ) {
//        log.info( b );
//    }
//
//    public String rofl() {
//        return "rofl";
//    }
//
//    public String rofl1() {
//        return "rofl1";
//    }

    public class Hello2 {
        public String a() {
            return "a";
        }
    }

}
