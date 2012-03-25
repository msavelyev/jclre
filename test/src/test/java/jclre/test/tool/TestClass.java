package jclre.test.tool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestClass {

    private Class<?> clazz;
    private Object object;

    public TestClass( String fullClassName ) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        clazz = Class.forName( fullClassName );
        object = clazz.newInstance();
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public <T> T call( String methodName, Class<T> returnType ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method declaredMethod = clazz.getDeclaredMethod( methodName );
        return ( T ) declaredMethod.invoke( object );
    }

    public void call( String methodName ) {

    }
}
