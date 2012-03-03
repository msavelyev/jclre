package jclre;

import javassist.*;

public class Test1 {
    public static void main( String[] args ) throws Throwable {
        ClassPool classPool = ClassPool.getDefault();
        Loader loader = new Loader( classPool );
        loader.addTranslator( classPool, new Translator() {
            @Override
            public void start( ClassPool pool ) throws NotFoundException, CannotCompileException {
            }

            @Override
            public void onLoad( ClassPool pool, String classname ) throws NotFoundException, CannotCompileException {
                pool.get( classname );
            }
        } );
        loader.run( "jclre.Test", new String[] { } );
    }
}
