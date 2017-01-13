package com.example.framgia.alarmclock.utility;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import com.example.framgia.alarmclock.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by framgia on 28/07/2016.
 */
public class MusicPlayerUtils {
    private static final String URI_RESOURCE = "android.resource://";
    private static MediaPlayer mMediaPlayer;
    private final static int VOLUME_RATIO = 5;

    public static void playMusic(Context context) {
        String path =""+R.raw.bells;
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setLooping(true);
            if (isSound(path))
                mMediaPlayer.setDataSource(context,
                        Uri.parse(URI_RESOURCE + context.getPackageName() + File.separator + path));
            else mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            ToastUtils.showToast(context, R.string.error_play_song);
        }
    }

    public static void setVolume(Activity activity, int volume) {
        AudioManager audioManager =
                (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume / VOLUME_RATIO,
                AudioManager.FLAG_PLAY_SOUND);
    }

    private static boolean isSound(String path) {
        int idPath;
        try {
            idPath = Integer.parseInt(path);
        } catch (NumberFormatException ex) {
            return false;
        }
        switch (idPath) {
            case R.raw.bells:
                return true;
            default:
                return false;
        }
    }

    public static void stopMusic() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) mMediaPlayer.stop();
    }
}
