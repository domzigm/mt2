package com.domzi.mt2;

import android.app.Activity;
import android.content.res.Resources;
import android.view.SurfaceView;
import android.content.DialogInterface;
import android.view.View;

import com.domzi.mt2.BoardEvent.BoardEvent;
import com.domzi.mt2.BoardEvent.BoardField;
import com.domzi.mt2.DebugServer;

import org.opencv.core.Mat;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.json.*;

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

public class GameEngine implements Runnable {

    public final static int MAXPLAYERS = 4;

    private PlayerEntity players[] = new PlayerEntity[MAXPLAYERS];
    //private BoardField places[] = new BoardField[20];
    private Board m_Board;

    private NativeInterface m_imageEngine;

    private int m_boardDetectionSlope;
    private boolean m_boardDetected;
    private final int m_boardDetectionSlopeMax = 5;

    private Thread m_gameThread;
    private DebugServer m_debugServer;

    private Condition m_newImageReceived;
    private ReentrantLock m_lock;

    private boolean run;

    // The game engine has/does/knows
    /*

    The location of the players
    Which BoardEntities are connected
    Which events can happen and where are the events placed on the board b

    Random number generator to distribute giving events (cards, strength)
    Random number generator to distribute events (capture, dragon, bandit)

     */

    public static final int getMaxplayers()
    {
        return MAXPLAYERS;
    }

    public GameEngine(Resources res)
    {
        m_boardDetectionSlope = 0;
        m_boardDetected = false;

        m_Board = new Board(res);

        for(int i=0; i<players.length; i++) {
            players[i] = new PlayerEntity(i);    // Todo: Pass value by constructor...
        }

        m_imageEngine = new NativeInterface();
        m_imageEngine.init();

        m_lock = new ReentrantLock();
        m_newImageReceived = m_lock.newCondition();
        run = true;
        m_gameThread = new Thread(this);
        m_gameThread.start();

        m_debugServer = new DebugServer();

    }

    public void onUpdateImage(long grayAddr, long rgbaAddr)
    {
        // We can skip updating the images if the image processing hasn't finished in the meantime
        if(m_lock.tryLock()) {

            m_imageEngine.updateImage(grayAddr, rgbaAddr);
            m_newImageReceived.signal();
            m_lock.unlock();
        }
    }

    public Mat getPreviewImage()
    {
        return new Mat(m_imageEngine.getPreviewImage());
    }

    public boolean isBoardDetected()
    {
        return m_boardDetected;
    }

    @Override
    public void run() {

        m_debugServer.start();

        while(run) {

            try {
                // Wait for image updated
                m_newImageReceived.await();

                // Lock the image during processing
                try {
                    m_lock.lock();
                    if (m_imageEngine.detectBoard() == NativeInterface.RETURN_OK) {
                        m_boardDetectionSlope++;
                    } else {
                        m_boardDetectionSlope = 0;
                    }
                } catch (Exception e) {
                } finally {
                    m_lock.unlock();
                }

                if (m_boardDetectionSlope > m_boardDetectionSlopeMax) {
                    m_boardDetectionSlope = m_boardDetectionSlopeMax;
                    m_boardDetected = true;
                } else {
                    m_boardDetected = false;
                }
            } catch (Exception e) {
            }
        }

        m_debugServer.stop();
    }

    void startEvent(int playerId, int fieldId, boolean skip)
    {
        if(skip) {
            // Todo: With a certain probability it's possible to skip an event and reroll
        }

        BoardEvent event = m_Board.getEventForPlayer(playerId, fieldId);
        event.eval(players[playerId]);
    }


}
