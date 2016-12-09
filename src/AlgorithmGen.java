import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
* This class provides algorithms
* for hashing strings.
*
* @file 	AlgorithmGen.java
* @author	Jack Green
* @date 	13 Dec 2015
**/
public class AlgorithmGen {
    private final MessageDigest md;
    private static StringBuilder sb;

    public AlgorithmGen() throws NoSuchAlgorithmException {
        md = MessageDigest.getInstance("MD5");
        sb = new StringBuilder();
    }

    /**
    * hashes a word using the MD5 algorithm
    *
    * @throws Exception
    * @param word	the word to be hashed
    * @return 		the hashed word
    **/
    public byte[] MD5(String word) throws Exception {
        return md.digest(word.getBytes("UTF-8"));
    }

    public String getAlgorithm(String hashFile_filename) throws FileNotFoundException, IOException {
        /*hashFile_filetext = new File(hashFile_filename);
        hashFile_scanner = new Scanner(hashFile_filetext);

        try (LineNumberReader lnr = new LineNumberReader(new FileReader(hashFile_filetext))) {
            lnr.skip(Long.MAX_VALUE);
            hashFile_length = (lnr.getLineNumber() + 1);
        }

        for (int i = 0; i == hashFile_length; i++) {
            hash = hashFile_scanner.nextLine();

            if (hash.length() == 32) {
                md5Count++;
            } else {
                System.out.println(hash.length());
                unknownCount++;
            }
        }
        if (md5Count > 0) {
            return "Raw MD5";
        } else {
            return "Unknown Type";
        }
        */
        return "MD5";
    }

    public static String byteArraytoHexString(byte[] b) {
        sb.setLength(0);
        for (int i = 0; i < b.length; i++) {
            sb.append(Integer.toHexString((b[i] & 0xFF) | 0x100).substring(1,3));
	}
        return sb.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}