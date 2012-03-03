package jclre.tool;

import java.io.File;

public interface MonitorListener {

    void fileChanged( File file );
    void fileRemoved( File file );
    void fileAdded( File file );

}
