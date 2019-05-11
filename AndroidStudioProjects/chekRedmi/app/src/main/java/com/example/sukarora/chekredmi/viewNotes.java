package com.example.sukarora.chekredmi;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;

public class viewNotes extends Activity {

    private String STORE_DIRECTORY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_notes);

        File externalFilesDir = getExternalFilesDir(null);

        if(externalFilesDir!=null){

            STORE_DIRECTORY = externalFilesDir.getAbsolutePath() + "/screenshots/";
            File storeDirectory = new File(STORE_DIRECTORY);

            if (storeDirectory.exists()) {

                File nImage[]= storeDirectory.listFiles();
                Log.d("number of img:", Integer.toString(nImage.length));
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.imgLayout1);
                for (int i=0;i<nImage.length; i++){


                    //LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    //imageView.setLayoutParams(vp);

                    LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    parms.gravity = Gravity.CENTER;
                    parms.setMargins(20, 20, 20, 20);

                    ImageView imageView=new ImageView(this);
                    imageView.setLayoutParams(parms);
                    imageView.setAdjustViewBounds(true);
                    linearLayout.addView(imageView);

                    String getImg= STORE_DIRECTORY +nImage[i].getName();
                    Bitmap bmp= BitmapFactory.decodeFile(getImg);
                    imageView.setImageBitmap(bmp);

                }

            }
            else {
                Log.e("Error", "No images to view");

            }

        }




    }
}
