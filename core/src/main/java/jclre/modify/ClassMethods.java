package jclre.modify;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassMethods {

    private Object object;

    public MethodWrapper find( String methodName, Class<?> ... types ) {
        FastClass fastClass = FastClass.create( getClass() );
        try {
            Method method = getClass().getMethod( methodName, types );
            FastMethod fastMethod = fastClass.getMethod( method );
            return new MethodWrapper( fastMethod );
        } catch( NoSuchMethodException e ) {
            throw new RuntimeException( e );
        }
    }

    public void setObject( Object object ) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public class MethodWrapper {

        private FastMethod method;

        public MethodWrapper( FastMethod method ) {
            this.method = method;
        }

        public Object invoke( Object ... args ) {
            try {
                return method.invoke( this, args );
            } catch( InvocationTargetException e ) {
                throw new RuntimeException( e );
            }
        }
    }

}
