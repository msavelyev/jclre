package org.example;

import jclre.instrumentation.Jclre;
import org.example.test.Hello;
import jclre.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Test {

    private static final Logger log = LoggerFactory.getLogger( Test.class );

    public static void main( String[] args ) {
        log.info( "started" );

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool( 2, new NamedThreadFactory( "agent-worker" ) );
        executorService.scheduleWithFixedDelay(
            new Runnable() {
                @Override
                public void run() {
                    try {
                        Jclre.getInstance().getReloader().reload( Hello.class );
                    } catch( Throwable e ) {
                        e.printStackTrace();
                    }
                }
            },
            10, 10, TimeUnit.SECONDS
        );

        final Hello hello = new Hello();

        executorService.scheduleWithFixedDelay(
            new Runnable() {
                @Override
                public void run() {
                    try {
                        log.info( "tick " + hello.hello( "world" ) );
                    } catch( Throwable e ) {
                        e.printStackTrace();
                    }
                }
            },
            5, 5, TimeUnit.SECONDS
        );
    }
}
