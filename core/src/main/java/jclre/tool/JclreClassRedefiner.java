package jclre.tool;

import javassist.*;
import jclre.cache.FieldsCache;
import jclre.cache.OriginalBytecodeCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class JclreClassRedefiner {

    private static final Logger log = LoggerFactory.getLogger( JclreClassRedefiner.class );

    private ClassPool classPool;
    private Instrumentation instrumentation;

    public JclreClassRedefiner(
        ClassPool classPool,
        Instrumentation instrumentation
    ) {
        this.classPool = classPool;
        this.instrumentation = instrumentation;
    }

    public void redefine( Class clazz ) {
        try {
            log.info( "redefining " + clazz.getName() );
            CtClass newClass = classPool.get( clazz.getName() );
            newClass.detach();

            instrumentation.redefineClasses(
                new ClassDefinition(
                    clazz,
                    newClass.toBytecode()
                )
            );
        } catch( ClassNotFoundException e ) {
            log.error( "class not found " + clazz.getName(), e );
        } catch( CannotCompileException e ) {
            log.error( "can't compile " + clazz.getName(), e );
        } catch( UnmodifiableClassException e ) {
            log.error( "unmodifiable class " + clazz.getName(), e );
        } catch( NotFoundException e ) {
            log.error( "not found " + clazz.getName(), e );
        } catch( IOException e ) {
            log.error( "io " + clazz.getName(), e );
        } catch( RuntimeException e ) {
            log.error( "runtime " + clazz.getName(), e );
        }
    }

}
