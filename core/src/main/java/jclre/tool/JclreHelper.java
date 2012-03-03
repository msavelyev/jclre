package jclre.tool;

import javassist.*;
import jclre.bytecode.Analyzer;
import jclre.cache.FieldsCache;
import jclre.cache.field.FieldDefinition;
import jclre.modify.ClassFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class JclreHelper {

    private static final Logger log = LoggerFactory.getLogger( JclreHelper.class );

    private ClassPool classPool;
    private FieldsCache fieldsCache;
    private Analyzer analyzer;

    public JclreHelper( ClassPool classPool, FieldsCache fieldsCache, Analyzer analyzer ) {
        this.classPool = classPool;
        this.fieldsCache = fieldsCache;
        this.analyzer = analyzer;
    }

    public String standardizeClassName( String classNameWithSlashes ) {
        return classNameWithSlashes.replace( '/', '.' );
    }

    public boolean systemClassName( String className ) {
        List<String> systemPrefixes = Arrays.asList( "java.", "sun.", "jclre.instrumentation.", "org.slf4j." );
        for( String systemPrefx: systemPrefixes ) {
            if( className.startsWith( systemPrefx ) ) {
                return true;
            }
        }
        return false;
    }

    public CtClass createCtClass( String className, byte[] bytecode ) throws IOException, NotFoundException {
//        CtClass ctClass1 = classPool.get( className );
//        ctClass1.defrost();
        CtClass ctClass = classPool.makeClass( new ByteArrayInputStream( bytecode ) );
//        ctClass.defrost();
        return ctClass;
    }

    public void addFields( CtClass ctClass ) {
        try {
            CtField field = new CtField( classPool.get( ClassFields.class.getName() ), "___ADDED_FIELDS___", ctClass );
            field.setModifiers( Modifier.PUBLIC );
            ctClass.addField(
                field,
                "jclre.instrumentation.Jclre.getInstance().getFieldsCache().create( \"" + ctClass.getName() + "\" )"
            );
        } catch( CannotCompileException e ) {
            log.error( "can't compile " + ctClass.getName(), e );
        } catch( NotFoundException e ) {
            log.error( "not found " + ctClass.getName(), e );
        } catch( RuntimeException e ) {
            log.error( "runtime " + ctClass.getName(), e );
        }
    }

    public void compareFields( CtClass newClass, CtClass oldClass ) {
        String className = newClass.getName();
        CtField[] newFields = newClass.getDeclaredFields();
        CtField[] oldFields = oldClass.getDeclaredFields();

        for( CtField newField: newFields ) {
            try {
                oldClass.getDeclaredField( newField.getName() );
            } catch( NotFoundException e ) {
                log.info( "added field " + newField.getName() );
                try {
                    CtClass type = newField.getType();
                    log.info( "added field type " + type.getName() );
                    fieldsCache.addField(
                        className,
                        new FieldDefinition(
                            newField.getName(),
                            null,
                            analyzer.analyzeInitialValue( newClass, newField.getName() )
                        )
                    );
                    newClass.removeField( newField );
                } catch( NotFoundException e1 ) {
                    log.error( "field not found " + newField.getName(), e1 );
//                } catch( CannotCompileException e1 ) {
//                    log.error( "can't compile", e1 );
                }
            }
        }

        for( CtField oldField: oldFields ) {
            try {
                newClass.getDeclaredField( oldField.getName() );
            } catch( NotFoundException e ) {
                log.info( "removed field " + oldField.getName() );
                try {
                    CtField newField = new CtField( oldField, newClass );
                    newClass.addField( newField );
                } catch( CannotCompileException e1 ) {
                    log.error( "can't compile", e1 );
                }
            }
        }
    }
}
