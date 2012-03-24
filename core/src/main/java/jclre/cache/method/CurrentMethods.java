package jclre.cache.method;

import jclre.modify.ClassMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CurrentMethods {

    private static final Logger log = LoggerFactory.getLogger( CurrentMethods.class );

    private Map<String, Class> cache = new HashMap<String, Class>();

    public void set( String className, Class clazz ) {
        cache.put( className, clazz );
    }

    public Class get( String className ) {
        return cache.get( className );
    }

}
