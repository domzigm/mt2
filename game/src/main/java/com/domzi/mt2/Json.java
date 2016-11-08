/*
 *
 * (c) domzigm 2016 - GPLv3
 * https://github.com/domzigm/mt2
 *
 */
package com.domzi.mt2;

import android.content.res.Resources;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Json {

    static Resources m_res;

    /**
     * Register resource with JSON helper class
     * @param res Android application resource
     */
    public static void setResource(Resources res) {
        m_res = res;
    }

    /**
     * Read JSON data from a resource file or a file specified by overrideFile path
     * @param resId The android resource ID
     * @param overrideFile An absolute path which specifies the override File
     * @return On success getJsonData will a JSON string or an empty string on failure
     */
    static String getJsonData(int resId, String overrideFile)
    {
        InputStream jsonStream = null;
        StringBuffer buffer = new StringBuffer();

        try {
            jsonStream = m_res.openRawResource(resId);
        }
        catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

        // If an override file is available
        File boardDescFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + overrideFile);
        if(boardDescFile.exists()) {
            // File should exist but there could be another error preventing a read operation
            try {
                jsonStream = new FileInputStream(boardDescFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if(jsonStream != null) {
            String line = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(jsonStream));
            try {
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return buffer.toString();
    }
}
