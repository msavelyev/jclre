package jclre.tool;

import javassist.*;
import jclre.cache.FieldsCache;
import jclre.cache.OriginalBytecodeCache;
import jclre.cache.field.FieldDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;

public class JclreClassTransformer implements ClassFileTransformer {

    private static final Logger log = LoggerFactory.getLogger( JclreClassTransformer.class );

    private ClassPool classPool;
    private OriginalBytecodeCache originalBytecodeCache;
    private JclreHelper jclreHelper;
    private FieldsCache fieldsCache;

    public JclreClassTransformer(
        ClassPool classPool,
        OriginalBytecodeCache originalBytecodeCache,
        JclreHelper jclreHelper,
        FieldsCache fieldsCache
    ) {
        this.classPool = classPool;
        this.originalBytecodeCache = originalBytecodeCache;
        this.jclreHelper = jclreHelper;
        this.fieldsCache = fieldsCache;
    }

    @Override
    public byte[] transform( ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer ) throws IllegalClassFormatException {
        className = jclreHelper.standardizeClassName( className );

        if( jclreHelper.systemClassName( className ) ) {
            return classfileBuffer;
        }

        log.info( "transforming " + className + " / "  );

        try {
            CtClass newClass = jclreHelper.createCtClass( className, classfileBuffer );
            newClass.detach();
            jclreHelper.addFields( newClass );

            for( CtField f: newClass.getDeclaredFields() ) {
                log.info( "new field: " + f.getName() );
            }

            if( !originalBytecodeCache.contains( className ) ) {
                log.info( "first time " + className );
                originalBytecodeCache.put( className, classfileBuffer );
            } else {
                log.info( "transformed before " + className );

                CtClass oldClass = originalBytecodeCache.getCtClass( className );
                oldClass.detach();
                jclreHelper.addFields( oldClass );

                for( CtField f: oldClass.getDeclaredFields() ) {
                    log.info( "old field: " + f.getName() );
                }

                jclreHelper.compareFields( newClass, oldClass );
                for( CtField f: newClass.getDeclaredFields() ) {
                    log.info( "compared field: " + f.getName() );
                }
            }

//            jclreHelper.addFields( newClass );

            for( CtField f: newClass.getDeclaredFields() ) {
                log.info( "modified field: " + f.getName() );
            }

            return newClass.toBytecode();
        } catch( CannotCompileException e ) {
            log.error( "can'compile " + className, e );
        } catch( NotFoundException e ) {
            log.error( "classfile not found " + className, e );
        } catch( IOException e ) {
            log.error( "io " + className, e );
        } catch( RuntimeException e ) {
            log.error( "something " + className, e );
        }

        if( true )
            return classfileBuffer;

        className = jclreHelper.standardizeClassName( className );

        if( className.startsWith( "java" ) || className.startsWith( "jclre.instrumentation" ) || className.startsWith( "sun" ) ) {
            return classfileBuffer;
        }
        log.info( "transforming " + className );

        CtClass oldClass = null;
        if( originalBytecodeCache.contains( className ) ) {
            oldClass = originalBytecodeCache.getCtClass( className );
        }

        originalBytecodeCache.put( className, classfileBuffer );

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( classfileBuffer );
        try {
            log.info( "going to transform " + className );
            CtClass newClass = classPool.makeClass( byteArrayInputStream );
            newClass.defrost();

            CtField field = new CtField( classPool.get( "java.util.Map" ), "___ADDED_FIELDS___", newClass );
            field.setModifiers( Modifier.PUBLIC );
            newClass.addField(
                field,
                "jclre.put.FieldsCache.getInstance().create( \"" + className + "\" )"
            );

            if( oldClass != null ) {
                for( CtField f: newClass.getDeclaredFields() ) {
                    log.info( "new field " + f.getName() );
                }
                for( CtField f: oldClass.getDeclaredFields() ) {
                    log.info( "new field " + f.getName() );
                }

                for( CtField f : newClass.getDeclaredFields() ) {
                    if( f.getName().equals( "___ADDED_FIELDS___" ) ) {
                        continue;
                    }

                    try {
                        oldClass.getDeclaredField( f.getName() );
                    } catch( NotFoundException e ) {
                        log.info( "added field " + f.getType().getName() + " " + f.getName() );
                        newClass.removeField( f );
                        fieldsCache.addField(
                            className,
                            new FieldDefinition(
                                f.getName(),
                                null,
                                null
                            )
                        );
                    }
                }
            }

            byte[] bytes = newClass.toBytecode();
            newClass.defrost();
            log.info( "contains " + className );
            return bytes;
        } catch( Throwable e ) {
            e.printStackTrace();
        }
        return classfileBuffer;
    }
}
