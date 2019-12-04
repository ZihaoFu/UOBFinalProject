package fuzihao.test1.Music;

import android.media.AudioManager;
import android.media.MediaPlayer;

import fuzihao.test1.R;

import static fuzihao.test1.Music.FloatingMusicPlayerService.btnMusic;

public class Player implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    public MediaPlayer mediaPlayer;

    public Player(){
        //Initialize the player and set some parameters
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(this);
        }catch (Exception e) {

        }
    }

    // play media
    public void play(){
        mediaPlayer.start();
    }

    public void playUrl(String musicUrl) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(musicUrl); // set media data URL
            mediaPlayer.prepare();//Play automatically after prepare
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    btnMusic.setImageResource(R.drawable.musicplay); // set Initial icon
                }
            });
        } catch (Exception e) {
        }
    }

    // pause media
    public void pause()
    {
        mediaPlayer.pause();
    }

    // stop media
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    //Play automatically after prepare
    @Override
    public void onPrepared(MediaPlayer arg0) {
        arg0.start();
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
    }
}
