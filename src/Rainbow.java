import java.util.ArrayList;

/**
* This class performs a rainbow table lookup
* on a given hash.
*
* @file 	Rainbow.java
* @author	Jack Green
* @date 	13 Dec 2015
**/

public class Rainbow {
    public static final char[] CHARSET_ALPHA = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
    public static final char[] CHARSET_NUM = {'0','1','2','3','4','5','6','7','8','9'};
    public static final char[] CHARSET_ALPHANUM = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','1','2','3','4','5','6','7','8','9','0'};

    private int keyspace;
    private char[] charset;

    private ArrayList<Integer> charRefs;

    public Rainbow(String requestedCharset) throws Exception {
        this.charRefs = new ArrayList<Integer>();

        if (requestedCharset.equalsIgnoreCase("a1")) {
            keyspace = 36;
            charset = CHARSET_ALPHANUM;
        } else if (requestedCharset.equalsIgnoreCase("a")) {
            keyspace = 26;
            charset = CHARSET_ALPHA;
        } else if (requestedCharset.equalsIgnoreCase("1")) {
            keyspace = 10;
            charset = CHARSET_NUM;
        } else {}
    }

    public final String attack(String hash) {return null;}

    public final String reduce(String str, int plaintextLength) {
        String reduced = str.replaceAll("[^\\d]", "").substring(0, plaintextLength * 2);

        charRefs.clear();
        for (int i=0; i<plaintextLength * 2; i+=2) {
            charRefs.add(Integer.parseInt(reduced.substring(i, Math.min(plaintextLength * 2, i + 2))));
        }

        StringBuilder sb = new StringBuilder();
        for (int charRef : charRefs) {
            int charRef_withinKeyspace = charRef % keyspace;

            sb.append(charset[charRef_withinKeyspace]);
        }

        return sb.toString();
    }

    public void generateTable(int chainLength, int chainAmount, String seed) {

    }
}