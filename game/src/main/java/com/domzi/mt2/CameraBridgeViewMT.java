package com.domzi.mt2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Size;

import java.util.List;
import java.lang.Math.*;

/**
 * Created by M43734 on 02.09.2016.
 */
public abstract class CameraBridgeViewMT extends CameraBridgeViewBase implements SurfaceHolder.Callback {

    public CameraBridgeViewMT(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Size calculateCameraFrameSize(List<?> supportedSizes, ListItemAccessor accessor, int surfaceWidth, int surfaceHeight) {

        int calcWidth = 0;
        int calcHeight = 0;

        for (Object size : supportedSizes) {
            int width = accessor.getWidth(size);
            int height = accessor.getHeight(size);

            if(width == 1280 && height == 720) {
                calcWidth = 1280;
                calcHeight = 720;
                break;
            }

            //calcWidth = Math.max(calcWidth, width);
            //calcHeight = Math.max(calcHeight, width);
        }

        return new Size(calcWidth, calcHeight);
    }
}
