import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 *
 * @author Jack
 */
public class Hybrid {
    public static final char[] CHARSET_NUM = {'1','2','0','3','4','5','9','6','8','7'};
    public static final int MAX_LENGTH = 4;

    private StringBuilder wordlist_plain;
    private String hash_plaintext;
    private boolean found;
    private boolean firstAttempt;
    private String currentAttempt;
    private int attemptLength;
    private int maxLength;
    private int appendLength;
    private long generated;
    private char[] charset;
    private int lengthIndex;

    private boolean useRules = false;
    private char rule_append;
    private char rule_replace;
    private char rule_replaceWith;

    private final String wordlist_filename;
    private FileInputStream inputStream;

    private final PotFile potfile;
    private final AlgorithmGen algorithmGen;
    private final RulesHandler rulesHandler;

    public Hybrid(String wordlist_filename, int appendLength, String rule_filename) throws IOException, NoSuchAlgorithmException {
        wordlist_plain = new StringBuilder();
        this.wordlist_filename = wordlist_filename;
        this.generated = 0;
        this.firstAttempt = true;
        this.found = false;
        this.charset = CHARSET_NUM;
        this.maxLength = charset.length - 1;
        this.appendLength = appendLength;

        potfile = new PotFile();
        algorithmGen = new AlgorithmGen();
        rulesHandler = new RulesHandler(rule_filename);
    }

    public final String attack(String hashAsHexString, byte[] hashAsByteArray) throws Exception, FileNotFoundException {
        try {
            found = false;
            attemptLength = 0;
            lengthIndex = 0;
            inputStream = new FileInputStream(wordlist_filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            int rulesAmount = rulesHandler.getRulesAmount();

            if (!potfile.attack(hashAsHexString).equals("")) {
                hash_plaintext = potfile.attack(hashAsHexString);
                found = true;

            } else {
                while (!(found) && !wordlist_plain.append(reader.readLine()).toString().equals("null")) {
                    attemptLength = 0;
                    currentAttempt = String.valueOf(charset[0]);
                    lengthIndex = wordlist_plain.length();
                    while (!(found) && attemptLength <= appendLength) {

                        if (useRules) {
                            for (int i=0; i<rulesAmount && !found; i++) {
                                wordlist_plain = rulesHandler.parse(wordlist_plain, i);

                                if (Arrays.equals(algorithmGen.MD5(wordlist_plain.append(nextString()).toString()), hashAsByteArray)) {
                                    hash_plaintext = wordlist_plain.toString();
                                    potfile.add(hashAsHexString, hash_plaintext);
                                    found = true;

                                }
                            }
                        }

                        if (Arrays.equals(algorithmGen.MD5(wordlist_plain.append(nextString()).toString()), hashAsByteArray)) {
                            hash_plaintext = wordlist_plain.toString();
                            potfile.add(hashAsHexString, hash_plaintext);
                            found = true;

                        } else if (getCurrent().length() <= appendLength) {
                            wordlist_plain.setLength(lengthIndex);
                        } else {
                            wordlist_plain.setLength(0);
                        }
                    }
                }
            }

            if (found) {
                return hashAsHexString + ":" + hash_plaintext;
            } else {
                return hashAsHexString + ":--PASSWORD NOT FOUND--";
            }

        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
            return hashAsHexString + ":--PASSWORD NOT FOUND--";
        }
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
