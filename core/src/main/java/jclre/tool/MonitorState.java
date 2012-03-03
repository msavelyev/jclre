package jclre.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitorState {

    private Map<String, Long> fileNameAndChangeDates = new HashMap<String, Long>();
    private List<String> checkedFiles = new ArrayList<String>();

    public void check( File file, MonitorListener listener ) {
        checkedFiles.add( file.getAbsolutePath() );

        if( fileNameAndChangeDates.containsKey( file.getAbsolutePath() ) ) {
            if( !fileNameAndChangeDates.get( file.getAbsolutePath() ).equals( file.lastModified() ) ) {
                listener.fileChanged( file );
                fileNameAndChangeDates.put( file.getAbsolutePath(), file.lastModified() );
            }
        } else {
            listener.fileAdded( file );
            fileNameAndChangeDates.put( file.getAbsolutePath(), file.lastModified() );
        }
    }


    public void init( File dir ) {
        if( dir.isDirectory() ) {
            for( File file: dir.listFiles() ) {
                if( file.isDirectory() ) {
                    init( file );
                } else {
                    fileNameAndChangeDates.put( file.getAbsolutePath(), file.lastModified() );
                }
            }
        }
    }

    public void checkDeleted( MonitorListener listener ) {
        List<String> deleted = new ArrayList<String>();
        for( String file: fileNameAndChangeDates.keySet() ) {
            if( !checkedFiles.contains( file ) ) {
                listener.fileRemoved( new File( file ) );
                deleted.add( file );
            }
        }

        for( String file: deleted ) {
            fileNameAndChangeDates.remove( file );
        }

        checkedFiles = new ArrayList<String>();
    }
}
