package com.szczepaniak.dawid.photofilter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by dawid on 20.05.2018.
 */

public class Singleton {
    private static final Singleton ourInstance = new Singleton();

    public static Singleton getInstance() {
        return ourInstance;
    }

    int cameraID = 0;
    Activity activity;
    Bitmap photoBitmap;

    private Singleton() {
    }

    public int getCameraID() {
        return cameraID;
    }

    public void setCameraID(int cameraID) {
        this.cameraID = cameraID;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Context context) {
        this.activity = activity;
    }

    public Bitmap getPhotoBitmap() {
        return photoBitmap;
    }

    public void setPhotoBitmap(Bitmap photoBitmap) {
        this.photoBitmap = photoBitmap;
    }
}
