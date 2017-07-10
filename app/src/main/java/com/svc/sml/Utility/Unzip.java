package com.svc.sml.Utility;

/**
 * Created by himanshu on 8/7/16.
 */

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class Unzip {
    private static final String INPUT_GZIP_FILE = "/home/mkyong/file1.gz";
    private static final String OUTPUT_FILE = "/home/mkyong/file1.txt";

//    public static void main(String[] args) {
//        GZipFile gZip = new GZipFile();
//        gZip.gunzipIt();
//    }

    /**
     * GunZip it
     */
    public static String getUnzipPlyFileName(String inputFile) {
        String outputFile = inputFile.replace(".gz",".ply");

        byte[] buffer = new byte[1024];

        try {
            GZIPInputStream gzis =
                    new GZIPInputStream(new FileInputStream(inputFile));

            FileOutputStream out =
                    new FileOutputStream(outputFile);

            int len;
            while ((len = gzis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            gzis.close();
            out.close();
            File f = new File(inputFile);
            f.delete();
            Log.d("Unzip","Done");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
//        File f = new File(inputFile);
//        f.delete();
        return outputFile;
    }
}
