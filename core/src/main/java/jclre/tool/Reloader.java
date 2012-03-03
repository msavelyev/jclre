package jclre.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reloader {

    private static final Logger log = LoggerFactory.getLogger( Reloader.class );

    private JclreClassRedefiner jclreClassRedefiner;

    public Reloader( JclreClassRedefiner jclreClassRedefiner ) {
        this.jclreClassRedefiner = jclreClassRedefiner;
    }

    public void reload( Class clazz ) {
        jclreClassRedefiner.redefine( clazz );
    }

}