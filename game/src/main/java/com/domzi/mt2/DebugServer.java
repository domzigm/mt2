/*
 *
 * (c) domzigm 2016 - GPLv3
 * https://github.com/domzigm/mt2
 *
 */
package com.domzi.mt2;

import org.json.*;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

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
