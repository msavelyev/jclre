package jclre.cache;

import jclre.cache.method.CurrentMethods;
import jclre.cache.method.InstanceMethods;
import jclre.modify.ClassMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodsCache {

    private static final Logger log = LoggerFactory.getLogger( MethodsCache.class );

    private CurrentMethods currentMethods = new CurrentMethods();
    private InstanceMethods instanceMethods = new InstanceMethods();

    @SuppressWarnings( "unused" )
    public ClassMethods create( Object object ) {
        Class aClass = currentMethods.get( object.getClass().getName() );
        try {
            ClassMethods classMethods = ( ClassMethods ) aClass.newInstance();
            classMethods.setObject( object );
            instanceMethods.addInstance( object );
            return classMethods;
        } catch( InstantiationException e ) {
            log.error( "new instance", e );
        } catch( IllegalAccessException e ) {
            log.error( "illegal access", e );
        }
        return null;
    }

    public void update( String className, Class clazz ) {
        currentMethods.set( className, clazz );
        instanceMethods.update( className, clazz );
    }

}
