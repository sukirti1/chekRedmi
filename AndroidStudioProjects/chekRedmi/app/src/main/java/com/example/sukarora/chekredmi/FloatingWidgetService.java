package com.example.sukarora.chekredmi;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.andremion.counterfab.CounterFab;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class FloatingWidgetService extends Service {

    private WindowManager mWindowManager;
    private View mOverlayView;
    private MediaProjection mMediaProjection;
    private static final int REQUEST_CODE = 1000;
    CounterFab counterFab;
    private Button save, cancel;
    public final static int PERMISSION_CODE = 231;
    private int mActivePointerId;
    private int screenwidth,screenheight;
    private float wid;
    private float x_longPress,y_longPress, endx, endy, startx, starty;
    private ImageView mrect;
    private int initialX;
    private int initialY;
    private Bitmap bmp2;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();

        setTheme(R.style.AppTheme);

        mOverlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null);
        mrect = (ImageView) mOverlayView.findViewById(R.id.iv);
        save = mOverlayView.findViewById(R.id.save);
        cancel= mOverlayView.findViewById(R.id.cancel);

        save.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);


        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }


        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);


        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;



        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mOverlayView, params);

        //screenwidth = mWindowManager.getDefaultDisplay().getWidth();
        //screenheight = mWindowManager.getDefaultDisplay().getHeight();

        Point point = new Point();
        mWindowManager.getDefaultDisplay().getSize(point);
        //screenwidth = point.x;
        //screenheight = point.y;

        final DisplayMetrics metrics = new DisplayMetrics();
        Display display = mWindowManager.getDefaultDisplay();
        Method mGetRawH = null, mGetRawW = null;

        try {
            // For JellyBean 4.2 (API 17) and onward
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealMetrics(metrics);

                screenwidth = metrics.widthPixels;
                screenheight = metrics.heightPixels;

                Log.d("screenwidth", Integer.toString(screenwidth));
            } else {
                mGetRawH = Display.class.getMethod("getRawHeight");
                mGetRawW = Display.class.getMethod("getRawWidth");

                try {
                    screenwidth = (Integer) mGetRawW.invoke(display);
                    screenheight = (Integer) mGetRawH.invoke(display);
                    Log.d("screenwidth", Integer.toString(screenwidth));
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        }






        counterFab = (CounterFab) mOverlayView.findViewById(R.id.fabHead);
        //counterFab.setCount(1);

        counterFab.setOnTouchListener(new View.OnTouchListener() {
            private float initialTouchX;
            private float initialTouchY;
            private int flag=0;


            GestureDetector gestureDetector = new GestureDetector(FloatingWidgetService.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {


                    //takeScreenshot();



                    mActivePointerId = e.getPointerId(0);
                    int pointerIndex = e.findPointerIndex(mActivePointerId);
                    x_longPress = e.getRawX();
                    y_longPress = e.getRawY();

                    //x_longPress= params.x;
                    //y_longPress= params.y;


                    Log.d("x:", Float.toString(x_longPress-initialX));
                    Log.d("y:", Float.toString(y_longPress-initialY));







                    /*params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_DIM_BEHIND
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_FULLSCREEN;*/

                    //mWindowManager.removeView(mOverlayView);

                    //mWindowManager.addView(mOverlayView, params);

                    params.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    params.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;

                    params.width= WindowManager.LayoutParams.FILL_PARENT;
                    params.height= WindowManager.LayoutParams.FILL_PARENT;


                    mWindowManager.updateViewLayout(mOverlayView, params);

                    counterFab.hide();





                    flag=1;



                }


            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                gestureDetector.onTouchEvent(event);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.

                        initialX = params.x;
                        initialY = params.y;


                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        startx = event.getRawX();
                        starty = event.getRawY();



                        return true;
                    case MotionEvent.ACTION_UP:

                        endx= event.getRawX();
                        endy= event.getRawY();

                        if(flag==1){

                            save.setVisibility(View.VISIBLE);
                            cancel.setVisibility(View.VISIBLE);


                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    //save.setVisibility(View.GONE);
                                    //counterFab.hide();
                                    mOverlayView.setVisibility(View.GONE);


                                    takeScreenshot();

                                }
                            });

                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    mOverlayView.setVisibility(View.GONE);
                                    onCreate();
                                }
                            });
                            flag=0;

                        }


                        // counterFab.show();

                        //Add code for launching application and positioning the widget to nearest edge.




                        return true;
                    case MotionEvent.ACTION_MOVE:


                        float Xdiff = Math.round(event.getRawX() - initialTouchX);
                        float Ydiff = Math.round(event.getRawY() - initialTouchY);

                        Log.d("wtf", Float.toString(event.getRawX()));

                        float x= event.getRawX();
                        float y= event.getRawY();

                        float diffx= x-startx;
                        float diffy= y-starty;


                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) Xdiff;
                        params.y = initialY + (int) Ydiff;



                        if(flag==1){

                            bmp2= createSelection(diffx,diffy);
                            mrect.setImageBitmap(bmp2);

                            //getAxis(event.getRawX(),event.getRawY());

                        }


                        //Update the layout with new X & Y coordinates
                        mWindowManager.updateViewLayout(mOverlayView, params);


                        return true;
                }
                return false;
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopSelf();
        if (mOverlayView != null)
            mWindowManager.removeView(mOverlayView);
    }

    private void takeScreenshot(){





        Intent intent= new Intent(this, screenShot.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //float startx= x_longPress- (x_longPress-initialX);
        //float startx1= startx;
        float width= endx;

        //float starty= y_longPress- (y_longPress-initialY);
        //float height= mrect.getMeasuredHeight()-starty;
        float height= endy;

        intent.putExtra("startx", startx);
        intent.putExtra("width", width);
        intent.putExtra("starty", starty);
        intent.putExtra("height", height);

        Log.d("startx.....", Float.toString(startx));
        //Log.d("width", Float.toString(width));
        Log.d("starty......", Float.toString(starty));
        //Log.d("height", Float.toString(height));


        startActivity(intent);

        //float height_y= mrect.getMeasuredHeight()-(x_longPress- (x_longPress-initialX));
        //float width_x= mrect.getMeasuredWidth()-(y_longPress- (y_longPress-initialY));







    }

    private Bitmap createSelection(float diff_x, float diff_y){

        // Creating a selection box

        Bitmap bitmap = Bitmap.createBitmap(
                (int) (screenwidth),
                (int) (screenheight),
                Bitmap.Config.ARGB_8888
        );

        try{



            // changed here..original were i=sx and j=sy-30
            //for(int i=Math.round(x_longPress- (x_longPress-initialX)); i<x_longPress+ diff_x;i+=2){
            //                for (int j=Math.round(y_longPress- (y_longPress-initialY));j<=y_longPress+ diff_y;j++){

            //for(int i=Math.round(0); i<x_longPress+ diff_x;i+=2){
            for(int i=Math.round(startx); i<startx+diff_x;i+=2){
                for (int j=Math.round(starty);j<=y_longPress+ diff_y;j++){
                    bitmap.setPixel(i,j, Color.CYAN);
                    if(i<300){
                        bitmap.setPixel(i,j,Color.YELLOW);
                    }
                    if(i>500){
                        bitmap.setPixel(i,j,Color.GRAY);
                    }

                }

            }


        }
        catch (Exception e){
            Log.e("Exception","ex",e);
        }

        return bitmap;
        //mrect.setImageBitmap(bitmap);




        //Log.d("width of mrect:", Integer.toString(mrect.getMeasuredWidth()));
        //Log.d("height of mrect:", Integer.toString(mrect.getMeasuredHeight()));




    }

    /*private void createSelection(){

        // Creating a selection box

        Log.d("startx", Float.toString(startx));
        //Log.d("width", Float.toString(width));
        Log.d("starty", Float.toString(starty));
        //Log.d("height", Float.toString(height));

        Bitmap bitmap = Bitmap.createBitmap(
                (int) (screenwidth),
                (int) (screenheight),
                Bitmap.Config.ARGB_8888
        );

        try{



            // changed here..original were i=sx and j=sy-30
            for(int i=0; i<screenwidth;i+=2){
                for (int j=0;j<=screenheight;j++){
                    bitmap.setPixel(i,j, Color.CYAN);
                    if(i<300){
                        bitmap.setPixel(i,j,Color.YELLOW);
                    }
                    if(i>500){
                        bitmap.setPixel(i,j,Color.GRAY);
                    }

                }

            }


        }
        catch (Exception e){
            Log.e("Exception","ex",e);
        }
        mrect.setImageBitmap(bitmap);

        //Log.d("width of mrect:", Integer.toString(mrect.getMeasuredWidth()));
        //Log.d("height of mrect:", Integer.toString(mrect.getMeasuredHeight()));




    }*/


    private void getAxis(float rawx, float rawy){



        //Log.d("raw x:", Float.toString(rawx));
        //Log.d("raw y:", Float.toString(rawy));

        /*Log.d("diff x:", Float.toString(rawx-x_longPress));
        Log.d("diff y:", Float.toString(rawy-y_longPress));*/

        float diff_x= rawx-(x_longPress);
        float diff_y= rawy-(y_longPress);



        //createSelection(diff_x,diff_y);



    }





}
