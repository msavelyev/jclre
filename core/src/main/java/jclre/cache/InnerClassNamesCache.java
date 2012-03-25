package jclre.cache;

import java.util.HashMap;
import java.util.Map;

public class InnerClassNamesCache {

//    private Map<String, List<String>> cache = new HashMap<String, List<String>>();
    private Map<String, Integer> cache = new HashMap<String, Integer>();

    public String generate( String forClassName ) {
        if( cache.containsKey( forClassName ) ) {
            int counter = cache.get( forClassName );
            cache.put( forClassName, counter + 1 );
            return "Inner" + ( counter + 1 );
        } else {
            cache.put( forClassName, 1 );
            return "Inner1";
        }
    }



}
