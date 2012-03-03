package jclre.cache.field;

import jclre.modify.ClassFields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstancesFieldValues {

    private Map<String, List<ClassFields>> fieldValuesByClass = new HashMap<String, List<ClassFields>>();

    public void add( String className, FieldDefinition fieldDefinition ) {
        if( !fieldValuesByClass.containsKey( className ) ) {
            fieldValuesByClass.put( className, new ArrayList<ClassFields>() );
        }

        for( ClassFields classFields:  fieldValuesByClass.get( className ) ) {
            classFields.set( fieldDefinition.getName(), fieldDefinition.getDefaultValue() );
        }
    }

    public void remove( String className, FieldDefinition fieldDefinition ) {
        for( ClassFields classFields:  fieldValuesByClass.get( className ) ) {
            classFields.remove( fieldDefinition.getName() );
        }
    }

    public void addInstance( String className, ClassFields classFields ) {
        if( !fieldValuesByClass.containsKey( className ) ) {
            fieldValuesByClass.put( className, new ArrayList<ClassFields>() );
        }

        fieldValuesByClass.get( className ).add( classFields );
    }

}
