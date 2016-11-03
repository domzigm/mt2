/*
 *
 * (c) domzigm 2016 - GPLv3
 * https://github.com/domzigm/mt
 * 
 */
 
package com.domzi.mt2;

import org.opencv.core.Mat;

public class NativeInterface {

    public static final int RETURN_OK = 0;
    public static final int REMOTE_NOT_PRESSED = -1;
    public static final int REMOTE_NOT_DETECTED = -2;
    
    /**
     * Reset all states in the NativeInterfaceLibrary to their initial values
     */
    public native void init();

    /**
     * Update the native image processing engine with the latest image retrieved from the camera
     * @param grayImage The native address of the gray image
     * @param rgbaImage The native address of the RGBA image
     */
    public native void updateImage(long grayImage, long rgbaImage);

    /**
     * Get the latest preview image from the processing engine
     * @return Native address of the preview image
     */
    public native long getPreviewImage();

    /**
     * Do the board detection and update the rectificated image
     * @return   0 Image received and board detected
     *          -1 Could not detect the board corners
     */
    public native int detectBoard();

    /**
     * Register an area based on its relative coordinates
     * @param values org.opencv.core.Rect relative coordinates
     * @return >=0  The index of the registered area
     *          -1  The coordinates are invalid
     *          -2  The area overlaps with an already registered area
     */
    public native int registerArea(float[] values);

    /**
     * Set the ROI for the dice detection in relative coordinates
     * @param values org.opencv.core.Rect relative coordinates
     * @return =0   The ROI has been set
     *         -1   The coordinates are invalid
     */
    public native int setDiceRoi(float[] values);

    /**
     * Register a remote based on its color identifier
     * Note: There is no overlapping check for colors!
     * @param values org.opencv.core.Scalar based HSV color range
     * @return >=0  The index of the registered remote
     *          -1  At least one of the values is out of range
     */
    public native int registerRemote(int[] values);

    /**
     * Retrieve the pushed button of the remote specified by remoteId
     * @param remoteId Id of the remote returned by registerRemote
     * @return >0   The index of the button
     *         =0   No button pressed
     *         <0   Invalid result
     */
    public native int getRemote(int remoteId);

    /**
     * Register a figure based on its color identifier
     * Note: There is no overlapping check for colors!
     * @param values org.opencv.core.Scalar based HSV color range
     * @return >=0  The index of the registered figure
     *          -1  At least one of the values is out of range
     */
    public native int registerFigure(int[] values);

    /**
     * Retrieve the area index where a specific figure is located
     * @param figureId Id of the figure returned by registerFigure
     * @return >=0  The index of the registered area
     *          -1  The figure id is not registered
     *          -2  No area has been registered yet
     *          -3  The figure was located at an non-registered area
     *          -4  The figure could not be located
     */
    public native int getFigure(int figureId);

    /**
     * Get the number of dice rolled
     * Note: Has to be called before getEyes()
     * @return >=1  The number of dice
     *           0  No dice detected
     */
    public native int getDice();

    /**
     * Get the number of eyes from the dice rolled
     * Note: Has to be called after getDice()
     * Note: The application has to verify the sum of eyes vs the dice count (e.g. eyes <= dice * 6)
     * @return >=1  The sum of all eyes
     *           0  No eyes detected
     */
    public native int getEyes();

}