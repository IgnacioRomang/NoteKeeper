package romang.montejo.moya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.util.Date;

import romang.montejo.moya.Model.AudioReminder;
import romang.montejo.moya.Util.MediaPlayerInHolderManager;
import romang.montejo.moya.Util.ParcelableUtil;
import romang.montejo.moya.databinding.ActivityPlayerBinding;

public class MediaPlayerActivity extends AppCompatActivity {
    private ActivityPlayerBinding binding;
    private static final float[] speed = {0.5f,0.25f,1,1.25f,1.5f};
    private static int currentSpeed = 2;
    private MediaPlayer mediaPlayer;
    private Runnable timer = new Runnable() {
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int pos = mediaPlayer.getCurrentPosition();
                binding.progressbar.setProgress(pos);
                binding.timeText.setText(MediaPlayerInHolderManager.formatdate.format(new Date(mediaPlayer.getCurrentPosition())));
                if (mediaPlayer.isPlaying()) {
                    binding.progressbar.postDelayed(timer,1000);
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        AudioReminder audreminder = ParcelableUtil.unmarshall(getIntent().getExtras().getByteArray("audio"), AudioReminder.CREATOR);
        binding.reminderTitle.setText(audreminder.getTitle());
        File file = new File(audreminder.filePath);
        if(!file.exists()){
            binding.playStop.setActivated(false);
            binding.moreSpeed.setActivated(false);
            binding.lessSpeed.setActivated(false);
            binding.timeText.setText("ERROR");
        }
        else{
            mediaPlayer= MediaPlayer.create(getApplicationContext(), Uri.parse(audreminder.filePath));
        }
        binding.speed.setText("x"+String.valueOf(speed[currentSpeed]));
        binding.moreSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSpeed(true);
            }
        });
        binding.lessSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSpeed(false);
            }
        });
        binding.playStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    binding.playStop.setIconResource(R.drawable.ic_baseline_play_arrow_24);
                    mediaPlayer.pause();
                }
                else{
                    binding.progressbar.postDelayed(timer,1000);
                    binding.playStop.setIconResource(R.drawable.ic_baseline_pause_24);
                    mediaPlayer.start();
                }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                binding.playStop.setIconResource(R.drawable.ic_baseline_play_arrow_24);
                binding.timeText.setText("00:00");
                binding.progressbar.setProgress(0);
            }
        });
        binding.progressbar.setProgress(0);
        binding.progressbar.setMax(mediaPlayer.getDuration());
        setContentView(binding.getRoot());
    }
    public void setSpeed(boolean sum){
        if(sum){ if(currentSpeed<4){ currentSpeed++;} }
        else{ if(currentSpeed>0){currentSpeed--;}}
        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed[currentSpeed]));
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            binding.playStop.setIconResource(R.drawable.ic_baseline_play_arrow_24);
        }
        binding.speed.setText("x"+String.valueOf(speed[currentSpeed]));
    }
}