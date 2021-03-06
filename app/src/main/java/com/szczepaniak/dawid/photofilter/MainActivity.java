package com.szczepaniak.dawid.photofilter;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {

    RelativeLayout layout;
    private final int CAMERA_REQUEST_CODE= 2;
    CameraView cameraView;
    ImageView photo;
    ImageView camerButton;
    Singleton singleton;
    ImageView back;
    ImageView download;
    ImageView options;
    ImageView gallery;
    int width;
    int height;
    ProgressBar loader;

    Bitmap normalBitmap;
    Bitmap iconBitmap;


    HorizontalScrollView filters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       requestWindowFeature(Window.FEATURE_NO_TITLE);
       getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.layout);
        cameraView = findViewById(R.id.CameraView);
        cameraView.setMainActivity(this);
        loader = findViewById(R.id.progressBar2);
        loader.setVisibility(View.GONE);
        fullScreenOptions();
        photo = findViewById(R.id.Photo);
        back = findViewById(R.id.Back);
        gallery = findViewById(R.id.Gallery);
        options = findViewById(R.id.Menu);


        filters = findViewById(R.id.Filters);
        filters.setVisibility(View.GONE);
        options.setVisibility(View.INVISIBLE);
        back.setVisibility(View.INVISIBLE);

        camerButton = findViewById(R.id.Camera);
        singleton = Singleton.getInstance();
        singleton.setActivity(this);
        inputsSystem();
        askPermission(android.Manifest.permission.CAMERA, CAMERA_REQUEST_CODE);
        cameraView.photo = photo;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        cameraView.height = height;
        cameraView.width = width;

        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(10);
        gd.setColor(Color.argb(70,239, 235, 233));
        filters.setBackground(gd);

        //normalFiltr = findViewById(R.id.NormalFiltr);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
       fullScreenOptions();
    }


    void inputsSystem(){

        final GestureDetector gestureDetector = new GestureDetector(singleton.getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                cameraView.changeCameraID(MainActivity.this);
                return super.onDoubleTap(e);
            }
        });

        cameraView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                gestureDetector.onTouchEvent(motionEvent);
                return false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = getIntent();
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(myIntent);
            }
        });

        camerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cameraView.takePhoto();
                photo.setVisibility(View.VISIBLE);
                options.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
                gallery.setVisibility(View.GONE);
                camerButton.setVisibility(View.GONE);

            }
        });

    }

    void fullScreenOptions(){

        layout.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){

                   // Toast.makeText(this,"Camera Permission is Granted", Toast.LENGTH_SHORT).show();
                    Intent myIntent = getIntent();
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    startActivity(myIntent);
                }else{

                    Toast.makeText(this,"Camera Permission is Denied", Toast.LENGTH_SHORT).show();

                }

        }
    }
    private void askPermission(String permission, int requestCode){

        if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{permission},requestCode);
            Intent myIntent = getIntent();
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            startActivity(myIntent);
        }else{

            //Toast.makeText(this,"Permission is Granted", Toast.LENGTH_SHORT).show();
        }
    }


    void createFiltrs(){
        loader.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(0);
                } catch( InterruptedException e ) {

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inDither = false;
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        Bitmap btm = BitmapFactory.decodeByteArray(singleton.getPhotoData(), 0, singleton.getPhotoData().length, options);
                        Matrix matrix = new Matrix();
                        Bitmap RotateBitmap;
                        if(cameraView.cameraID == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT){

                            matrix.postRotate(-90);
                            matrix.preScale(1,-1);

                        }else {
                            matrix.postRotate(90);

                        }
                        RotateBitmap = Bitmap.createBitmap(btm,0,0,btm.getWidth(),btm.getHeight(),matrix,false);


                        photo.setImageBitmap(RotateBitmap);
                        normalBitmap = RotateBitmap;
                        cameraView.setVisibility(View.INVISIBLE);

                        if(normalBitmap != null) {
                            filters.setVisibility(View.VISIBLE);
                            //iconBitmap = Bitmap.createBitmap(normalBitmap, 400, 400, normalBitmap.getWidth()/2 - 800, normalBitmap.getHeight()/2 - 800, null, true);
                            findFilters();
                        }
                    }
                });
            }
        }).start();

       // loader.setVisibility(View.GONE);


    }

    void findFilters(){

       final ArrayList<FilterView>filters =  new ArrayList<>();


        FilterView normalFilter = findViewById(R.id.normalFiltr);
        normalFilter.setType("Normal");
        normalFilter.mainActivity = this;
        filters.add(normalFilter);

        //normalFilter.setBitmaps(normalBitmap,iconBitmap);

        FilterView negativFilter = findViewById(R.id.negativFiltr);
        negativFilter.setType("Negativ");
        negativFilter.mainActivity = this;
        filters.add(negativFilter);

        //negativFilter.setBitmaps(normalBitmap,iconBitmap);

        FilterView monochromeFilter = findViewById(R.id.monochromeFiltr);
        monochromeFilter.setType("Monochrome");
        monochromeFilter.mainActivity = this;
        filters.add(monochromeFilter);

        //monochromeFilter.setBitmaps(normalBitmap,iconBitmap);

//        FilterView f4 = findViewById(R.id.warmFiltr);
//        f4.setType("4");
//        f4.mainActivity = this;
//        f4.setBitmaps(normalBitmap,iconBitmap);

        FilterView sepiaFilter = findViewById(R.id.sepiaFiltr);
        sepiaFilter.setType("Sepia");
        sepiaFilter.mainActivity = this;
        filters.add(sepiaFilter);

        //sepiaFilter.setBitmaps(normalBitmap,iconBitmap);

        FilterView binaryFilter = findViewById(R.id.binaryFiltr);
        binaryFilter.setType("Binary");
        binaryFilter.mainActivity = this;
        filters.add(binaryFilter);

       // binaryFilter.setBitmaps(normalBitmap,iconBitmap);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(0);
                } catch( InterruptedException e ) {

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i = 0; i < filters.size(); i++){

                            FilterView filterView = filters.get(i);
                            filterView.setBitmaps(normalBitmap);
                        }
                    }
                });
            }
        }).start();


    }




}
