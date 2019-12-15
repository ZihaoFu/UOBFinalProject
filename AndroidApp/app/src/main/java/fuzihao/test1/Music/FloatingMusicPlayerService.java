package fuzihao.test1.Music;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import fuzihao.test1.R;

public class FloatingMusicPlayerService extends Service {
    public static boolean isStarted = false;

    public static WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private WindowManager.LayoutParams layoutParams2;

    public static ImageButton btnMusic;
    public static TextView txtMusic;

    private long startTime = 0;
    private long endTime = 0;

    String selectCode;

    private int isPlay = 0;

    public static Player player = new Player();

    @Override
    public void onCreate() {
        super.onCreate();

        isStarted = true;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE); //Call service
        layoutParams = new WindowManager.LayoutParams();
        // Determine whether the build version meets the requirements
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        // Set the parameters of the View of floating button
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = 150;
        layoutParams.height = 150;
        layoutParams.x = 300;
        layoutParams.y = 300;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        selectCode = intent.getStringExtra("code"); // get country code to play right national anthem
        isPlay = 0; // reset isPlay
        player = new Player(); // Initialize the player
        showFloatingWindow(); // call showFloatingWindow function
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void showFloatingWindow() {
        isStarted = true;

        if (Settings.canDrawOverlays(this)) {
            // add text to describe current country
            txtMusic = new TextView(getApplicationContext());
            txtMusic.setText(selectCode.toUpperCase());
            txtMusic.setTextColor(getColor(R.color.colorPrimary));
            windowManager.addView(txtMusic, layoutParams); // add floating button to view

            // play button
            btnMusic = new ImageButton(getApplicationContext());
            btnMusic.setImageDrawable(getDrawable(R.drawable.musicplay)); // set Initial icon
            btnMusic.setBackgroundColor(Color.TRANSPARENT);// set floating button colour
            windowManager.addView(btnMusic, layoutParams); // add floating button to view

            btnMusic.setOnTouchListener(new FloatingOnTouchListener()); // set touch events
        }
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;
        private int originX;
        private int originY;

        // add touch event to floating button
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //get position of touch point
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    originX = x;
                    originY = y;
                    startTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //get current position of touch point
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager.updateViewLayout(view, layoutParams);
                    windowManager.updateViewLayout(txtMusic, layoutParams);
                    break;
                case MotionEvent.ACTION_UP:
                    endTime = System.currentTimeMillis();
                    // click event
                    if ((endTime - startTime) < 0.1 * 1000L) {
                        if (isPlay%2==0){
                            btnMusic.setImageDrawable(getDrawable(R.drawable.musicstop)); // switch icon
                            if(isPlay==0){
                                selectCode = selectCode.toLowerCase();
                                player.playUrl("http://www.nationalanthems.info/"+selectCode+".mp3"); // set music URL
                            }else{
                                player.play();
                            }
                        }else {
                            btnMusic.setImageDrawable(getDrawable(R.drawable.musicplay));// switch icon
                            player.pause();
                        }
                        isPlay = isPlay + 1;
                    }
                    // close floating button
                    else if((endTime - startTime) >= 2 * 1000L && layoutParams.x-originX < 1 && layoutParams.y-originY < 1){
                        windowManager.removeView(btnMusic);
                        windowManager.removeView(txtMusic);
                        player.stop();
                        isStarted = false;
                    }
                default:
                    break;
            }
            return false;
        }
    }
}
