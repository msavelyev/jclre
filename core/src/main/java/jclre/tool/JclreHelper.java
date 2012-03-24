package jclre.tool;

import javassist.*;
import javassist.Modifier;
import javassist.bytecode.MethodInfo;
import javassist.compiler.Javac;
import jclre.bytecode.*;
import jclre.cache.FieldsCache;
import jclre.cache.InnerClassNamesCache;
import jclre.cache.MethodsCache;
import jclre.cache.field.FieldDefinition;
import jclre.modify.ClassFields;
import jclre.tool.helper.CompareResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JclreHelper {

    private static final Logger log = LoggerFactory.getLogger( JclreHelper.class );

    private ClassPool classPool;
    private FieldsCache fieldsCache;
    private MethodsCache methodsCache;
    private jclre.bytecode.Modifier modifier;
    private InnerClassNamesCache innerClassNamesCache;

    public JclreHelper(
        ClassPool classPool,
        FieldsCache fieldsCache,
        MethodsCache methodsCache,
        jclre.bytecode.Modifier modifier,
        InnerClassNamesCache innerClassNamesCache
    ) {
        this.classPool = classPool;
        this.fieldsCache = fieldsCache;
        this.methodsCache = methodsCache;
        this.modifier = modifier;
        this.innerClassNamesCache = innerClassNamesCache;
    }

    public String standardizeClassName( String classNameWithSlashes ) {
        return classNameWithSlashes.replace( '/', '.' );
    }

    public boolean systemClassName( String className ) {
        List<String> systemPrefixes = Arrays.asList( "java.", "sun.", "jclre.", "org.slf4j.", "ch.qos.", "com.apple.", "com.intellij." );
        for( String systemPrefx: systemPrefixes ) {
            if( className.startsWith( systemPrefx ) ) {
                return true;
            }
        }
        return false;
    }

    public CtClass createCtClass( byte[] bytecode ) throws IOException, NotFoundException {
        return classPool.makeClass( new ByteArrayInputStream( bytecode ) );
    }

    public void addFields( CtClass ctClass ) {
        try {
            CtField field = new CtField( classPool.get( ClassFields.class.getName() ), "___ADDED_FIELDS___", ctClass );
            field.setModifiers( java.lang.reflect.Modifier.PUBLIC );
            ctClass.addField(
                field,
                "jclre.instrumentation.Jclre.getInstance().getFieldsCache().create( \"" + ctClass.getName() + "\" )"
            );

            CtField staticField = new CtField(
                classPool.get( ClassFields.class.getName() ),
                "___ADDED_STATIC_FIELDS___",
                ctClass
            );
            staticField.setModifiers( java.lang.reflect.Modifier.PUBLIC | java.lang.reflect.Modifier.STATIC );
            ctClass.addField(
                staticField,
                "jclre.instrumentation.Jclre.getInstance().getFieldsCache().createStatic( \"" + ctClass.getName() + "\" )"
            );
        } catch( CannotCompileException e ) {
            log.error( "can't compile " + ctClass.getName(), e );
        } catch( NotFoundException e ) {
            log.error( "not found " + ctClass.getName(), e );
        } catch( RuntimeException e ) {
            log.error( "runtime " + ctClass.getName(), e );
        }
    }

    public CompareResult<CtField> compareFields( CtClass newClass, CtClass oldClass ) {
        CompareResult<CtField> compareResult = new CompareResult<CtField>();

        for( CtField newField: newClass.getDeclaredFields() ) {
            try {
                oldClass.getDeclaredField( newField.getName() );
            } catch( NotFoundException e ) {
                log.info( "added field " + newField.getName() );
                compareResult.addedItem( newField );
            }
        }

        for( CtField oldField: oldClass.getDeclaredFields() ) {
            try {
                newClass.getDeclaredField( oldField.getName() );
            } catch( NotFoundException e ) {
                log.info( "removed field " + oldField.getName() );
                compareResult.removedItem( oldField );
            }
        }

        return compareResult;
    }

    public void fixFields( CtClass ctClass, CompareResult<CtField> compareResult ) {
        for( CtField removedField: compareResult.getRemovedItems() ) {
            try {
                if( java.lang.reflect.Modifier.isStatic( removedField.getModifiers() ) ) {
                    fieldsCache.removeStaticField(
                        ctClass.getName(),
                        makeFieldDef( removedField )
                    );
                } else {
                    fieldsCache.removeField(
                        ctClass.getName(),
                        makeFieldDef( removedField )
                    );
                }

                CtField newField = new CtField( removedField, ctClass );
                ctClass.addField( newField );
            } catch( CannotCompileException e ) {
                log.error( "can't compile", e );
            }
        }

        List<String> addedFieldsNames = new ArrayList<String>();
        for( CtField addedField: compareResult.getAddedItems() ) {
            addedFieldsNames.add( addedField.getName() );
            try {
                if( java.lang.reflect.Modifier.isStatic( addedField.getModifiers() ) ) {
                    fieldsCache.addStaticField(
                        ctClass.getName(),
                        makeFieldDef( addedField )
                    );
                } else {
                    fieldsCache.addField(
                        ctClass.getName(),
                        makeFieldDef( addedField )
                    );
                }

                ctClass.removeField( addedField );
            } catch( NotFoundException e ) {
                log.error( "field not found " + addedField.getName(), e );
            }
        }

        modifier.modifyReadersAndWriters( ctClass, addedFieldsNames );
    }

    private FieldDefinition makeFieldDef( CtField ctField ) {
        CtClass ctFieldType = null;
        try {
            ctFieldType = ctField.getType();
        } catch( NotFoundException e ) {
            log.error( "type of field " + ctField.getName() + " not found", e );
            return new FieldDefinition(
                ctField.getName(),
                null, // TODO
                null // TODO
            );
        }

        Object defaultValue = null;
        if( ctFieldType.isPrimitive() ) {
            if( ctFieldType.getName().equals( "boolean" ) ) {
                defaultValue = false;
            } else if( ctFieldType.getName().equals( "float" ) ) {
                defaultValue = ( float ) 0;
            } else if( ctFieldType.getName().equals( "double" ) ) {
                defaultValue = ( double ) 0;
            } else if( ctFieldType.getName().equals( "int" ) ) {
                defaultValue = 0;
            } else if( ctFieldType.getName().equals( "long" ) ) {
                defaultValue = ( long ) 0;
            } else if( ctFieldType.getName().equals( "byte" ) ) {
                defaultValue = ( byte ) 0;
            } else if( ctFieldType.getName().equals( "char" ) ) {
                defaultValue = ( char ) 0;
            } else if( ctFieldType.getName().equals( "short" ) ) {
                defaultValue = ( short ) 0;
            }
        }

        return new FieldDefinition(
            ctField.getName(),
            null, // TODO
            defaultValue // TODO
        );

    }

    public CompareResult<CtMethod> compareMethods( CtClass newClass, CtClass oldClass ) {
        CompareResult<CtMethod> compareResult = new CompareResult<CtMethod>();

        for( CtMethod newMethod: newClass.getDeclaredMethods() ) {
            MethodInfo methodInfo = newMethod.getMethodInfo();
            try {
                oldClass.getMethod( methodInfo.getName(), methodInfo.getDescriptor() );
            } catch( NotFoundException e ) {
                log.info( "added method " + methodInfo.getName() + " - " + methodInfo.getDescriptor() );
                compareResult.addedItem( newMethod );
            }
        }

        for( CtMethod oldMethod: oldClass.getDeclaredMethods() ) {
            MethodInfo methodInfo = oldMethod.getMethodInfo();
            try {
                newClass.getMethod( methodInfo.getName(), methodInfo.getDescriptor() );
            } catch( NotFoundException e ) {
                log.info( "removed method " + methodInfo.getName() + " - " + methodInfo.getDescriptor() );
                compareResult.removedItem( oldMethod );
            }
        }

        return compareResult;
    }

    public void fixMethods( CtClass newClass, CompareResult<CtMethod> methodsCompareResult ) {
        log.info( "fixing methods for class " + newClass.getName() + " : " + methodsCompareResult );
        for( CtMethod removedMethod: methodsCompareResult.getRemovedItems() ) {
            try {
                newClass.addMethod( removedMethod );
            } catch( CannotCompileException e ) {
                log.error( "can't compile method " + removedMethod.getName(), e );
            }
        }

        String generatedName = innerClassNamesCache.generate( newClass.getName() );
        CtClass innerClass = newClass.makeNestedClass( generatedName, true );
        try {
            CtClass superclass = classPool.get( "jclre.modify.MethodsAccessor" );
            innerClass.setSuperclass( superclass );

            innerClass.addConstructor( CtNewConstructor.defaultConstructor( innerClass ) );

            for( CtMethod addedMethod: methodsCompareResult.getAddedItems() ) {
                try {
                    CtMethod ctMethod = new CtMethod( addedMethod, innerClass, null );
                    innerClass.addMethod( ctMethod );
                    newClass.removeMethod( addedMethod );
                } catch( CannotCompileException e ) {
                    log.error( "can't compile " + addedMethod.getName(), e );
                }
            }

            methodsCache.update( newClass.getName(), innerClass.toClass() );

            CtField newField = new CtField( innerClass.getSuperclass(), "___ADDED_METHODS___", newClass );
            newField.setModifiers( Modifier.PUBLIC );
            newClass.addField(
                newField,
                "jclre.instrumentation.Jclre.getInstance().getMethodsCache().create( " + newClass.getName() + "\" )"
            );
        } catch( CannotCompileException e ) {
            log.error( "can't compile", e );
        } catch( NotFoundException e ) {
            log.error( "class not found", e );
        }
    }

    public void dumpFields( CtClass ctClass, Logger logger, String logPrefix ) {
        for( CtField ctField: ctClass.getDeclaredFields() ) {
            logger.info( logPrefix + ": " + ctField );
        }
    }

    public void dumpMethods( CtClass ctClass, Logger logger, String logPrefix ) {
        for( CtMethod ctMethod: ctClass.getDeclaredMethods() ) {
            logger.info( logPrefix + ": " + ctMethod );
        }
    }

    public void addMethods( CtClass newClass ) {
        try {
            CtMethod ctMethod = CtNewMethod.make(
                "public Object get( String fieldName ) {" +
                    "       Field declaredField = getClass().getDeclaredField( fieldName );" +
                    "       declaredField.setAccessible( true );" +
                    "       return declaredField.get( this );" +
                    "   try {" +
                    "   } catch( Exception e ) {" +
                    "       throw new RuntimeException( e );" +
                    "   }" +
                    "}",
                newClass
            );
            newClass.addMethod( ctMethod );
        } catch( CannotCompileException e ) {
            log.error( "can't compile", e );
        }
    }
}
