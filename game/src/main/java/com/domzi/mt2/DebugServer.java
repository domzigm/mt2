package com.domzi.mt2;

import org.json.*;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;


/**
 * Created by M43734 on 02.11.2016.
 */
public class DebugServer extends NanoHTTPD {

    static final int webServerPort = 8081;

    public DebugServer()
    {
        super(webServerPort);
    }

    @Override
    public void start()
    {
        // Override start() to catch port already used exception

        try {
            super.start();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
