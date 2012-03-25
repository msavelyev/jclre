package jclre.test.tool;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;

public class CompileTool {

    public void compile( File file ) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int compilationResult = compiler.run( null, System.out, System.err, file.getAbsolutePath() );
        if( compilationResult != 0 ) {
            throw new RuntimeException( "can't compile" );
        }
    }

}
