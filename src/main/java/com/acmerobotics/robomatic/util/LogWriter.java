package com.acmerobotics.robomatic.util;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;

public class LogWriter {

    private static final String TAG = "LogWriter";

    private FileWriter writer;

    public LogWriter (String name, String... headers) {
        try {
            File path = new File("/sdcard/ACME/" + name + "/");
            path.mkdir();
        } catch (Exception e) {
            Log.e(TAG, "errorCreatingPath");
            Log.e(TAG, e.getLocalizedMessage());
        }

        try {
            writer = new FileWriter("/sdcard/ACME/" + name + "/" + System.currentTimeMillis() / 1000 + ".csv");
        } catch (Exception e) {
            Log.e(TAG, "error creating file");
            Log.e(TAG, e.getLocalizedMessage());
        }

        writeLine((Object[]) headers);
    }

    public void writeLine (Object... tokens) {
        try {
            writer.write('\n');
            for (int i = 0; i < tokens.length; i++) {
                if (i != 0) writer.write(", ");
                writer.write(tokens[i].toString());
            }
        } catch (Exception e) {
            Log.e(TAG, "error writing");
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    public void close () {
        try {
            writer.flush();
            writer.close();
        } catch (Exception e) {
            Log.e(TAG, "error closing writer");
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

}
