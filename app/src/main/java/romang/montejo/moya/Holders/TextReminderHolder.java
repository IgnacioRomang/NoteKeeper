package romang.montejo.moya.Holders;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import romang.montejo.moya.R;
import romang.montejo.moya.databinding.HolderReminderTextBinding;

public class TextReminderHolder extends RecyclerView.ViewHolder {
    public HolderReminderTextBinding binding;


    public TextReminderHolder(@NonNull View itemView) {
        super(itemView);
        binding = HolderReminderTextBinding.bind(itemView);
        binding.textCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.reminderTextCard.getLineCount() >= 6) {
                    Dialog dialog = new Dialog(v.getContext());
                    dialog.setContentView(R.layout.dialog_inspection_text_reminder);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    TextView textView = dialog.findViewById(R.id.titleTextCard);
                    textView.setText(binding.titleTextCard.getText());
                    textView = dialog.findViewById(R.id.timeTextCard);
                    textView.setText(binding.timeTextCard.getText());
                    textView = dialog.findViewById(R.id.reminderTextCard);
                    textView.setText(binding.reminderTextCard.getText());
                    ImageView imageView = dialog.findViewById(R.id.exitb);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                }
                else{
                    Toast.makeText(v.getContext(),v.getContext().getResources().getString(R.string.text_no_size),Toast.LENGTH_LONG).show();
                }
            }

        });

    }
    public ImageView getRemove(){
        return binding.remove;
    }

    public void setTitle(String title) {
        binding.titleTextCard.setText("Titulo: " + title);
    }

    public void setReminder(String reminder) {
        binding.reminderTextCard.setText(reminder);
    }

    public void setTime(String time) {
        binding.timeTextCard.setText(time);
    }
}
