package com.szczepaniak.dawid.photofilter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;


import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by dawid on 15.05.2018.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    public Camera camera;
    public  int cameraID = 1;
    private SurfaceHolder surfaceHolder;
    private Bitmap mBitmap;
    private boolean preview = true;
    Singleton singleton;
    private GestureDetector gestureDetector;
    private byte[] mPreviewFrameBuffer;
    ImageView photo;
    private final Lock lock = new ReentrantLock();
    Camera.PictureCallback mPicture;
    boolean isTakePhoto = false;

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

        mPicture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                Bitmap bitmap = YuvByteArrayToBitmap(data, camera);
                photo.setVisibility(View.VISIBLE);
                photo.setImageBitmap(bitmap);
                isTakePhoto = false;

            }
        };

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


    }

    public boolean onTouchEvent(MotionEvent event){

        int action = MotionEventCompat.getActionMasked(event);

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
          //  changeCameraID();
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


   Camera.PictureCallback mPictureCallback = (new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, android.hardware.Camera camera) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap btm = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            Bitmap scaledBitmap;
            scaledBitmap = Bitmap.createScaledBitmap(btm,photo.getHeight(),photo.getWidth()+10,true);
            Matrix matrix = new Matrix();

            if(cameraID == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT){
                //matrix.preScale(1.0f, -1.0f);
                matrix.setScale(1,-1);
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, false);
                matrix.postRotate(-90);
                //image.setScaleX(-1);
//                camView.setVisibility(View.GONE);
                photo.setImageBitmap(scaledBitmap);
//                image.setVisibility(View.VISIBLE);


            }else {
                matrix.postRotate(90);
            }


            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            photo.setImageBitmap(rotatedBitmap);


            FileOutputStream outStream = null;

        }


    });


//    @Override
//    public void onPreviewFrame(byte[] bytes, Camera camera) {
//
//
//         }

   public void takePhoto(){


       photo.setVisibility(VISIBLE);
       camera.takePicture(null, null,null, mPictureCallback);
       //this.setVisibility(View.GONE);


   }

   public void changeCameraID(MainActivity activity){

       if(preview) {
           camera.release();

           if (cameraID == Camera.CameraInfo.CAMERA_FACING_BACK) {
               cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
           } else {
               cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
           }

           singleton.setCameraID(cameraID);
           Intent intent = new Intent();
           intent.setClass(activity, activity.getClass());
           activity.startActivity(intent);
           activity.finish();
       }

   }


    public static Bitmap YuvByteArrayToBitmap(byte[] data, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        YuvImage image = new YuvImage(data, parameters.getPreviewFormat(), size.width, size.height, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 100, out);
        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }



}