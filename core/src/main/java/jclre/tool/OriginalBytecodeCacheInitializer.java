package jclre.tool;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import jclre.cache.OriginalBytecodeCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Arrays;
import java.util.List;

public class OriginalBytecodeCacheInitializer {

    private static final Logger log = LoggerFactory.getLogger( OriginalBytecodeCacheInitializer.class );

    private ClassPool classPool;
    private Instrumentation instrumentation;
    private OriginalBytecodeCache originalBytecodeCache;

    public OriginalBytecodeCacheInitializer(
        ClassPool classPool,
        Instrumentation instrumentation,
        OriginalBytecodeCache originalBytecodeCache
    ) {
        this.classPool = classPool;
        this.instrumentation = instrumentation;
        this.originalBytecodeCache = originalBytecodeCache;
    }

    public void init() {
        Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();
        List<String> systemPrefixes = Arrays.asList( "java.", "sun.", "jclre.instrumentation." );
        for( Class loadedClass: allLoadedClasses ) {
            if( instrumentation.isModifiableClass( loadedClass ) ) {
                String loadedClassName = loadedClass.getName();

                boolean systemClass = false;
                for( String systemPrefix: systemPrefixes ) {
                    if( loadedClassName.startsWith( systemPrefix ) ) {
                        systemClass = true;
                        break;
                    }
                }
                if( !systemClass ) {
                    try {
                        instrumentation.retransformClasses( loadedClass );
//                        CtClass ctClass = classPool.get( loadedClassName );
//                        originalBytecodeCache.put( loadedClassName, ctClass.toBytecode() );
//                    } catch( NotFoundException e ) {
//                        log.error( "class " + loadedClassName + " not found", e );
//                    } catch( CannotCompileException e ) {
//                        log.error( "can't compile class " + loadedClassName, e );
//                    } catch( IOException e ) {
//                        log.error( "io", e );
                    } catch( UnmodifiableClassException e ) {
                        log.error( "unmodifiable class " + loadedClassName, e );
                    }
                }
            }
        }
    }

}
