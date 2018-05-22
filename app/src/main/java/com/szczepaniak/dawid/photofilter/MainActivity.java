package com.szczepaniak.dawid.photofilter;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    RelativeLayout layout;
    private final int CAMERA_REQUEST_CODE= 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.layout);
        fullScreenOptions();

        askPermission(android.Manifest.permission.CAMERA, CAMERA_REQUEST_CODE);
        Singleton singleton = Singleton.getInstance();
        singleton.setActivity(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        fullScreenOptions();
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

                    Toast.makeText(this,"Camera Permission is Granted", Toast.LENGTH_SHORT).show();
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

            Toast.makeText(this,"Permission is Granted", Toast.LENGTH_SHORT).show();
        }
    }

}
