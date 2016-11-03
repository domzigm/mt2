package com.domzi.mt2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.util.Log;
import android.view.WindowManager;
import android.widget.CheckBox;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;

import org.opencv.core.Mat;

public class GameScreen extends AppCompatActivity implements CvCameraViewListener2 {

    private static final String TAG = "GameScreen::Activity";

    private GameEngine gameEngine;
    private CameraBridgeViewBase m_openCvCameraView;
    //private CameraBridgeViewMT m_openCvCameraView;

    public GameScreen() {

    }

    private BaseLoaderCallback m_LoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    m_openCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    static {
        try {
            System.loadLibrary("cmt2");
        }
        catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        //
        gameEngine = new GameEngine(this.getResources());


        setContentView(R.layout.gamescreen);
        getSupportActionBar().hide();
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        //wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MT2");
        //wl.acquire();

        m_openCvCameraView = (CameraBridgeViewBase)findViewById(R.id.surfaceView);
        m_openCvCameraView.setMinimumWidth(1280);
        m_openCvCameraView.setMinimumHeight(720);
        //m_openCvCameraView.calc
        m_openCvCameraView.setMaxFrameSize(1280, 720);
        m_openCvCameraView.setVisibility(SurfaceView.VISIBLE);
        m_openCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onBackPressed() {

        //wl.release();
        //getWindow().setFlags(getWindow().getFlags() & ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        doExit();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (m_openCvCameraView != null)
            m_openCvCameraView.disableView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (m_openCvCameraView != null)
            m_openCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, m_LoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            m_LoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


    private void doExit() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GameScreen.this);
        alertDialog.setPositiveButton("Jawohl!", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.setNegativeButton("Niemals!", null);
        alertDialog.setMessage("Das Gemecker wirklich ignorieren?");
        alertDialog.setTitle("MT2");
        alertDialog.show();
    }

    public void onCameraViewStarted(int width, int height) {
//        mGray = new Mat();
        //      mRgba = new Mat();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        int rows = inputFrame.rgba().rows();
        int cols = inputFrame.rgba().cols();
/*
        Mat image = new Mat();

        try {
            image = Utils.loadResource(this, R.drawable.scaled);
        } catch (IOException e) {
            e.printStackTrace();
        }

        rows = image.rows();
        cols = image.cols();
        //org.opencv.imgproc.Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2RGB);
        Mat newImage = new Mat(image, new Rect(0, 0, 1280, 720));
*/

        gameEngine.onUpdateImage(inputFrame.gray().getNativeObjAddr(), inputFrame.rgba().getNativeObjAddr());

        setButton(gameEngine.isBoardDetected());

//      return gameEngine.getPreviewImage();
        return inputFrame.rgba();
    }

    public void onCameraViewStopped() {

    }

    private void setButton(final boolean checked) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CheckBox cb = (CheckBox)findViewById(R.id.cbBoardDetection);
                cb.setChecked(checked);
            }
        });
    }

}
