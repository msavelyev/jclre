package jclre.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class ClassPathMonitor implements Runnable {

    private static final Logger log = LoggerFactory.getLogger( ClassPathMonitor.class );

    private Reloader reloader;

    private List<File> jars = new ArrayList<File>();
    private List<File> dirs = new ArrayList<File>();

    private MonitorState previousState = new MonitorState();

    public ClassPathMonitor( Reloader reloader ) {
        this.reloader = reloader;
    }

    public void readClasspath() {
        String javaClassPath = System.getProperty( "java.class.path" );

        for( String classPathEntry: javaClassPath.split( System.getProperty( "path.separator" ) ) ) {
            File file = new File( classPathEntry );
            if( file.isDirectory() ) {
                dirs.add( file );
                previousState.init( file );
            } else {
                jars.add( file );
            }
        }
    }

    private void checkDirectory( File dir, MonitorListener listener, MonitorState previousState ) {
        checkDirectory( dir, listener, previousState, true );
    }

    private void checkDirectory( File dir, MonitorListener listener, MonitorState previousState, boolean root ) {
        if( dir.isDirectory() ) {
            for( File file: dir.listFiles() ) {
                if( file.isDirectory() ) {
                    checkDirectory( file, listener, previousState, false );
                } else {
                    previousState.check( file, listener );
                }
            }
        }

        if( root ) {
//            previousState.checkDeleted( listener );
        }
    }

    public Map<File, Date> readDir( File dir, Map<File, Date> result ) {
        if( result == null ) {
            result = new HashMap<File, Date>();
        }

        if( dir.isDirectory() ) {
            File[] files = dir.listFiles();
            for( File file: files ) {
                if( file.isDirectory() ) {
                    readDir( file, result );
                } else {
                    result.put( file, new Date( file.lastModified() ) );
                }
            }

            return result;
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public void run() {
        for( File dir: dirs ) {
            checkDirectory(
                dir,
                new MonitorListener() {
                    @Override
                    public void fileChanged( File file ) {
                        log.info( "changed " + file.getAbsolutePath() );
                    }

                    @Override
                    public void fileRemoved( File file ) {
                        log.info( "removed " + file.getAbsolutePath() );
                    }

                    @Override
                    public void fileAdded( File file ) {
                        log.info( "added " + file.getAbsolutePath() );
                    }
                },
                previousState
            );
        }
    }
}
