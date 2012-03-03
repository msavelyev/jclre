package jclre.cache;

import javassist.ClassPool;
import javassist.CtClass;
import jclre.instrumentation.Jclre;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OriginalBytecodeCache {

    private static final Logger log = LoggerFactory.getLogger( OriginalBytecodeCache.class );

    private ClassPool classPool;
    private Map<String, byte[]> bytecodes = new HashMap<String, byte[]>();

    public OriginalBytecodeCache( ClassPool classPool ) {
        this.classPool = classPool;
    }

    public void put( String className, byte[] bytecode ) {
        bytecodes.put( className, bytecode );
    }

    public boolean contains( String className ) {
        return bytecodes.containsKey( className );
    }

    public byte[] get( String className ) {
        return bytecodes.get( className );
    }

    public CtClass getCtClass( String className ) {
        try {
            return classPool.makeClass( new ByteArrayInputStream( bytecodes.get( className ) ) );
        } catch( IOException e ) {
            throw new RuntimeException( "making byteArrayInputStream from bytecode", e );
        }
    }
}
