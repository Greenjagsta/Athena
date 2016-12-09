import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

/**
 *
 * @author Jack
 */
public class ArgumentsHandler {
    @Option(name = "-i", usage = "Input file to use")
    private String hashFile_filename = "input.lst";
    @Option(name = "-d", handler = StringArrayOptionHandler.class, usage = "Dictionary file to use")
    private String[] wordlist_filename;
    @Option(name = "-m", usage = "Attack mode to use")
    private String mode;
    @Option(name = "-t", usage = "Hash type in input")
    private String hashtype;
    @Option(name = "-r", usage = "Rule file to use")
    private String rule_filename;
    @Option(name = "-c", usage = "Character set to use")
    private String charset = "a";
    @Option(name = "-l", usage = "Maximum length of string to generate")
    private int maxLength = 6;
    @Option(name = "-o", usage = "File to output to")
    private String output_filename = "output.txt";
    @Option(name = "-s", usage = "Session name")
    private String session_name = "Athena";
    @Option(name = "-f", usage = "Fast start")
    private boolean fastStart;

    private String modeName;
    private String charsetName;
    private int charsetLength;
    private int hashFile_length;
    private int wordlist_length;
    private int wordlist_length2;
    private int hashfile_bytes;
    private int wordlist_bytes;
    private int wordlist_bytes2;
    private String line;
    private int uniques;

    private final AlgorithmGen algorithmGen;

    public ArgumentsHandler() throws NoSuchAlgorithmException, FileNotFoundException, IOException {
        algorithmGen = new AlgorithmGen();
        System.out.println("Starting Athena...\n");
    }

    public void parse() throws IOException {
        hashFile_length = getLineCount(hashFile_filename);
        hashfile_bytes = getBytes(hashFile_filename);
        uniques = getUniques(hashFile_filename);

        switch (mode) {
            case "1":
                modeName = "Dict";
                if (!fastStart) {
                    wordlist_length = getLineCount(wordlist_filename[0]);
                }   wordlist_bytes = getBytes(wordlist_filename[0]);
                break;
            case "2":
                modeName = "Brute";
                switch (charset) {
                    case "a":
                        charsetLength = 26;
                        charsetName = "alphabetic";
                        break;
                    case "A":
                        charsetLength = 26;
                        charsetName = "upper alphabetic";
                        break;
                    case "aA":
                        charsetLength = 52;
                        charsetName = "mixed alphabetic";
                        break;
                    case "1":
                        charsetLength = 10;
                        charsetName = "numeric";
                        break;
                    case "a1":
                        charsetLength = 36;
                        charsetName = "alphanumeric";
                        break;
                    case "A1":
                        charsetLength = 36;
                        charsetName = "upper alphanumeric";
                        break;
                    case "aA1":
                        charsetLength = 62;
                        charsetName = "mixed alphanumeric";
                        break;
                    default:
                        break;
                }   break;
            case "3":
                modeName = "Hybrid";
                if (!fastStart) {
                    wordlist_length = getLineCount(wordlist_filename[0]);
                }   wordlist_bytes = getBytes(wordlist_filename[0]);
                break;
            case "4":
                modeName = "Combinator";
                if (!fastStart) {
                    wordlist_length = getLineCount(wordlist_filename[0]);
                    if (wordlist_filename[0].equals(wordlist_filename[1])) {
                        wordlist_length2 = wordlist_length;
                    } else {
                        wordlist_length2 = getLineCount(wordlist_filename[1]);
                    }
                }   wordlist_bytes = getBytes(wordlist_filename[0]);
                wordlist_bytes2 = getBytes(wordlist_filename[1]);
                break;
            default:
                break;
        }

        if (hashtype == null) {
            hashtype = algorithmGen.getAlgorithm(hashFile_filename);
        }

        System.out.println("Session..: " + session_name + "\nInput....: " + hashFile_filename + " (" + hashfile_bytes + " bytes)\nHashes...: "
                            + hashFile_length + " total, " + uniques + " unique" + " (" + hashtype.toUpperCase() + ")");

        if (rule_filename != null) {
            System.out.println("Rules....: " + rule_filename);
        }

        if ((mode.equals("1") && fastStart) || (mode.equals("3") && fastStart)) {
            System.out.println("Mode.....: " + modeName + " (" + wordlist_filename[0] + ", ~" + (((wordlist_bytes/10) + 500) / 1000 * 1000) + " words, " + wordlist_bytes + " bytes)\nETA......: " + calculateETA() + "\n");

        } else if (mode.equals("1")) {
            System.out.println("Mode.....: " + modeName + " (" + wordlist_filename[0] + ", " + wordlist_length + " words, " + wordlist_bytes + " bytes)\nETA......: " + calculateETA() + "\n");

        } else if (mode.equals("3")) {
            System.out.println("Mode.....: " + modeName + " (" + wordlist_filename[0] + ", " + wordlist_length + " words, " + wordlist_bytes + " bytes)\nETA......: " + calculateETA() + "\n");

        } else if (mode.equals("2")) {
            System.out.println("Mode.....: " + modeName + " (" + charsetName + " charset, " + Math.pow(charsetLength, maxLength) + " combinations)\nETA......: " + calculateETA() + "\n");

        } else if (mode.equals("4")) {
            System.out.println("Mode.....: " + modeName + " (" + wordlist_filename[0] + ", " + wordlist_bytes + " bytes, " + wordlist_filename[1] + ", " + wordlist_bytes2 + " bytes)\nETA......: " + calculateETA() + "\n");

        }
        //System.out.println("Status...: Running\n");
    }

