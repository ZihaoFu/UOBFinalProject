package fuzihao.test1.Music;

import android.media.AudioManager;
import android.media.MediaPlayer;

import fuzihao.test1.R;

import static fuzihao.test1.Music.FloatingMusicPlayerService.btnMusic;

public class Player implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
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
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    btnMusic.setImageResource(R.drawable.musicplay);
                }
            });
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

    @Override
    public void onCompletion(MediaPlayer arg0) {
    }
}
