package romang.montejo.moya;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.SeekBar;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import romang.montejo.moya.Holders.AudioReminderHolder;
import romang.montejo.moya.Persistence.DAO;
import romang.montejo.moya.Persistence.MyRoomDB;
import romang.montejo.moya.Persistence.StorageManager;

public class MediaPlayerInHolderManager {
    private AudioReminderHolder currentHolder;
    private static MediaPlayerInHolderManager instance;
    private MediaPlayer player;


    private MediaPlayerInHolderManager(Context ctx, AudioReminderHolder holder) {
        currentHolder = holder;
        player = MediaPlayer.create(ctx, Uri.parse(holder.getFilePath()));
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                currentHolder.getSeekBar().setMax(player.getDuration() / 1000);
                currentHolder.getSeekBar().postDelayed(onEverySecond, 1000);
            }
        });
        currentHolder.getPlayButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    currentHolder.getPlayButton().setIconResource(R.drawable.ic_baseline_play_arrow_24);
                    pause();
                } else {
                    currentHolder.getPlayButton().setIconResource(R.drawable.ic_baseline_pause_24);
                    try {
                        play();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                currentHolder.getPlayButton().setIconResource(R.drawable.ic_baseline_play_arrow_24);
            }
        });
        currentHolder.getSeekBar().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int seek;
                    if (fromUser) {
                        seek = (progress * player.getDuration()) / 100;
                        player.seekTo(seek);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public static MediaPlayerInHolderManager getInstance(Context ctx, AudioReminderHolder holder) {
        if (instance == null) {
            instance = new MediaPlayerInHolderManager(ctx, holder);
        } else {
            if (instance.currentHolder.hashCode() != holder.hashCode()) {
                // no estoy seguro que sea otro
                instance.clear();
                instance = new MediaPlayerInHolderManager(ctx, holder);
            }
        }
        return instance;
    }

    private SimpleDateFormat formatdate = new SimpleDateFormat("mm:ss");
    private Runnable onEverySecond = new Runnable() {
        public void run() {
            if (player != null) {
                // TODO: 17/1/2022 agregar formato al texto MM:ss
                int pos = player.getCurrentPosition() / 1000;
                currentHolder.getTimerText().setText(formatdate.format(new Date(player.getCurrentPosition())));
                currentHolder.getSeekBar().setProgress(pos);
                if (player.isPlaying()) {
                    currentHolder.getSeekBar().postDelayed(onEverySecond, 1000);
                }
            }
        }
    };

    public void clear() {
        instance.player.stop();
        instance.player.release();
        instance.player = null;
        currentHolder.getSeekBar().setProgress(0);
        currentHolder.getPlayButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayerInHolderManager.getInstance(v.getContext(), currentHolder);
                currentHolder.getPlayButton().callOnClick();
            }
        });
        currentHolder.getSeekBar().setOnSeekBarChangeListener(null);
    }

    public void play() throws IOException {
        if (player.getCurrentPosition() != 0) {
            currentHolder.getSeekBar().postDelayed(onEverySecond, 1000);
        }
        player.start();
    }

    public void pause() {
        player.pause();
    }
}

