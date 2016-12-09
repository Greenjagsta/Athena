import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author Jack
 */
public class RulesHandler {
    public static final char[] CHARSET_UPPER = {'A','E','O','R','I','S','N','T','L','M','D','C','P','H','B','U','K','G','Y','F','W','J','V','Z','X','Q'};
    public static final char[] CHARSET_LOWER = {'a','e','o','r','i','s','n','t','l','m','d','c','p','h','b','u','k','g','y','f','w','j','v','z','x','q'};
    public static final char[] CHARSET_NUM = {'1','2','0','3','4','5','9','6','8','7'};

    private final int rulesAmount;
    private final ArrayList<String> rules;
    private final BufferedReader reader;

    public RulesHandler(String rule_filename) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        if (rule_filename != null) {
            InputStream inputStream = new FileInputStream(rule_filename);
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            rulesAmount = getLineCount(rule_filename);
            rules = new ArrayList<>(rulesAmount);

            String s;
            while ((s = reader.readLine()) != null) {
                rules.add(s);
            }

        } else {
            reader = null;
            rulesAmount = -1;
            rules = null;
        }
    }

    public final StringBuilder parse(StringBuilder word, int index) throws IOException {
        String rule = rules.get(index);
            switch (rule.charAt(0)) {
                case ':':
                    break;

                case '$':
                    word.append(rule.substring(1));
                    break;

                case '^':
                    word.insert(0, rule.substring(1));
                    break;

                case 'u':
                    word.replace(0, word.length(), word.toString().toUpperCase());
                    break;

                case 'l':
                    word.replace(0, word.length(), word.toString().toLowerCase());
                    break;

                case 'r':
                    word.reverse();
                    break;

                case 'c':
                    word.setCharAt(0, Character.toUpperCase(word.charAt(0)));
                    break;

                case 's':
                    word.replace(0, word.length(), word.toString().replaceAll(rule.substring(1, 2), rule.substring(2)));

                default:
                    break;
            }
        return word;
    }

    public final int getRulesAmount() {
        return rulesAmount;
    }

    private int getLineCount(String filename) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        try {
            InputStream lineInputStream = new FileInputStream(filename);
            LineNumberReader lnr = new LineNumberReader(new BufferedReader(new InputStreamReader(lineInputStream, "UTF-8")));
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
}
