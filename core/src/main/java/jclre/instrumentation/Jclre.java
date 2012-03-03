package jclre.instrumentation;

import javassist.ClassPool;
import jclre.bytecode.Analyzer;
import jclre.cache.FieldsCache;
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
    private JclreClassRedefiner jclreClassRedefiner;
    private JclreClassTransformer jclreClassTransformer;
    private Reloader reloader;
    private Analyzer analyzer;

    private Jclre( Instrumentation instrumentation ) {
        this.instrumentation = instrumentation;
        classPool = ClassPool.getDefault();

        analyzer = new Analyzer();
        fieldsCache = new FieldsCache();
        jclreHelper = new JclreHelper( classPool, fieldsCache, analyzer );
        originalBytecodeCache = new OriginalBytecodeCache( classPool );
        jclreClassRedefiner = new JclreClassRedefiner( classPool, originalBytecodeCache,  instrumentation,  fieldsCache );
        reloader = new Reloader( jclreClassRedefiner );
        jclreClassTransformer = new JclreClassTransformer( classPool, originalBytecodeCache,  jclreHelper,  fieldsCache );

        instrumentation.addTransformer( jclreClassTransformer );


//        new OriginalBytecodeCacheInitializer( classPool, instrumentation, originalBytecodeCache ).init();
    }

    public static Jclre getInstance() {
        if( instance == null ) {
            throw new RuntimeException( "jclre not initialized" );
        }
        return instance;
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

    public Analyzer getAnalyzer() {
        if( analyzer == null ) {
            throw new RuntimeException( "not initialized" );
        }
        return analyzer;
    }

    public static void init( ) {
        instance = new Jclre( Agent.getInstrumentation() );
        log.info( "inited" );
    }
}
