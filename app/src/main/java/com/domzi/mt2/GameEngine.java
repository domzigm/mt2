package com.domzi.mt2;

import com.domzi.mt2.BoardEvent.BoardField;

import org.opencv.android.CameraBridgeViewBase;

/**
 * Created by M43734 on 22.07.2016.
 */

/*

Gamescreen is loaded by GUI.
GameEngine is created when Starting/Loading game
Gamescreen is passed to GameEngine for rendering image / applying settings to GameEngine
Settings is object (class) owned by GameEngine
CameraView is receiving images from camera
CameraView is owned by GameEngine, GameEngine will query CameraView for updated images (Timer?)

GameEngine does calibration, tracking of players, managing board fields and events

 */

public class GameEngine {

    private PlayerEntity players[] = new PlayerEntity[4];
    private BoardField places[] = new BoardField[20];

    private CameraBridgeViewBase openCvCameraView;
    private CameraView cameraView;

    // The game engine has/does/knows
    /*

    The location of the players
    Which BoardEntities are connected
    Which events can happen and where are the events placed on the board b

    Random number generator to distribute giving events (cards, strength)
    Random number generator to distribute events (capture, dragon, bandit)

     */

    public void GameEngine()
    {
        /*
        openCvCameraView = new CameraBridgeViewBase() {
            @Override
            protected boolean connectCamera(int width, int height) {
                return false;
            }

            @Override
            protected void disconnectCamera() {

            }
        }
        */

        cameraView = new CameraView();
        openCvCameraView.setCvCameraViewListener(cameraView);

        for(int i=0; i<players.length; i++) {
            players[i] = new PlayerEntity();    // Todo: Pass value by constructor...
            players[i].init(i);
        }
    }
}
