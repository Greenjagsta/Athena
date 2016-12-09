import java.io.*;
import org.kohsuke.args4j.CmdLineParser;

/**
* This class checks the command line arguments
* and passes the data to the relevant class to be
* attacked and outputs the results.
*
* @file 	Athena.java
* @author	Jack Green
* @date 	13 Dec 2015
**/
public class Athena {
    private static final String OUTPUT_FILENAME = "output.txt";

    private static String mode;
    private static String hashFile_filename;
    private static String wordlist_filename;
    private static String wordlist_filename2;
    private static String[] crackedHashes;
    private static String hashAsHexString;
    private static byte[] hashAsByteArray;
    private static int amountCracked;
    private static int maxLength;
    private static String charset;
    private static int hashFile_length;
    private static String output_filename;
    private static String rule_filename;

    private static InputStream inputStream;
    private static OutputStream outputStream;
    private static BufferedReader reader;
    private static BufferedWriter writer;

    private static ArgumentsHandler argsHandler;
    private static Timer timer;

    public static void main(String[] args) throws IOException, Exception {
        argsHandler = new ArgumentsHandler();
        timer = new Timer();

        CmdLineParser clp = new CmdLineParser(argsHandler);
        clp.parseArgument(args);

        try {
            argsHandler.parse();
            mode = argsHandler.getMode();
            hashFile_filename = argsHandler.getHashfile();
            maxLength = argsHandler.getMaxLength();
            charset = argsHandler.getCharset();
            hashFile_length = argsHandler.getHashFileLength();
            rule_filename = argsHandler.getRuleFilename();
            output_filename = argsHandler.getOutputFile();

            if (mode.equals("1") || mode.equals("3")) {
                wordlist_filename = argsHandler.getWordlist();
            } else if (mode.equals("4")) {
                wordlist_filename = argsHandler.getWordlist();
                wordlist_filename2 = argsHandler.getWordlist2();
            }

            crackedHashes = new String[hashFile_length];

            inputStream = new FileInputStream(hashFile_filename);
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            timer.startTimer();

            switch (mode) {
                case "1":
                    DictAtt dictAtt = new DictAtt(wordlist_filename, rule_filename);
                    for (int i = 0; i < hashFile_length; i++) {
                        hashAsHexString = reader.readLine();
                        hashAsByteArray = AlgorithmGen.hexStringToByteArray(hashAsHexString);

                        crackedHashes[i] = dictAtt.attack(hashAsHexString, hashAsByteArray);
                        System.out.println(crackedHashes[i]);
                    }   break;

                case "2":
                    BruteForce bruteForce = new BruteForce(charset);
                    for (int i = 0; i < hashFile_length; i++) {
                        hashAsHexString = reader.readLine();
                        hashAsByteArray = AlgorithmGen.hexStringToByteArray(hashAsHexString);

                        crackedHashes[i] = bruteForce.attack(hashAsHexString, hashAsByteArray, maxLength);
                        System.out.println(crackedHashes[i]);
                    }   break;

                case "3":
                    Hybrid hybrid = new Hybrid(wordlist_filename, maxLength, rule_filename);
                    for (int i = 0; i < hashFile_length; i++) {
                        hashAsHexString = reader.readLine();
                        hashAsByteArray = AlgorithmGen.hexStringToByteArray(hashAsHexString);

                        crackedHashes[i] = hybrid.attack(hashAsHexString, hashAsByteArray);
                        System.out.println(crackedHashes[i]);
                    }   break;

                case "4":
                    Combinator combinator = new Combinator(wordlist_filename, wordlist_filename2, rule_filename);
                    for (int i = 0; i < hashFile_length; i++) {
                        hashAsHexString = reader.readLine();
                        hashAsByteArray = AlgorithmGen.hexStringToByteArray(hashAsHexString);

                        crackedHashes[i] = combinator.attack(hashAsHexString, hashAsByteArray);
                        System.out.println(crackedHashes[i]);
                    }   break;

                default:
                    break;
            }

            timer.stopTimer();
            PotFile.sort();

            if (output_filename != null) {
                outputStream = new FileOutputStream(output_filename);
            } else {
                outputStream = new FileOutputStream(OUTPUT_FILENAME);
            }
            writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write("Athena Results - " + timer.getStartDate());
            writer.newLine();

            amountCracked = hashFile_length;
            for (int i = 0; i < hashFile_length; i++) {
                writer.newLine();
                writer.write(crackedHashes[i]);
                if ((crackedHashes[i].endsWith("--PASSWORD NOT FOUND--")) || (crackedHashes[i].endsWith("--PASSWORD LIMIT REACHED--"))) {
                    amountCracked--;
                }
            }
            writer.flush();
            inputStream.close();
            outputStream.close();

            System.out.println("\nRecovered: " + amountCracked + "/" + hashFile_length + " (" + ((amountCracked * 100.00f) / hashFile_length) + "%)");
            System.out.println("Started..: " + timer.getStartDate() + "\nFinished.: " + timer.getEndDate() + " "
                    + "(" + timer.getElapsedTime() + " seconds)");

	} catch (Exception e) {
            System.out.println(e); //argsHandler.usage();
	} finally {
	}
    }
}