    private String calculateETA() {
        switch (mode) {
            case "1":
                return timeConversion((wordlist_length / 3000000) * hashFile_length);
            case "2":
                return timeConversion((int) (Math.pow(charsetLength, maxLength) / 3000000) * hashFile_length);
            case "3":
                return timeConversion((wordlist_length * ((int) Math.pow(10, maxLength)) / 3000000) * hashFile_length);
            case "4":
                return timeConversion((((wordlist_bytes / 10) * (wordlist_bytes2 / 10)) / 3000000) * hashFile_length);
            default:
                return "";
        }
    }

    private String timeConversion(int seconds) {
        int minutes = seconds / 60;
        seconds -= minutes * 60;
        int hours = minutes / 60;
        minutes -= hours * 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private int getUniques(String filename) throws FileNotFoundException, IOException {
        try {
            InputStream inputStream = new FileInputStream(filename);

            ArrayList<String> uniqueLines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            while (reader.readLine() != null) {
                line = reader.readLine();

                if (!uniqueLines.contains(line)) {
                    uniqueLines.add(line);
                }
            }
            return uniqueLines.size();

        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
            return -1;
        } catch (IOException e) {
            System.out.println(e.toString());
            return -1;
        }
    }

    private int getLineCount(String filename) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        try {
            InputStream inputStream = new FileInputStream(filename);
            LineNumberReader lnr = new LineNumberReader(new BufferedReader(new InputStreamReader(inputStream, "UTF-8")));
            lnr.skip(Long.MAX_VALUE);
            return lnr.getLineNumber() + 1;

        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
            return -1;
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.toString());
            return -1;
        } catch (IOException e) {
            System.out.println(e.toString());
            return -1;
        }
    }

    private int getBytes(String filename) throws FileNotFoundException, IOException {
        try {
            InputStream inputStream = new FileInputStream(filename);
            return inputStream.available();

        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
            return -1;
        } catch (IOException e) {
            System.out.println(e.toString());
            return -1;
        }
    }

    public final String getHashfile() {
        return hashFile_filename;
    }

    public final String getMode() {
        return mode;
    }

    public final String getCharset() {
        return charset;
    }

    public final int getMaxLength() {
        return maxLength;
    }

    public final String getWordlist() {
        return wordlist_filename[0];
    }

    public final String getWordlist2() {
        return wordlist_filename[1];
    }

    public final int getHashFileLength() {
        return hashFile_length;
    }

    public final int getWordlistLength() {
        return wordlist_length;
    }

    public final int getWordlistLength2() {
        return wordlist_length2;
    }

    public final String getRuleFilename() {
        return rule_filename;
    }

    public final String getOutputFile() {
        return output_filename;
    }

    public final void usage() {
	System.out.println("usage");
    }
}