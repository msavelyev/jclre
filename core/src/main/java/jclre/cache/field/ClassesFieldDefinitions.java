package jclre.cache.field;

import jclre.modify.ClassFields;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassesFieldDefinitions {

    private Map<String, ClassFieldDefinitions> classFieldDefinitionsMap
        = new ConcurrentHashMap<String, ClassFieldDefinitions>();

    public void add( String className, FieldDefinition fieldDefinition ) {
        if( !classFieldDefinitionsMap.containsKey( className ) ) {
            classFieldDefinitionsMap.put( className, new ClassFieldDefinitions() );
        }

        ClassFieldDefinitions classFieldDefinitions = classFieldDefinitionsMap.get( className );
        classFieldDefinitions.add( fieldDefinition );
    }

    public void remove( String className, FieldDefinition fieldDefinition ) {
        ClassFieldDefinitions classFieldDefinitions = classFieldDefinitionsMap.get( className );
        classFieldDefinitions.remove( fieldDefinition );
    }

    public ClassFields create( String className ) {
        ClassFields classFields = new ClassFields();

        if( classFieldDefinitionsMap.containsKey( className ) ) {
            for( FieldDefinition fieldDefinition: classFieldDefinitionsMap.get( className ).getAll() ) {
                classFields.set( fieldDefinition.getName(), fieldDefinition.getDefaultValue() );
            }
        }

        return classFields;
    }

}
