/*
 *
 * (c) domzigm 2016 - GPLv3
 * https://github.com/domzigm/mt2
 *
 */
package com.domzi.mt2;

import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PlayerEntity {

    private int entityIndex;
    private String entityName;

    PlayerAttributes m_attributes;
    LinkedList<String> m_changedAttrList;

    private static final String TAG = "PlayerEntity";

    /**
     * Create an entity object for a player
     * @param index Id of the player
     * @param playerName Name of the player
     */
    public PlayerEntity(int index, String playerName)
    {
        entityIndex = index;
        entityName = playerName;

        // Todo: Attributes should be parsed from json file!
        final int maximumLevel = 24;
        final int minimumLevel = 0;
        final int maximumStrength = 12;
        final int minimumStrength = 0;
        m_attributes.addAttribute("strength", "str.ogg", 0, minimumStrength, maximumStrength);
        m_attributes.addAttribute("level", "lvl.ogg", 0, minimumLevel, maximumLevel);
    }

    /**
     * Get the value of an attribute
     * @param AttribString Name of the attribute
     * @return Value of the attribute
     */
    public int getAttr(String AttribString) {
        int returnVal = -1;
        try {
            returnVal =  m_attributes.getAttribValue(AttribString);
        }
        catch(Exception e) {
            Log.e(TAG, "Tried to get non-existing attribute");
        }
        return returnVal;
    }

    /**
     * Set the value of an attribute
     * @param AttribString Name of the attribute
     * @param value New value of the attribute
     * @return The new verified value of the attribute
     */
    public int setAttr(String AttribString, int value)
    {
        int returnVal = -1;
        try {
            returnVal =  m_attributes.setAttribValue(AttribString, value);
        }
        catch(Exception e) {
            Log.e(TAG, "Tried to set non-existing attribute");
        }
        return returnVal;
    }

    /**
     * Get the audio file of an attribute
     * @param AttribString The attribute
     * @return Filename of the audio file
     */
    public String getAudioFile(String AttribString)
    {
        String returnVal = null;
        try {
            Object userData =  m_attributes.getUserData(AttribString);
            if(userData instanceof String) {
                returnVal = (String)userData;
            }
            else {
                Log.e(TAG, "Userdata type miss-match");
            }
        }
        catch(Exception e) {
            Log.e(TAG, "Tried to get non-existing attribute");
        }
        return returnVal;
    }

    /**
     * Parse a command string and execute operations accordingly
     * Each modified attribute will be stored until the next call
     * @param cmd The command string to be parsed
     */
    public void parseStringSimple(String cmd)
    {
        m_changedAttrList.clear();
        String tokens[] = cmd.toLowerCase().trim().split("\\s*,\\s*");

        for(String token : tokens) {
            int pos = 0;

            if(-1 != (pos = token.indexOf("+"))) {
                // Found positive number
            }
            else if(-1 != (pos = token.indexOf("-"))) {
                // Found negative number
            }

            if(-1 != pos) {
                String attribute = token.substring(0, pos);
                int modifier = Integer.parseInt(token.substring(pos));
                int value = getAttr(attribute);
                if(-1 != value) {
                    value = value + modifier;
                    setAttr(attribute, value);
                    m_changedAttrList.addLast(attribute);
                }
            }
        }
    }
}
