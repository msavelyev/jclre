package org.example.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lol {

    private static final Logger log = LoggerFactory.getLogger( Lol.class );

    public Lol() {
        log.info( "constructor" );
        new Exception().printStackTrace();
    }
}
