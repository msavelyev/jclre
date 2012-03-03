package jclre.modify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClassFields {

    private static final Logger log = LoggerFactory.getLogger( ClassFields.class );

    private Map<String, Object> addedFieldsValues = Collections.synchronizedMap( new HashMap<String, Object>() );

    public void set( String fieldName, Object value ) {
        log.info( "setting " + fieldName + "=" + value );
        addedFieldsValues.put( fieldName, value );
    }

    public Object get( String fieldName ) {
        Object o = addedFieldsValues.get( fieldName );
        log.info( "getting " + fieldName + "=" + o );
        return o;
    }

    public void remove( String fieldName ) {
        log.info( "removing " + fieldName );
        addedFieldsValues.remove( fieldName );
    }
}
