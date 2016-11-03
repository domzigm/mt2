package com.domzi.mt2.BoardEvent;

import com.domzi.mt2.GameEngine;
import com.domzi.mt2.PlayerEntity;

/**
 * Created by M43734 on 22.07.2016.
 */
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
        pEntity.decreaseLevel();
        pEntity.increaseLevel();
        pEntity.decreaseStrength();
        pEntity.increaseStrength();
    }
}
