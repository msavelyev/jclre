package jclre;

import jclre.instrumentation.Jclre;
import jclre.test.Hello;
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
//                        Jclre.getInstance().getReloader().reload( Hello.Hello1.class );
                    } catch( Throwable e ) {
                        e.printStackTrace();
                    }
                }
            },
            10, 10, TimeUnit.SECONDS
        );

        executorService.scheduleWithFixedDelay(
            new Runnable() {
                @Override
                public void run() {
                    try {
                        log.info( "tick " + new Hello().hello( "world" ) );
                    } catch( Throwable e ) {
                        e.printStackTrace();
                    }
                }
            },
            5, 5, TimeUnit.SECONDS
        );

//        executorService.scheduleWithFixedDelay(
//            new ClassPathMonitor( null ),
//            1, 2, TimeUnit.SECONDS
//        );
    }
}
