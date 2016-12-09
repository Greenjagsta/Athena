import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class BruteForce {
    public static final char[] CHARSET_LOWERALPHA = {'a','e','o','r','i','s','n','t','l','m','d','c','p','h','b','u','k','g','y','f','w','j','v','z','x','q'};
    public static final char[] CHARSET_UPPERALPHA = {'A','E','O','R','I','S','N','T','L','M','D','C','P','H','B','U','K','G','Y','F','W','J','V','Z','X','Q'};
    public static final char[] CHARSET_MIXEDALPHA = {'a','e','o','r','i','s','n','t','l','m','d','c','p','h','b','u','k','g','y','f','w','j','v','z','x','q','A','E','O','R','I','S','N','T','L','M','D','C','P','H','B','U','K','G','Y','F','W','J','V','Z','X','Q'};
    public static final char[] CHARSET_NUM = {'1','2','0','3','4','5','9','6','8','7'};
    public static final char[] CHARSET_LOWERALPHANUM = {'a','e','o','r','i','s','n','1','t','l','m','d','0','c','p','3','h','b','u','k','4','5','g','9','8','6','7','y','f','w','j','v','z','x','q'};
    public static final char[] CHARSET_UPPERALPHANUM = {'A','E','O','R','I','S','N','1','T','L','M','D','0','C','P','3','H','B','U','K','4','5','G','9','8','6','7','Y','F','W','J','V','Z','X','Q'};
    public static final char[] CHARSET_MIXEDALPHANUM = {'A','E','O','R','I','S','N','1','T','L','M','D','0','C','P','3','H','B','U','K','4','5','G','9','8','6','7','Y','F','W','J','V','Z','X','Q','a','e','o','r','i','s','n','t','l','m','d','c','p','h','b','u','k','g','y','f','w','j','v','z','x','q'};

    //byte l_alpha_start = 96;
    //byte num_start = 48;
    //byte u_alpha_start = 65;

    private boolean firstAttempt;
    private boolean validCharset;
    private boolean found;
    private char[] charset;
    private String currentAttempt;
    private int attemptLength;
    private int maxLength;
    private long generated;

    private String hash_plaintext;

    private final PotFile potfile;
    private final AlgorithmGen algorithmGen;

    public BruteForce(String requestedCharset) throws IOException, NoSuchAlgorithmException {
        this.generated = 0;
        this.firstAttempt = true;
        this.validCharset = false;
        this.found = false;

        potfile = new PotFile();
        algorithmGen = new AlgorithmGen();

        validateCharset(requestedCharset);
    }

    public final String attack(String hashAsHexString, byte[] hashAsByteArray, int maxAttemptLength) throws Exception {
        found = false;
        attemptLength = 0;
        currentAttempt = String.valueOf(charset[0]);

        if (!potfile.attack(hashAsHexString).equals("")) {
            hash_plaintext = potfile.attack(hashAsHexString);
            found = true;

        } else {
            while (!(found) && attemptLength <= maxAttemptLength) {
                if (Arrays.equals(algorithmGen.MD5(nextString()), hashAsByteArray)) {
                    hash_plaintext = getCurrent();
                    potfile.add(hashAsHexString, hash_plaintext);
                    found = true;
                }
            }
        }

        if (found) {
            return hashAsHexString + ":" + hash_plaintext;
        } else {
            return hashAsHexString + ":--PASSWORD LIMIT REACHED--";
        }

    }

    private void validateCharset(String arg) {
        if (arg.equals("a")) {
            this.charset = CHARSET_LOWERALPHA;
            validCharset = true;
	}

        if (!validCharset || arg.equals("A")) {
            this.charset = CHARSET_UPPERALPHA;
	}

        if (!validCharset || arg.equals("aA")) {
            this.charset = CHARSET_MIXEDALPHA;
	}

        if (arg.equals("1")) {
            this.charset = CHARSET_NUM;
            validCharset = true;
	}

        if (!validCharset || arg.equals("a1")) {
            this.charset = CHARSET_LOWERALPHANUM;
	}

        if (!validCharset || arg.equals("A1")) {
            this.charset = CHARSET_UPPERALPHANUM;
	}

        if (!validCharset || arg.equals("aA1")) {
            this.charset = CHARSET_MIXEDALPHANUM;
	}

        currentAttempt = String.valueOf(charset[0]);
	maxLength = charset.length - 1;
    }

    private String nextString() {
        generated++;
	if (!firstAttempt) {
            this.currentAttempt = addOne(currentAttempt);
            return this.currentAttempt;
	} else {
            firstAttempt = false;
            return currentAttempt;
	}
    }

    private String addOne(String s) {
	char[] old = s.toCharArray();
	int cid = getID(old[old.length - 1]);

        if (cid < maxLength) {
            StringBuilder sb = new StringBuilder();
            for (int i=0; i < old.length - 1; i++) {
                sb.append(old[i]);
            }
            sb.append(getById(cid + 1));
            attemptLength = sb.length();
            return sb.toString();

        } else {
            boolean total = true;
            int cs = 1;

            for(; cs < old.length; cs++) {
                cid = getID(old[old.length - (cs + 1)]);
                if (cid < maxLength) {
                    total = false;
                    break;
                }
            }

            if (total) {
                int nl = old.length + 1;
                StringBuilder sb = new StringBuilder();
                for (int i=0; i < nl; i++) {
                    sb.append(getById(0));
                }
                return sb.toString();

            } else {
                StringBuilder sb = new StringBuilder();
                int needed = old.length;
		int caseid = old.length - cs;
                for (int i=0; i < caseid - 1; i++) {
                    sb.append(old[i]);
                    needed--;
		}
                sb.append(getById(getID(old[caseid - 1]) + 1));
                needed--;
		for (int i=0; i < needed; i++) {
                    sb.append(getById(0));
		}
                return sb.toString();
            }
        }
    }

    private char getById(int id) {
        return charset[id];
    }

    private int getID(char c) {
        for (int i=0; i < charset.length; i++) {
            if (charset[i] == c) {
                return i;
            }
	}
        return -1;
    }

    public final int getCharsetCount() {
	return charset.length;
    }

    public final String getCurrent() {
        return this.currentAttempt;
    }
}