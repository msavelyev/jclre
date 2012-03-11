package jclre.cache.field;

import jclre.modify.ClassFields;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StaticFieldValues {

    private Map<String, ClassFields> cache = new ConcurrentHashMap<String, ClassFields>();

    public ClassFields get( String className ) {
        if( !cache.containsKey( className ) ) {
            cache.put( className, new ClassFields() );
        }

        return cache.get( className );
    }

    public void add( String className, FieldDefinition fieldDefinition ) {
        ClassFields classFields = get( className );
        classFields.set( fieldDefinition.getName(), fieldDefinition.getDefaultValue() );
    }

    public void remove( String className, FieldDefinition fieldDefinition ) {
        ClassFields classFields = get( className );
        classFields.remove( fieldDefinition.getName() );
    }
}
