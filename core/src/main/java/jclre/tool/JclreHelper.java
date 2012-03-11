package jclre.tool;

import javassist.*;
import jclre.cache.FieldsCache;
import jclre.cache.field.FieldDefinition;
import jclre.modify.ClassFields;
import jclre.tool.helper.FieldsCompareResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JclreHelper {

    private static final Logger log = LoggerFactory.getLogger( JclreHelper.class );

    private ClassPool classPool;
    private FieldsCache fieldsCache;
    private jclre.bytecode.Modifier modifier;

    public JclreHelper( ClassPool classPool, FieldsCache fieldsCache, jclre.bytecode.Modifier modifier ) {
        this.classPool = classPool;
        this.fieldsCache = fieldsCache;
        this.modifier = modifier;
    }

    public String standardizeClassName( String classNameWithSlashes ) {
        return classNameWithSlashes.replace( '/', '.' );
    }

    public boolean systemClassName( String className ) {
        List<String> systemPrefixes = Arrays.asList( "java.", "sun.", "jclre.", "org.slf4j." );
        for( String systemPrefx: systemPrefixes ) {
            if( className.startsWith( systemPrefx ) ) {
                return true;
            }
        }
        return false;
    }

    public CtClass createCtClass( byte[] bytecode ) throws IOException, NotFoundException {
        return classPool.makeClass( new ByteArrayInputStream( bytecode ) );
    }

    public void addFields( CtClass ctClass ) {
        try {
            CtField field = new CtField( classPool.get( ClassFields.class.getName() ), "___ADDED_FIELDS___", ctClass );
            field.setModifiers( java.lang.reflect.Modifier.PUBLIC );
            ctClass.addField(
                field,
                "jclre.instrumentation.Jclre.getInstance().getFieldsCache().create( \"" + ctClass.getName() + "\" )"
            );

            CtField staticField = new CtField(
                classPool.get( ClassFields.class.getName() ),
                "___ADDED_STATIC_FIELDS___",
                ctClass
            );
            staticField.setModifiers( java.lang.reflect.Modifier.PUBLIC | java.lang.reflect.Modifier.STATIC );
            ctClass.addField(
                staticField,
                "jclre.instrumentation.Jclre.getInstance().getFieldsCache().createStatic( \"" + ctClass.getName() + "\" )"
            );
        } catch( CannotCompileException e ) {
            log.error( "can't compile " + ctClass.getName(), e );
        } catch( NotFoundException e ) {
            log.error( "not found " + ctClass.getName(), e );
        } catch( RuntimeException e ) {
            log.error( "runtime " + ctClass.getName(), e );
        }
    }

    public FieldsCompareResult compareFields( CtClass newClass, CtClass oldClass ) {
        CtField[] newFields = newClass.getDeclaredFields();
        CtField[] oldFields = oldClass.getDeclaredFields();

        FieldsCompareResult fieldsCompareResult = new FieldsCompareResult();

        for( CtField newField: newFields ) {
            try {
                oldClass.getDeclaredField( newField.getName() );
            } catch( NotFoundException e ) {
                log.info( "added field " + newField.getName() );
                fieldsCompareResult.addedField( newField );
            }
        }

        for( CtField oldField: oldFields ) {
            try {
                newClass.getDeclaredField( oldField.getName() );
            } catch( NotFoundException e ) {
                log.info( "removed field " + oldField.getName() );
                fieldsCompareResult.removedField( oldField );
            }
        }

        return fieldsCompareResult;
    }

    public void fixFields( CtClass ctClass, FieldsCompareResult fieldsCompareResult ) {
        for( CtField removedField: fieldsCompareResult.getRemovedFields() ) {
            try {
                if( java.lang.reflect.Modifier.isStatic( removedField.getModifiers() ) ) {
                    fieldsCache.removeStaticField(
                        ctClass.getName(),
                        makeFieldDef( removedField )
                    );
                } else {
                    fieldsCache.removeField(
                        ctClass.getName(),
                        makeFieldDef( removedField )
                    );
                }

                CtField newField = new CtField( removedField, ctClass );
                ctClass.addField( newField );
            } catch( CannotCompileException e ) {
                log.error( "can't compile", e );
            }
        }

        List<String> addedFieldsNames = new ArrayList<String>();
        for( CtField addedField: fieldsCompareResult.getAddedFields() ) {
            addedFieldsNames.add( addedField.getName() );
            try {
                if( java.lang.reflect.Modifier.isStatic( addedField.getModifiers() ) ) {
                    fieldsCache.addStaticField(
                        ctClass.getName(),
                        makeFieldDef( addedField )
                    );
                } else {
                    fieldsCache.addField(
                        ctClass.getName(),
                        makeFieldDef( addedField )
                    );
                }

                ctClass.removeField( addedField );
            } catch( NotFoundException e ) {
                log.error( "field not found " + addedField.getName(), e );
            }
        }

        modifier.modifyReadersAndWriters( ctClass, addedFieldsNames );
    }

    private FieldDefinition makeFieldDef( CtField ctField ) {
        CtClass ctFieldType = null;
        try {
            ctFieldType = ctField.getType();
        } catch( NotFoundException e ) {
            log.error( "type of field " + ctField.getName() + " not found", e );
            return new FieldDefinition(
                ctField.getName(),
                null, // TODO
                null // TODO
            );
        }

        Object defaultValue = null;
        if( ctFieldType.isPrimitive() ) {
            if( ctFieldType.getName().equals( "boolean" ) ) {
                defaultValue = false;
            } else if( ctFieldType.getName().equals( "float" ) ) {
                defaultValue = ( float ) 0;
            } else if( ctFieldType.getName().equals( "double" ) ) {
                defaultValue = ( double ) 0;
            } else if( ctFieldType.getName().equals( "int" ) ) {
                defaultValue = 0;
            } else if( ctFieldType.getName().equals( "long" ) ) {
                defaultValue = ( long ) 0;
            } else if( ctFieldType.getName().equals( "byte" ) ) {
                defaultValue = ( byte ) 0;
            } else if( ctFieldType.getName().equals( "char" ) ) {
                defaultValue = ( char ) 0;
            } else if( ctFieldType.getName().equals( "short" ) ) {
                defaultValue = ( short ) 0;
            }
        }

        return new FieldDefinition(
            ctField.getName(),
            null, // TODO
            defaultValue // TODO
        );

    }
}
