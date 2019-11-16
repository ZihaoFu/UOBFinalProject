package fuzihao.test1;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
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
import android.widget.TextView;

import java.io.IOException;

public class FloatingMusicPlayerService extends Service {
    public static boolean isStarted = false;

    private View displayView;

    public static WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    public static ImageButton btnMusic;
    private TextView txtMusic;

    private long startTime = 0;
    private long endTime = 0;

    String selectCode;

    private int isPlay = 0;

    public static Player player = new Player();

    @Override
    public void onCreate() {
        super.onCreate();

        isStarted = true;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
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
        selectCode = intent.getStringExtra("code");
        isPlay = 0;
        player = new Player();
        showFloatingWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void showFloatingWindow() {
        isStarted = true;



        if (Settings.canDrawOverlays(this)) {
            btnMusic = new ImageButton(getApplicationContext());
            btnMusic.setImageDrawable(getDrawable(R.drawable.musicplay));
            btnMusic.setBackgroundColor(Color.TRANSPARENT);
            windowManager.addView(btnMusic, layoutParams);

            btnMusic.setOnTouchListener(new FloatingOnTouchListener());
        }

//        if (Settings.canDrawOverlays(this)) {
//            LayoutInflater layoutInflater = LayoutInflater.from(this);
//            displayView = layoutInflater.inflate(R.layout.image_button, null);
//
//            btnMusic = displayView.findViewById(R.id.btnMusic);
//            btnMusic.setImageDrawable(getDrawable(R.drawable.musicplay));
//            btnMusic.setBackgroundColor(Color.BLUE);
//            txtMusic = displayView.findViewById(R.id.txtMusic);
//            txtMusic.setText("ok");
//            windowManager.addView(displayView, layoutParams);
//            displayView.setOnTouchListener(new FloatingOnTouchListener());
//        }
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;
        private int originX;
        private int originY;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    originX = x;
                    originY = y;
                    startTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                case MotionEvent.ACTION_UP:
                    endTime = System.currentTimeMillis();
                    if ((endTime - startTime) < 0.1 * 1000L) {
//                        final MediaPlayer mediaPlayer = new MediaPlayer();
                        if (isPlay%2==0){
                            btnMusic.setImageDrawable(getDrawable(R.drawable.musicstop));
                            if(isPlay==0){
                                player.playUrl("http://www.nationalanthems.info/"+selectCode+".mp3");
                            }else{
                                player.play();
                            }



//                            try {
//                                mediaPlayer.setDataSource("http://www.nationalanthems.info/"+selectCode+".mp3");
//                                //3 准备播放
//                                mediaPlayer.prepareAsync();
//                                //3.1 设置一个准备完成的监听
//                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                                    @Override
//                                    public void onPrepared(MediaPlayer mp) {
//                                        // 4 开始播放
//                                        mediaPlayer.start();
//                                    }
//                                });
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
                        }else {
                            btnMusic.setImageDrawable(getDrawable(R.drawable.musicplay));
                            player.pause();
//                            mediaPlayer.stop();
//                            mediaPlayer.release();
                        }
                        isPlay = isPlay + 1;

                    }
                    else if((endTime - startTime) >= 2 * 1000L && layoutParams.x-originX < 1 && layoutParams.y-originY < 1){
                        windowManager.removeView(btnMusic);
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
