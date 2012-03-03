package jclre.bytecode;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Analyzer {

    private static final Logger log = LoggerFactory.getLogger( Analyzer.class );

    public Object analyzeInitialValue( CtClass ctClass, final String fieldName ) {
        try {
            ctClass.instrument(
                new ExprEditor() {
                    @Override
                    public void edit( FieldAccess f ) throws CannotCompileException {
                        String methodName = f.where().getMethodInfo().getName();
                        if( f.getFieldName().equals( fieldName ) ) {
                            if( f.isWriter() ) {
                                f.replace( "___ADDED_FIELDS___.set( \"" + fieldName + "\", $1 );" );
                            } else if( f.isReader() ) {
                                f.replace( "$_ = ( $r ) ___ADDED_FIELDS___.get( \"" + fieldName + "\" );" );
                            }
                        }
                        log.info( "analyzeInitialValue: " + methodName );
                    }
                }
            );
        } catch( CannotCompileException e ) {
            log.info( "can't compile", e );
        }

        return null;
    }

}
