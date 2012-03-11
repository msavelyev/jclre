package jclre.bytecode;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Modifier {

    private static final Logger log = LoggerFactory.getLogger( Modifier.class );

    public void modifyReadersAndWriters( CtClass ctClass, final List<String> fieldNames ) {
        try {
            ctClass.instrument(
                new ExprEditor() {
                    @Override
                    public void edit( FieldAccess f ) throws CannotCompileException {
                        String fieldName = f.getFieldName();
                        if( fieldNames.contains( fieldName ) ) {
                            String fieldValuesField;
                            if( f.isStatic() ) {
                                fieldValuesField = "___ADDED_STATIC_FIELDS___";
                            } else {
                                fieldValuesField = "___ADDED_FIELDS___";
                            }

                            if( f.isWriter() ) {
                                f.replace( fieldValuesField + ".set( \"" + fieldName + "\", ( $w ) $1 );" );
                            } else if( f.isReader() ) {
                                f.replace( "$_ = ( $r ) " + fieldValuesField + ".get( \"" + fieldName + "\" );" );
                            }
                        }
                    }
                }
            );
        } catch( CannotCompileException e ) {
            log.info( "can't compile", e );
        }
    }

}
