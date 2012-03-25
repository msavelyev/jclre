package jclre.test.tool;

import java.io.File;
import java.net.URL;

public class BaseTest {

    private CompileTool compileTool = new CompileTool();

    private File getSourceFile( String shortClassName ) {
        URL resource = getClass().getResource( shortClassName + ".j" );
        return new File( resource.getPath() );
    }

    public void compileResource( String shortClassName ) {
        File sourceFile = getSourceFile( shortClassName );
        File newFile = new File( sourceFile.getParentFile().getAbsolutePath() + File.separatorChar + shortClassName + ".java" );
        sourceFile.renameTo( newFile );

        compileTool.compile( newFile );
    }

    public void compileModifiedResource( String shortClassName ) {
        File sourceFile = getSourceFile( shortClassName + "_modified" );

        File newFile = new File( sourceFile.getParentFile().getAbsolutePath() + File.separatorChar + shortClassName + ".java" );
        sourceFile.renameTo( newFile );

        compileTool.compile( newFile );
    }

}
