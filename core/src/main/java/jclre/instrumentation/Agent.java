package jclre.instrumentation;

import java.lang.instrument.Instrumentation;

public class Agent {

    private static Instrumentation instrumentation;

    public static void premain( String args, Instrumentation inst ) {
        instrumentation = inst;
        Jclre.init();
    }

    protected static Instrumentation getInstrumentation() {
        return instrumentation;
    }

}
