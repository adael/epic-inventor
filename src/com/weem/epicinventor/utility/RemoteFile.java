package com.weem.epicinventor.utility;

import java.net.*; 
import java.io.*; 

public class RemoteFile {

    private RemoteFile() {
    }
    
    public static String getFileContents(String u) {
        String contents = "";
        BufferedReader br = null;

        try {
            URL url =  new URL(u);

            InputStream is = url.openStream();
            br = new BufferedReader(new InputStreamReader(is));
        } catch (MalformedURLException e) {
            return "";
        } catch (IOException e) {
            return "";
        }

        try {
            String s;
            boolean eof = false;
            s = br.readLine();

            while (!eof) {
                contents += s;
                try {
                    s = br.readLine();
                    if (s == null) {
                        eof = true;
                        br.close();
                    }
                } catch (EOFException eo) {
                    eof = true;
                } catch (IOException e) {
                    return "";
                }
            }
        } catch (IOException e) {
            return "";
        }
        
        return contents.trim();
    }
}
