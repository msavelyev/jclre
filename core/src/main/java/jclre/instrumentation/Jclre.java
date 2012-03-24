package jclre.instrumentation;

import javassist.ClassPool;
import jclre.bytecode.Modifier;
import jclre.cache.FieldsCache;
import jclre.cache.InnerClassNamesCache;
import jclre.cache.MethodsCache;
import jclre.cache.OriginalBytecodeCache;
import jclre.tool.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;

public class Jclre {

    private static final Logger log = LoggerFactory.getLogger( Jclre.class );

    private static Jclre instance;

    private Instrumentation instrumentation;
    private ClassPool classPool;

    private JclreHelper jclreHelper;
    private OriginalBytecodeCache originalBytecodeCache;
    private FieldsCache fieldsCache;
    private MethodsCache methodsCache;
    private JclreClassRedefiner jclreClassRedefiner;
    private JclreClassTransformer jclreClassTransformer;
    private Reloader reloader;
    private Modifier modifier;
    private InnerClassNamesCache innerClassNamesCache;

    private Jclre( Instrumentation instrumentation ) {
        this.instrumentation = instrumentation;
        classPool = ClassPool.getDefault();

        modifier = new Modifier();
        fieldsCache = new FieldsCache();
        methodsCache = new MethodsCache();
        innerClassNamesCache = new InnerClassNamesCache();
        jclreHelper = new JclreHelper( classPool, fieldsCache, methodsCache, modifier, innerClassNamesCache );
        originalBytecodeCache = new OriginalBytecodeCache( classPool );
        jclreClassRedefiner = new JclreClassRedefiner( classPool, instrumentation );
        reloader = new Reloader( jclreClassRedefiner );
        jclreClassTransformer = new JclreClassTransformer( classPool, originalBytecodeCache,  jclreHelper,  fieldsCache );

        instrumentation.addTransformer( jclreClassTransformer );

    }

    public static Jclre getInstance() {
        if( instance == null ) {
            throw new RuntimeException( "jclre not initialized" );
        }
        return instance;
    }

    public InnerClassNamesCache getInnerClassNamesCache() {
        if( innerClassNamesCache == null ) {
            throw new RuntimeException( "not initialized" );
        }
        return innerClassNamesCache;
    }

    public Reloader getReloader() {
        if( reloader == null ) {
            throw new RuntimeException( "not initialized" );
        }
        return reloader;
    }

    public OriginalBytecodeCache getOriginalBytecodeCache() {
        if( originalBytecodeCache == null ) {
            throw new RuntimeException( "not initialized" );
        }
        return originalBytecodeCache;
    }

    public JclreClassRedefiner getJclreClassRedefiner() {
        if( jclreClassRedefiner == null ) {
            throw new RuntimeException( "not initialized" );
        }
        return jclreClassRedefiner;
    }

    public JclreHelper getJclreHelper() {
        if( jclreHelper == null ) {
            throw new RuntimeException( "not initialized" );
        }
        return jclreHelper;
    }

    public Instrumentation getInstrumentation() {
        if( instrumentation == null ) {
            throw new RuntimeException( "not initialized" );
        }
        return instrumentation;
    }

    public FieldsCache getFieldsCache() {
        if( fieldsCache == null ) {
            throw new RuntimeException( "not initialized" );
        }
        return fieldsCache;
    }

    public ClassPool getClassPool() {
        if( classPool == null ) {
            throw new RuntimeException( "not initialized" );
        }
        return classPool;
    }

    public JclreClassTransformer getJclreClassTransformer() {
        if( jclreClassTransformer == null ) {
            throw new RuntimeException( "not initialized" );
        }
        return jclreClassTransformer;
    }

    public Modifier getModifier() {
        if( modifier == null ) {
            throw new RuntimeException( "not initialized" );
        }
        return modifier;
    }

    public MethodsCache getMethodsCache() {
        if( methodsCache == null ) {
            throw new RuntimeException( "not initialized" );
        }
        return methodsCache;
    }

    public static void init( ) {
        instance = new Jclre( Agent.getInstrumentation() );
        log.info( "inited" );
    }
}
