/*
 *
 * (c) domzigm 2016 - GPLv3
 * https://github.com/domzigm/mt2
 *
 */
package com.domzi.mt2;

import android.content.res.Resources;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import com.domzi.mt2.BoardEvent.*;
import static com.domzi.mt2.Json.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.File;
import java.util.LinkedList;

public class Board {

    static final String TAG = "Board";
    LinkedList<BoardEvent> usedEvents;    // Events that only occur once and have been used
    LinkedList<BoardEvent> availEvents;   // Available events that haven't been used or can be reused
    //ArrayList<BoardEvents> setEvents;   // In-use events are managed by the boardfields

    ArrayList<ArrayList<BoardEvent>> playerUniqueEvents;

    Resources m_res;
    
    private BoardField[] fields;

    void parseBoardFields() throws JSONException {
        String jsonData = getJsonData(R.raw.board, "/MT2/board.json");
        JSONObject jData = new JSONObject(jsonData);
        JSONArray jBoardData = jData.getJSONArray("BoardRegions");

        fields = new BoardField[jBoardData.length()];

        for(int i=0; i<jBoardData.length(); i++) {
            JSONObject row = jBoardData.getJSONObject(i);
            JSONObject coords = row.getJSONObject("coords");

            fields[i] = new BoardField(
                    row.getString("name"),
                    row.getString("eventmask"),
                    row.getString("fixedevent"),
                    coords.getDouble("x"),
                    coords.getDouble("y"),
                    coords.getDouble("width"),
                    coords.getDouble("height")
            );
        }
    }

    void parseBoardEvents() throws JSONException
    {
        String jsonData = getJsonData(R.raw.board, "MT2/events.json");
        JSONObject jData = new JSONObject(jsonData);

        JSONArray Events = jData.getJSONArray("Events");
    }

    public Board(Resources res){
        m_res = res;

        try {
            parseBoardFields();
        } catch (JSONException e) {
            Log.e(TAG, "Couldn't parse JSON data");
            e.printStackTrace();
        }
    }

    public BoardEvent getEventForPlayer(int playerId, int fieldId)
    {
        BoardEvent event = fields[fieldId].getNextEvent(playerId);

        if(event == null) {
            // No planed event available for this field,
            // get one from the random event generator

            // Example:
            event = availEvents.getFirst();
            availEvents.removeFirst();
            usedEvents.addLast(event);
        }

        return event;
    }

}
