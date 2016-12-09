import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

/**
* This class checks the potfile for hashes
* that have already been discovered.
*
* @file		PotFile.java
* @author 	Jack Green
* @date		13 Dec 2015
**/
public class PotFile {
    private static final String POTFILE_FILENAME = "athena.pot";
    private File potFile_filetext;
    private PrintWriter potFile_writer;

    private final ArrayList<String> HASHES;
    private final ArrayList<String> PLAINS;
    private final FileInputStream inputStream;

    public PotFile() throws FileNotFoundException, IOException {
        HASHES = new ArrayList<>();
	PLAINS = new ArrayList<>();
        inputStream = new FileInputStream(POTFILE_FILENAME);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

        String line;
        while ((line = reader.readLine()) != null) {
            String[] line_split = line.split(":");
            HASHES.add(line_split[0]);
            PLAINS.add(line_split[1]);
        }
    }

    /**
    * searches the potfile to see if the hash
    * has already been discovered
    *
    * @param hash   the hashed word
    * @return       the plaintext of the hash if found and
    *               an empty string if not
    **/
    public final String attack(String hash) {
        if (HASHES.contains(hash)) {
            return PLAINS.get(HASHES.indexOf(hash));
	} else {
            return "";
	}
    }

    /**
    * appends a new given hash and plaintext to the potfile
    *
    * @throws Exception
    * @param plain  the plaintext of the hash
    * @param hash	the hashed word
    **/
    public final void add(String plain, String hash) throws Exception {
        try {
            potFile_filetext = new File(POTFILE_FILENAME);

            if (!(potFile_filetext.exists())) {
                potFile_filetext.createNewFile();
            }

            potFile_writer = new PrintWriter(new BufferedWriter(new FileWriter(potFile_filetext, true)));
            potFile_writer.write(plain + ":" + hash + "\n");

        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            potFile_writer.close();
        }
    }

    public final static void sort() throws FileNotFoundException, IOException {
        try {
            ArrayList<String> rows = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(POTFILE_FILENAME));

            String s;
            while ((s = reader.readLine()) != null) {
                rows.add(s);
            }

            Collections.sort(rows);

            FileWriter writer = new FileWriter(POTFILE_FILENAME);
            for (String cur: rows) {
                writer.write(cur+"\n");
            }

            reader.close();
            writer.close();

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}

