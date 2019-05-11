package com.example.sukarora.chekredmi;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

public class StartFloatingWindow extends AppCompatActivity {

    public final static int Overlay_REQUEST_CODE = 251;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StartFloatingWindow.this.moveTaskToBack(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkDrawOverlayPermission();

    }


    public void checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(StartFloatingWindow.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, Overlay_REQUEST_CODE);
            } else {
                openFloatingWindow();
            }
        } else {
            openFloatingWindow();
        }
    }

    private void openFloatingWindow() {
        Intent intent = new Intent(StartFloatingWindow.this, FloatingWidgetService.class);
        this.stopService(intent);
        this.startService(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Overlay_REQUEST_CODE: {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Settings.canDrawOverlays(StartFloatingWindow.this)) {
                        openFloatingWindow();
                    }
                } else {
                    openFloatingWindow();
                }
                break;
            }
        }
    }


}
