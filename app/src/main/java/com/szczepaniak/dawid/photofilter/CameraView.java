package com.szczepaniak.dawid.photofilter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import java.io.IOException;
import java.util.List;

/**
 * Created by dawid on 15.05.2018.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    private Camera camera;
    private int cameraID = 1;
    private SurfaceHolder surfaceHolder;
    private Bitmap mBitmap;
    Camera.PictureCallback mPicture;
    private boolean preview = true;
    Singleton singleton;
    private GestureDetector gestureDetector;
    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
        singleton =  Singleton.getInstance();
        cameraID = singleton.cameraID;
        gestureDetector = new GestureDetector(singleton.getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                changeCameraID();
                return super.onDoubleTap(e);
            }
        });

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {


        if (camera != null) {
            try {
                android.hardware.Camera.Parameters parameters = camera.getParameters();
                if (Integer.parseInt(Build.VERSION.SDK) >= 8)
                    camera.setDisplayOrientation(90);
                else {
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        parameters.set("orientation", "portrait");
                        parameters.set("rotation", 90);
                    }
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        parameters.set("orientation", "landscape");
                        parameters.set("rotation", 90);
                    }
                }
                List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();


                parameters.setPreviewSize(this.getHeight(), this.getWidth());

                camera.setParameters(parameters);
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
                Intent myIntent = new Intent(getContext(), MainActivity.class);
            }
        }

//       mPicture = new () {
//            @Override
//            public void onPictureTaken(byte[] bytes) {
//
//                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                mBitmap = bitmap;
//            }
//        };

    }

    public boolean onTouchEvent(MotionEvent event){

        int action = MotionEventCompat.getActionMasked(event);
        gestureDetector.onTouchEvent(event);

        float x = event.getX();
        double delta = 0;
        switch(action) {
            case (MotionEvent.ACTION_DOWN) :

                break;
            case (MotionEvent.ACTION_MOVE) :
                break;
            case (MotionEvent.ACTION_UP) :
                float newX = event.getX();
                double d = (double) (newX - x);
                delta = Math.sqrt(Math.pow(d,2));

                break;
            case (MotionEvent.ACTION_CANCEL) :
                break;
            case (MotionEvent.ACTION_OUTSIDE) :
                break;
        }

        if(delta >= 1){
            changeCameraID();
        }
        return true;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            camera = android.hardware.Camera.open(cameraID);
            android.hardware.Camera.Parameters parameters = camera.getParameters();

            if (Integer.parseInt(Build.VERSION.SDK) >= 8)
                camera.setDisplayOrientation(90);
            else {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    parameters.set("orientation", "portrait");
                    parameters.set("rotation", 90);
                }
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    parameters.set("orientation", "landscape");
                    parameters.set("rotation", 90);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        try {
            camera.stopPreview();
            camera.release();
            camera = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   public void takePhoto(){

        if(preview == true) {
            camera.stopPreview();
            preview = false;
        }else {
            camera.startPreview();
            preview = true;
        }
   }

   public void changeCameraID(){

       camera.release();

       if(cameraID == Camera.CameraInfo.CAMERA_FACING_BACK){
           cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
       }
       else {
           cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
       }
       try {
           camera = android.hardware.Camera.open(cameraID);
           android.hardware.Camera.Parameters parameters = camera.getParameters();

           if (Integer.parseInt(Build.VERSION.SDK) >= 8)
               camera.setDisplayOrientation(90);
           else {
               if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                   parameters.set("orientation", "portrait");
                   parameters.set("rotation", 90);
               }
               if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                   parameters.set("orientation", "landscape");
                   parameters.set("rotation", 90);
               }
           }
           camera.setParameters(parameters);
       } catch (Exception e) {
           e.printStackTrace();
       }
//       try {
//          // camera.setPreviewDisplay(surfaceHolder);
//       } catch (IOException e) {
//           e.printStackTrace();
//       }
       camera.startPreview();
   }
}