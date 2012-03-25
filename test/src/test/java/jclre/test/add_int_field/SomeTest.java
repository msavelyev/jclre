package jclre.test.add_int_field;

import jclre.instrumentation.Jclre;
import jclre.test.tool.BaseTest;
import jclre.test.tool.TestClass;
import junit.framework.Assert;
import org.junit.Test;

public class SomeTest extends BaseTest {

    @Test
    public void addField() throws Exception {
        compileResource( "Hello" );

        TestClass testClass = new TestClass( "jclre.test.add_int_field.Hello" );
        Assert.assertEquals( "Hello", testClass.call( "hello", String.class ) );

        compileModifiedResource( "Hello" );

        Jclre.getInstance().getReloader().reload( testClass.getClazz() );

        Assert.assertEquals( "Hello1", testClass.call( "hello", String.class ) );
        Assert.assertEquals( "Hello2", testClass.call( "hello", String.class ) );

    }



}
