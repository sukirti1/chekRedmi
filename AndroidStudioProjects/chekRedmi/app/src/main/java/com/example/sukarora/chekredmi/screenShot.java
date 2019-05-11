package com.example.sukarora.chekredmi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;


public class screenShot extends Activity {

    private MediaProjectionManager projectionManager;
    private MediaProjection mProjection;
    private static final int PERMISSION_CODE= 1234;
    private ImageReader mImageReader;
    private int mWidth;
    private int mHeight;
    private Display mDisplay;
    private VirtualDisplay mVirtualDisplay;
    private static final String SCREENCAP_NAME = "screencap";
    private int mDensity;
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private Handler mHandler;
    private static String STORE_DIRECTORY;
    private static int IMAGES_PRODUCED;
    private static final String TAG = screenShot.class.getName();
    private int count=0, countOncreate=0;
    private int count_ss=0;
    private Bitmap bitmap= null, nbitmap= null;
    private int startx, width, starty, height;
    private DisplayMetrics metrics;



    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader reader) {
            count_ss++;
            Log.d("onImageAvailable:", Integer.toString(count_ss));
            Image image = null;
            FileOutputStream fos = null;
            Bitmap bitmap = null;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());



            try {
                image = reader.acquireLatestImage();
                if (image != null && count_ss==1) {
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = (ByteBuffer) planes[0].getBuffer().rewind();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * mWidth;
                    Rect rect = image.getCropRect();

                    // create bitmap

                    //bitmap = Bitmap.createBitmap(metrics.widthPixels + (int) ((float) rowPadding / (float) pixelStride), metrics.heightPixels, Bitmap.Config.ARGB_8888);
                    bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                    //nbitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height());
                    //bitmap = Bitmap.createBitmap(mWidth , mHeight, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);
                    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, startx, starty, width-startx, height-starty);


                    // write bitmap to a file
                    fos = new FileOutputStream(STORE_DIRECTORY + "/myscreen_" + timeStamp + ".png");
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                    IMAGES_PRODUCED++;
                    Log.e(TAG, "captured image: " + IMAGES_PRODUCED);

                    stopProjection();




                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }

                if (bitmap != null) {
                    bitmap.recycle();
                }

                if (image != null) {
                    image.close();
                }

            }
        }
    }

    private void stopProjection(){

        if (mProjection!=null) {
            mProjection.stop();
            mVirtualDisplay.release();
            mProjection=null;
            this.finish();

            Intent intent= new Intent(screenShot.this, MainActivity.class);
            startActivity(intent);

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //moveTaskToBack(true);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        countOncreate++;

        Window window=getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        Intent intent= getIntent();
        startx = (int)intent.getFloatExtra("startx", 0);
        width = (int)intent.getFloatExtra("width", 0);
        starty = (int)intent.getFloatExtra("starty", 0);
        height = (int)intent.getFloatExtra("height", 0);

        Log.d("startx_ss", Float.toString(startx));
        Log.d("width_ss", Float.toString(width));
        Log.d("starty_ss", Float.toString(starty));
        Log.d("height_ss", Float.toString(height));


        //mImageReader = ImageReader.newInstance(mWidth, mHeight, ImageFormat.RGB_565, 2);

        projectionManager=(MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
        Log.d("onCreate", "onCreate");
        startActivityForResult(projectionManager.createScreenCaptureIntent(), PERMISSION_CODE);

        Log.d("screenShot", Integer.toString(countOncreate));





    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        mDisplay = getWindowManager().getDefaultDisplay();

        if (requestCode == 1234) {
            if (resultCode == Activity.RESULT_OK) {

                mProjection = projectionManager.getMediaProjection(resultCode, data);
                if (mProjection != null) {

                    File externalFilesDir = getExternalFilesDir(null);
                    if (externalFilesDir != null) {

                        STORE_DIRECTORY = externalFilesDir.getAbsolutePath() + "/screenshots/";
                        File storeDirectory = new File(STORE_DIRECTORY);
                        if (!storeDirectory.exists()) {
                            boolean success = storeDirectory.mkdirs();
                            if (!success) {
                                Log.e(TAG, "failed to create file storage directory.");
                                return;
                            }
                        }
                    } else {
                        Log.e(TAG, "failed to create file storage directory, getExternalFilesDir is null.");


                        return;
                    }



                    createVirtualDisplay();



                }

            }
        }

    }



    private void createVirtualDisplay() {
        // get width and height
        Point size = new Point();
        mDisplay.getRealSize(size);
        metrics = getResources().getDisplayMetrics();
        mDensity = metrics.densityDpi;
        mWidth = size.x;
        mHeight = size.y;
        count++;

        Log.d("createVirtualDisplay", Integer.toString(count));

        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
        mVirtualDisplay = mProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight, mDensity, flags, mImageReader.getSurface(), null, mHandler);


        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);





        //mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
        //imageReader.setOnImageAvailableListener(this, svc.getHandler());
        //mImageReader.setOnImageAvailableListener(this, mHandler);
    }



}
