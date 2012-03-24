package jclre.cache;

import java.util.HashMap;
import java.util.Map;

public class InnerClassNamesCache {

//    private Map<String, List<String>> cache = new HashMap<String, List<String>>();
    private Map<String, Integer> cache = new HashMap<String, Integer>();

    public String generate( String forClassName ) {
        if( cache.containsKey( forClassName ) ) {
            Integer counter = cache.get( forClassName );
            cache.put( forClassName, counter + 1 );
            return "Inner" + counter;
        } else {
            cache.put( forClassName, 1 );
            return "Inner1";
        }
    }



}
