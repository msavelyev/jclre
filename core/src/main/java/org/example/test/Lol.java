package org.example.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Lol {

    private static final Logger log = LoggerFactory.getLogger( Lol.class );

    public Lol() {
        log.info( "constructor" );
        new Exception().printStackTrace();
    }

    public void a( int a ) {
        log.info( "" + a );
    }

    public static void main( String[] args ) {
        try {
            Method a = Lol.class.getMethod( "a", int.class );
            a.invoke( new Lol(), Integer.valueOf( 1 ) );
        } catch( NoSuchMethodException e ) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch( InvocationTargetException e ) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch( IllegalAccessException e ) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
