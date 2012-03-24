package org.example;

public class ByteCode {

    private int a = 1;
    private int b;

    private ByteCode() {
        b = 2;
    }

    private ByteCode( String lol ) {
        b = 3;
    }

    private void b() {

    }

    private class A {
        private A() {
            b();
            System.out.println( b );
        }
    }

}
