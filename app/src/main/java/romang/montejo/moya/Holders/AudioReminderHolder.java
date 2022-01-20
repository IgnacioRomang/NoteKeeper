package romang.montejo.moya.Holders;

import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import romang.montejo.moya.Util.MediaPlayerInHolderManager;
import romang.montejo.moya.databinding.HolderReminderAudioBinding;

public class AudioReminderHolder extends RecyclerView.ViewHolder {
    private HolderReminderAudioBinding binding;
    private Long record_time;
    private String filePath;

    public Long getRecord_time() {
        return record_time;
    }
    public void setRecord_time(Long record_time) {
        this.record_time = record_time;
    }
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public void setTitle(String title){
        binding.titleTextCard.setText(title);
    }
    public void setTime(String time){
        binding.timeTextCard.setText(time);
    }
    public MaterialButton getPlayButton(){return binding.playHolderbutton;}
    public SeekBar getSeekBar(){return binding.seekBar;}
    public TextView getTimerText(){return binding.timer;}
    public MaterialCardView getCardView(){return binding.PlayerCardView;}

    public AudioReminderHolder(@NonNull View itemView) {
        super(itemView);
        binding = HolderReminderAudioBinding.bind(itemView);
        binding.timer.setText("00:00");
        binding.playHolderbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayerInHolderManager.getInstance(v.getContext(),AudioReminderHolder.this);
                binding.playHolderbutton.callOnClick();
            }
        });
    }
}
