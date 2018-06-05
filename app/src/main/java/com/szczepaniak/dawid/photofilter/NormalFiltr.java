package com.szczepaniak.dawid.photofilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by dawid on 27.05.2018.
 */

public class NormalFiltr extends View{

    Bitmap normalBitmap;
    Singleton singleton;


    public NormalFiltr(Context context) {
        super(context);
    }

    public NormalFiltr(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        singleton = Singleton.getInstance();
    }

    public NormalFiltr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NormalFiltr(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(normalBitmap != null){

            canvas.drawBitmap(normalBitmap,0,0,new Paint());
        }
    }

    public void setNormalBitmap(){
        normalBitmap = singleton.StringToBitmap(singleton.getPhotoBitmap());
        invalidate();
    }
}
