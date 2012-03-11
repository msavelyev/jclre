package jclre.cache;

import jclre.cache.field.ClassesFieldDefinitions;
import jclre.cache.field.FieldDefinition;
import jclre.cache.field.InstancesFieldValues;
import jclre.cache.field.StaticFieldValues;
import jclre.modify.ClassFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldsCache {

    private static final Logger log = LoggerFactory.getLogger( FieldsCache.class );

    private ClassesFieldDefinitions fieldsDef = new ClassesFieldDefinitions();
    private InstancesFieldValues instancesFieldValues = new InstancesFieldValues();
    private StaticFieldValues staticFieldValues = new StaticFieldValues();

    @SuppressWarnings( "unused" )
    public ClassFields create( String className ) {
        log.info( "creating " + className );

        ClassFields classFields = fieldsDef.create( className );

        instancesFieldValues.addInstance( className, classFields );

        log.info( "created for " + className + " " + classFields );
        return classFields;
    }

    @SuppressWarnings( "unused" )
    public ClassFields createStatic( String className ) {
        log.info( "creating static " + className );

        ClassFields classFields = staticFieldValues.get( className );

        log.info( "created static for " + className + " " + classFields );
        return classFields;
    }

    public void addField( String className, FieldDefinition fieldDefinition ) {
        log.info( "adding field " + fieldDefinition.getName() + " to " + className );

        fieldsDef.add( className, fieldDefinition );
        instancesFieldValues.add( className, fieldDefinition );
    }

    public void removeField( String className, FieldDefinition fieldDefinition ) {
        log.info( "removing field " + fieldDefinition.getName() + " from " + className );

        fieldsDef.remove( className, fieldDefinition );
        instancesFieldValues.remove( className, fieldDefinition );
    }

    public void addStaticField( String className, FieldDefinition fieldDefinition ) {
        log.info( "adding static field " + fieldDefinition.getName() + " to " + className );

        staticFieldValues.add( className, fieldDefinition );
    }

    public void removeStaticField( String className, FieldDefinition fieldDefinition ) {
        log.info( "removing static field " + fieldDefinition.getName() + " from " + className );

        staticFieldValues.remove( className, fieldDefinition );
    }

}
