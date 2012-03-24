package jclre.cache.method;

import jclre.modify.ClassMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstanceMethods {

    private static final Logger log = LoggerFactory.getLogger( InstanceMethods.class );

    public Map<String, List<Object>> instances = new HashMap<String, List<Object>>();

    public void update( String className, Class clazz ) {
        if( instances.containsKey( className ) ) {
            for( Object obj: instances.get( className ) ) {
                try {
                    Field addedMethods = obj.getClass().getDeclaredField( "___ADDED_METHODS___" );
                    ClassMethods classMethods = ( ClassMethods ) clazz.newInstance();
                    classMethods.setObject( obj );
                    addedMethods.set( obj, classMethods );
                } catch( NoSuchFieldException e ) {
                    log.error( "no added methods", e );
                } catch( IllegalAccessException e ) {
                    log.error( "illegal access", e );
                } catch( InstantiationException e ) {
                    log.error( "new instance", e );
                }
            }
        }
    }

    public void addInstance( Object object ) {
        String className = object.getClass().getName();
        if( !instances.containsKey( className ) ) {
            instances.put( className, new ArrayList<Object>() );
        }

        instances.get( className ).add( object );
    }

}
