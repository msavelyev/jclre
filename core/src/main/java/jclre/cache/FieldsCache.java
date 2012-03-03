package jclre.cache;

import jclre.cache.field.ClassesFieldDefinitions;
import jclre.cache.field.FieldDefinition;
import jclre.cache.field.InstancesFieldValues;
import jclre.modify.ClassFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FieldsCache {

    private static final Logger log = LoggerFactory.getLogger( FieldsCache.class );

    private ClassesFieldDefinitions fieldsDef = new ClassesFieldDefinitions();
    private InstancesFieldValues instancesFieldValues = new InstancesFieldValues();

    public ClassFields create( String className ) {
        log.info( "creating " + className );

        ClassFields classFields = fieldsDef.create( className );

        instancesFieldValues.addInstance( className, classFields );

        log.info( "created for " + className + " " + classFields );
        return classFields;
    }

    public void addField( String className, FieldDefinition fieldDefinition ) {
        log.info( "adding field " + fieldDefinition.getName() + " to " + className );

        fieldsDef.add( className, fieldDefinition );
        instancesFieldValues.add( className, fieldDefinition );

    }

}
