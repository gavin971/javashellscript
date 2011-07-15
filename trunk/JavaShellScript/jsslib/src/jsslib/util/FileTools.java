package jsslib.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * This class contains tools to work with files
 * 
 * @author robert schuster
 */
public class FileTools {
    
    /**
     * Copy a file and replace keywords with content from a map
     * @param src              source file
     * @param dest             destination file
     * @param keysToReplace    map with key->new content
     * @param force            override existing files 
     */
    public static void copyTextfileAndReplaceKeywords(File src, File dest, Map<String,String> keysToReplace, boolean force) throws IOException {
        if(dest.exists()) {
            if(force) {
                dest.delete();
            } else {
                throw new IOException("Cannot overwrite existing file: " + dest.getName());
            }
        }
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new FileReader(src));
            out = new PrintWriter(dest);
            String line = null;
            if (keysToReplace != null) {
                String[] keys = keysToReplace.keySet().toArray(new String[0]);
                while((line = in.readLine()) != null) {
                    for (String key:keys) {
                        while (line.contains(key)) {
                            line = line.replace(key, keysToReplace.get(key));
                        }
                    }
                    out.println(line);
                }                
            } else {
                while((line = in.readLine()) != null) out.println(line);
            }
        } finally {
            // Sicherstellen, dass die Streams auch
            // bei einem throw geschlossen werden.
            // Falls in null ist, ist out auch null!
            if (in != null) {
                //Falls tatsächlich in.close() und out.close()
                //Exceptions werfen, die jenige von 'out' geworfen wird.
                try {
                    in.close();
                }
                finally {
                    if (out != null) {
                        out.close();
                    }
                }
            }
        }        
    }
}
