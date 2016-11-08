/*
 *
 * (c) domzigm 2016 - GPLv3
 * https://github.com/domzigm/mt2
 *
 */
package com.domzi.mt2.BoardEvent;

import com.domzi.mt2.GameEngine;
import com.domzi.mt2.PlayerEntity;

public class BoardEvent {

    // Abstract Parent class to all events that can happen on this field

    // The player has already rolled the dice in this event and got knowledge
    // for the next turn
    private int[] playerProgress;

    public BoardEvent()
    {
        final int PLAYERS = GameEngine.getMaxplayers();
        playerProgress = new int[PLAYERS];

    }

    public void eval(PlayerEntity pEntity)
    {
        pEntity.parseStringSimple("strength+10");
        pEntity.getAttr("strength");
        pEntity.setAttr("strength", 5);
        pEntity.getAttr("strength");
    }
}
