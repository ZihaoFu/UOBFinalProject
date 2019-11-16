package fuzihao.test1;

import android.media.AudioManager;
import android.media.MediaPlayer;

public class Player implements MediaPlayer.OnPreparedListener{
    public MediaPlayer mediaPlayer;
    public Player(){
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(this);
        }catch (Exception e) {

        }
    }

    public void play(){
        mediaPlayer.start();
    }

    public void playUrl(String videoUrl) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.prepare();//prepare之后自动播放
        } catch (Exception e) {
        }
    }

    public void pause()
    {
        mediaPlayer.pause();
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onPrepared(MediaPlayer arg0) {
        arg0.start();
    }
}
