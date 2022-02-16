package romang.montejo.moya.Holders;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import romang.montejo.moya.R;
import romang.montejo.moya.databinding.HolderReminderPhotoBinding;

public class PhotoReminderHolder extends RecyclerView.ViewHolder {
    public HolderReminderPhotoBinding binding;
    public boolean defaultPhoto;

    public PhotoReminderHolder(@NonNull View itemView) {
        super(itemView);
        binding = HolderReminderPhotoBinding.bind(itemView);
        defaultPhoto=false;
        binding.bigCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.dialog_inspection_photo_reminder);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView textView = dialog.findViewById(R.id.titleCardPhoto);
                textView.setText(binding.titleCardPhoto.getText());
                textView = dialog.findViewById(R.id.timeCardPhoto);
                textView.setText(binding.timeCardPhoto.getText());
                ImageView imageView = dialog.findViewById(R.id.exitButton);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                imageView = dialog.findViewById(R.id.photoview);
                imageView.setImageBitmap(((BitmapDrawable)binding.photoview.getDrawable()).getBitmap());
                if(defaultPhoto){
                    MaterialCardView bigCard = dialog.findViewById(R.id.inspCardView);
                    bigCard.setCardBackgroundColor(v.getContext().getColor(R.color.md_red_200));
                }
                dialog.show();
            }
        });

    }
    public ImageView getRemove(){
        return binding.removep;
    }
    public MaterialCardView getCardView(){return binding.bigCard;}

    public void setTitle(String title) {
        binding.titleCardPhoto.setText("Titulo: " + title);
    }

    public void setReminder(Bitmap photo) {
        binding.photoview.setImageBitmap(photo);
    }

    public void setTime(String time) {
        binding.timeCardPhoto.setText(time);
    }
}
