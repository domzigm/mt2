package com.domzi.mt2.BoardEvent;

/**
 * Created by M43734 on 22.07.2016.
 */
public class BoardField {

    // Every BoardField describes a field on the board itself
    private String entityName;

    // It has relative coords of the rect
    private float startCoordX;
    private float startCoordY;
    private float rectWidth;
    private float rectHeight;

    // And absolute height/width of the board
    private int boardWidth;
    private int boardHeight;

    // Guaranteed event
    // Some fields will trigger this specific event

    // Random events
    // One of these events can trigger

    public void initCoords(float eX, float eY, float eWidth, float eHeight, int bWidth, int bHeight)
    {
        startCoordX = eX;
        startCoordY = eY;
        rectWidth = eWidth;
        rectHeight = eHeight;
        boardWidth = bWidth;
        boardHeight = bHeight;
    }

}
