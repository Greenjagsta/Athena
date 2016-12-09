import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
* This class performs a dictionary attack using a
* single given dictionary on a given hash.
*
* @file 	DictAtt.java
* @author	Jack Green
* @date 	13 Dec 2015
**/

public class DictAtt {
    private StringBuilder wordlist_plain;
    private String hash_plaintext;
    private boolean found;
    private boolean useRules = false;

    private final String wordlist_filename;
    private FileInputStream inputStream;

    private final PotFile potfile;
    private final AlgorithmGen algorithmGen;
    private final RulesHandler rulesHandler;

    /**
    * constructor
    *
    * @throws                   java.io.IOException
    * @throws                   java.security.NoSuchAlgorithmException
    * @param wordlist_filename  the name of the file containing words to try
    * @param rule_filename      the rule file to use
    **/
    public DictAtt(String wordlist_filename, String rule_filename) throws IOException, NoSuchAlgorithmException {
        if (rule_filename != null) {
            useRules = true;
        }

        wordlist_plain = new StringBuilder();
        this.wordlist_filename = wordlist_filename;

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
            inputStream = new FileInputStream(wordlist_filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            int rulesAmount = rulesHandler.getRulesAmount();

            if (!potfile.attack(hashAsHexString).equals("")) {
                hash_plaintext = potfile.attack(hashAsHexString);
                found = true;

            } else {
                String s;
                while (!(found) && (s = reader.readLine()) != null) {
                    wordlist_plain.append(s);

                    if (useRules) {
                        for (int i=0; i<rulesAmount && !found; i++) {
                            if  ((wordlist_plain = rulesHandler.parse(wordlist_plain, i)).length() == 0) {
                                wordlist_plain.append(reader.readLine());
                                //i = 0; //implement applyRules as seperate method recursing for rejected words
                            }

                            if (Arrays.equals(algorithmGen.MD5(wordlist_plain.toString()), hashAsByteArray)) {
                                hash_plaintext = wordlist_plain.toString();
                                potfile.add(hashAsHexString, hash_plaintext);
                                found = true;
                            }
                        }

                    } else {
                        if (Arrays.equals(algorithmGen.MD5(wordlist_plain.toString()), hashAsByteArray)) {
                            hash_plaintext = wordlist_plain.toString();
                            potfile.add(hashAsHexString, hash_plaintext);
                            found = true;
                        }
                    }
                    wordlist_plain.setLength(0);
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
            inputStream.close();
        }
    }
}