package com.szczepaniak.dawid.photofilter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.Date;

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
    String photoBitmap;
    byte[] photoData;

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

    public String getPhotoBitmap() {
        return photoBitmap;
    }

    public void setPhotoBitmap(String photoBitmap) {
        this.photoBitmap = photoBitmap;
    }


    public String BitmapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitmap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    public byte[] getPhotoData() {
        return photoData;
    }

    public void setPhotoData(byte[] photoData) {
        this.photoData = photoData;
    }
}
