import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
* This class performs a combinator attack using a
* single given dictionary on a given hash.
*
* @file 	DictAtt.java
* @author	Jack Green
* @date 	13 Dec 2015
**/

public class Combinator {
    private StringBuilder wordlist_plain;
    private String hash_plaintext;
    private int currentLength;
    private boolean found;
    private boolean useRules = false;

    private final String wordlist_filename1;
    private final String wordlist_filename2;
    private FileInputStream inputStream1;
    private FileInputStream inputStream2;

    private final PotFile potfile;
    private final AlgorithmGen algorithmGen;
    private final RulesHandler rulesHandler;

    /**
    * constructor
    *
    * @throws                   java.io.IOException
    * @throws                   java.security.NoSuchAlgorithmException
    * @param wordlist_filename1 the name of the file containing words to try
    * @param wordlist_filename2 the name of the second file containing words to try
    * @param rule_filename      the rules to use
    **/
    public Combinator(String wordlist_filename1, String wordlist_filename2, String rule_filename) throws IOException, NoSuchAlgorithmException {
        if (rule_filename != null) {
            useRules = true;
        }

        wordlist_plain = new StringBuilder();
        this.wordlist_filename1 = wordlist_filename1;
        this.wordlist_filename2 = wordlist_filename2;

        potfile = new PotFile();
        algorithmGen = new AlgorithmGen();
        rulesHandler = new RulesHandler(rule_filename);
    }

    /**
    * performs a dictionary attack on a word
    *
    * @throws                   FileNotFoundException
    * @param hashAsHexString    the hashed word
    * @param hashAsByteArray    the hashed word as a byte array
    * @return 			the original hash and the result of the attack
    **/
    public final String attack(String hashAsHexString, byte[] hashAsByteArray) throws Exception, FileNotFoundException {
        try {
            found = false;
            inputStream1 = new FileInputStream(wordlist_filename1);
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(inputStream1, "UTF-8"));
            inputStream2 = new FileInputStream(wordlist_filename2);
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(inputStream2, "UTF-8"));
            reader2.mark(10000000);
            int rulesAmount = rulesHandler.getRulesAmount();

            if (!potfile.attack(hashAsHexString).equals("")) {
                hash_plaintext = potfile.attack(hashAsHexString);
                found = true;

            } else {
                String s;
                while (!(found) && (s = reader1.readLine()) != null) {
                    wordlist_plain.append(s);
                    currentLength = wordlist_plain.length();
                    while (!(found) && (s = reader2.readLine()) != null) {
                        wordlist_plain.append(s);

                        if (useRules) {
                            for (int i=0; i<rulesAmount && !found; i++) {
                                wordlist_plain = rulesHandler.parse(wordlist_plain, i);

                                if (Arrays.equals(algorithmGen.MD5(wordlist_plain.toString()), hashAsByteArray)) {
                                    hash_plaintext = wordlist_plain.toString();
                                    potfile.add(hashAsHexString, hash_plaintext);
                                    found = true;
                                }
                            }
                        }

                        if (Arrays.equals(algorithmGen.MD5(wordlist_plain.toString()), hashAsByteArray)) {
                            hash_plaintext = wordlist_plain.toString();
                            potfile.add(hashAsHexString, hash_plaintext);
                            found = true;
                        } else {
                            wordlist_plain.setLength(currentLength);
                        }
                    }
                    wordlist_plain.setLength(0);
                    reader2.reset();
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

        } finally {
            inputStream1.close();
            inputStream2.close();
        }
    }
}