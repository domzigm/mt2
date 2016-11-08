/*
 *
 * (c) domzigm 2016 - GPLv3
 * https://github.com/domzigm/mt2
 *
 */
package com.domzi.mt2.BoardEvent;

import android.util.Log;

import com.domzi.mt2.GameEngine;

import java.util.ArrayList;
import java.util.LinkedList;

public class BoardField {

    // Every BoardField describes a field on the board itself
    private String fieldName;

    // Random events
    // One of these events can trigger
    private String eventMask;

    // Guaranteed event
    // Some fields will trigger this specific event
    private String fixedEvent;

    // Relative coords of the rect
    private float coords_x;
    private float coords_y;
    private float coords_height;
    private float coords_width;

    // Todo: Queue of next events, e.g. save current event on stack
    private LinkedList<BoardEvent> nextEvent;

    // Todo: Player is on quest, what will be the next event
    private BoardEvent playerSpecificEvent[];

    // Todo: Player specific progress per field
    private int playerSpecficProgress[];

    public BoardField(String Name, String EventMask, String FixedEvent, double ex, double ey, double Width, double Height)
    {
        final int PLAYERS = GameEngine.getMaxplayers();
        playerSpecificEvent = new BoardEvent[PLAYERS];
        playerSpecficProgress = new int[PLAYERS];

        for(int i=0; i<PLAYERS; i++) {
            playerSpecificEvent[i] = null;
        }

        fieldName = Name;
        eventMask = EventMask;
        fixedEvent = FixedEvent;

        coords_x = (float)ex;
        coords_y = (float)ey;
        coords_width  = (float)Width;
        coords_height = (float)Height;
   }

    /**
     * Check if there is any player specific event set for this field
     * @return true/false
     */
    public boolean isPlayerSpecificEventSet()
    {
        for(int i=0; i<GameEngine.getMaxplayers(); i++) {
            if(playerSpecificEvent[i] != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add a player specific event to this field. Will overwrite event if already set.
     * @param playerId Id of the player (0 .. MaxPlayers)
     * @param event The BoardEvent to be set
     */
    public void setPlayerSpecificEvent(int playerId, BoardEvent event)
    {
        playerSpecificEvent[playerId] = event;
    }

    /**
     * Set the first event of the queue. Will prepend to previously first event.
     * @param event The BoardEvent to be prepended
     */
    public void setNextEvent(BoardEvent event)
    {
        nextEvent.addFirst(event);
    }

    /**
     * Append an event to the queue.
     * @param event The BoardEvent to be appended
     */
    public void setTrailingEvent(BoardEvent event)
    {
        nextEvent.addLast(event);
    }

    /**
     * Return the next event for this field. Player specific events have higher priority than the queue.
     * The event will be removed from the queue after the call.
     * @param playerId The PlayerId
     * @return The next event
     */
    public BoardEvent getNextEvent(int playerId)
    {
        BoardEvent event = null;

        // Is there a player specific event set?
        if(playerSpecificEvent[playerId] != null) {
            event = playerSpecificEvent[playerId];
            playerSpecificEvent[playerId] = null;
        }
        else {
            // There is no specific event, check the linked list
            if (nextEvent.size() > 1) {
                nextEvent.getFirst();
                nextEvent.removeFirst();
            } else {
                // No event in list
                Log.d("BoardField", "::getNextEvent no event in list!");
            }
        }
        return event;
    }
}
