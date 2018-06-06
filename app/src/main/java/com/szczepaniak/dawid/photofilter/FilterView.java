package com.szczepaniak.dawid.photofilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicConvolve3x3;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by dawid on 27.05.2018.
 */

public class FilterView extends AppCompatImageView{

    Bitmap normalBitmap;
    Bitmap iconBitmap;
    Bitmap filterBitmap;
    Singleton singleton;
    String type;
    MainActivity mainActivity;


    public FilterView(Context context) {
        super(context);
    }

    public FilterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        singleton = Singleton.getInstance();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(normalBitmap != null){

                    mainActivity.photo.setImageBitmap(filterBitmap);
                }
            }
        });

        GradientDrawable gd = new GradientDrawable();
        gd.setStroke(5, Color.rgb(239, 235, 233));
        gd.setCornerRadius(15);
        this.setBackground(gd);
    }

    public FilterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBitmaps(Bitmap normalBitmap){
        this.normalBitmap = normalBitmap;
        //this.iconBitmap = iconBitmap;
        filterBitmap = createFiltr(normalBitmap);
        copreBitmap();
        GradientDrawable gd = new GradientDrawable();
        gd.setStroke(3, Color.rgb(239, 235, 233));
        gd.setCornerRadius(20);
        this.setBackground(gd);
        this.setImageBitmap(iconBitmap);

        //invalidate();
    }



    Bitmap createFiltr(Bitmap bitmap){

        switch (type){

            case "Normal":
                bitmap = normalBitmap;
                break;
            case "Negativ":
                bitmap = setNegativ(normalBitmap);
                break;
            case "Monochrome":
                bitmap = setMonochrome(normalBitmap);
                break;
            case "Sepia":
               bitmap = setBinary(normalBitmap);
//                bitmap = setFuzzyGlass(normalBitmap,new float[]{
//                        0,  20,  0,
//                        20, -59, 20,
//                        1,  13,  0 });

//                bitmap = setEdgeDetection(normalBitmap,new float[]{
//                        -1, -1, -1,
//                        -1 , 8, -1,
//                        -1, -1, -1 } );

//                   bitmap = setSharpen(normalBitmap,new float[]{
//                           0, -1, 0,
//                           -1 , 5, -1,
//                           0, -1, 0  } );

                break;
            case "Binary":
               // bitmap = setBinary(normalBitmap);
                break;
        }

        return bitmap;
    }


    public void copreBitmap(){

        if (filterBitmap.getWidth() >= filterBitmap.getHeight()){

            iconBitmap = Bitmap.createBitmap(filterBitmap,filterBitmap.getWidth()/2 - filterBitmap.getHeight()/2, 0, filterBitmap.getHeight(), filterBitmap.getHeight()
            );

        }else{

            iconBitmap = Bitmap.createBitmap(filterBitmap, 0, filterBitmap.getHeight()/2 - filterBitmap.getWidth()/2, filterBitmap.getWidth(), filterBitmap.getWidth()
            );
        }
    }

    public Bitmap setNegativ(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap inversion = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(inversion);
        ColorMatrix colorMatrix = new ColorMatrix(new float[] {
                -1,  0,  0,  0, 255,
                0, -1,  0,  0, 255,
                0,  0, -1,  0, 255,
                0,  0,  0,  1,   0
        });
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return inversion;
    }

    public static Bitmap setMonochrome(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap monochrome = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(monochrome);
        ColorMatrix ma = new ColorMatrix();
        ma.setSaturation(0);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(ma));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return monochrome;
    }

    public static Bitmap setSepia(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap monochrome = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(monochrome);
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        ColorMatrix colorScale = new ColorMatrix();
        colorScale.setScale(1, 1, 0.8f, 1);
        colorMatrix.postConcat(colorScale);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return monochrome;
    }

    public static Bitmap setBinary(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap monochrome = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(monochrome);
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        float m = 255f;
        float t = -255*128f;
        ColorMatrix threshold = new ColorMatrix(new float[] {
                m, 0, 0, 1, t,
                0, m, 0, 1, t,
                0, 0, m, 1, t,
                0, 0, 0, 1, 0
        });
        colorMatrix.postConcat(threshold);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return monochrome;
    }

    private Bitmap setFuzzyGlass(Bitmap original, float[] coefficients) {
        Bitmap bitmap = Bitmap.createBitmap(
                original.getWidth(), original.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript rs = RenderScript.create(mainActivity);

        Allocation allocIn = Allocation.createFromBitmap(rs, original);
        Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);

        ScriptIntrinsicConvolve3x3 convolution
                = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
        convolution.setInput(allocIn);
        convolution.setCoefficients(coefficients);
        convolution.forEach(allocOut);

        allocOut.copyTo(bitmap);         // {  0,  20,  0,
        rs.destroy();                    //   20, -59, 20,
        return bitmap;                   //    1,  13,  0  } / 7
    }

    private Bitmap setEdgeDetection(Bitmap original, float[] coefficients) {
        Bitmap bitmap = Bitmap.createBitmap(
                original.getWidth(), original.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript rs = RenderScript.create(mainActivity);

        Allocation allocIn = Allocation.createFromBitmap(rs, original);
        Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);

        ScriptIntrinsicConvolve3x3 convolution
                = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
        convolution.setInput(allocIn);
        convolution.setCoefficients(coefficients);
        convolution.forEach(allocOut);

        allocOut.copyTo(bitmap);         // { -1, -1, -1,
        rs.destroy();                    //   -1 , 8, -1,
        return bitmap;                   //   -1, -1, -1  }
    }

    private Bitmap setSharpen(Bitmap original, float[] coefficients) {
        Bitmap bitmap = Bitmap.createBitmap(
                original.getWidth(), original.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript rs = RenderScript.create(mainActivity);

        Allocation allocIn = Allocation.createFromBitmap(rs, original);
        Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);

        ScriptIntrinsicConvolve3x3 convolution
                = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
        convolution.setInput(allocIn);
        convolution.setCoefficients(coefficients);
        convolution.forEach(allocOut);

        allocOut.copyTo(bitmap);         // {  0, -1,  0,
        rs.destroy();                    //   -1 , 5, -1,
        return bitmap;                   //    0, -1,  0  }
    }
}
