package romang.montejo.moya.Holders;

import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.loader.AssetsProvider;
import android.content.res.loader.ResourcesProvider;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Picture;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.io.File;
import java.io.FileDescriptor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import romang.montejo.moya.Holders.TextReminderHolder;
import romang.montejo.moya.Model.AudioReminder;
import romang.montejo.moya.Model.PhotoReminder;
import romang.montejo.moya.Model.Reminder;
import romang.montejo.moya.Model.TextReminder;
import romang.montejo.moya.R;

public class ReminderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int NOONE = 0;
    private static final int TEXT_TYPE = 1;
    private static final int IMG_TYPE = 2;
    private static final int AUD_TYPE = 3;

    public ReminderAdapter(List<Reminder> reminderList) {
        this.list = reminderList;
    }

    public void addReminder(Reminder reminder) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(reminder);
    }

    private List<Reminder> list;

    public List<Reminder> getList() {
        return list;
    }

    public void setList(List<Reminder> list) {
        this.list = list;
    }

    public void addList(List<Reminder> reminderList) {
        list.addAll(reminderList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // TODO: 11/1/2022 AGREGAR EL RESTO
        View view;
        RecyclerView.ViewHolder reminder = null;
        switch (viewType) {
            case TEXT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_reminder_holder, parent, false);
                reminder = new TextReminderHolder(view);
                break;
            case IMG_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_reminder_holder, parent, false);
                reminder = new PhotoReminderHolder(view);
                break;
            case AUD_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_reminder_holder, parent, false);
                reminder = new AudioReminderHolder(view);
                break;
        }
        return reminder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY - HH:mm");
        File file;
        switch (getItemViewType(position)) {
            case TEXT_TYPE:
                TextReminderHolder textReminderHolder = (TextReminderHolder) holder;
                TextReminder textReminder = (TextReminder) list.get(position);
                textReminderHolder.setTitle(textReminder.getTitle());
                textReminderHolder.setReminder(textReminder.getReminderText());
                textReminderHolder.setTime(format.format(textReminder.getTime()));
                break;
            case IMG_TYPE:
                PhotoReminderHolder photoReminderHolder = (PhotoReminderHolder) holder;
                PhotoReminder photoReminder = (PhotoReminder) list.get(position);
                photoReminderHolder.setTitle(photoReminder.getTitle());

                file = new File(photoReminder.getCurrentPhotoPath());
                Bitmap photo = null;
                if (!file.exists()) {
                    photoReminderHolder.defaultPhoto=true;
                    photoReminderHolder.getCardView().setCardBackgroundColor(ContextCompat.getColor(photoReminderHolder.getCardView().getContext(), R.color.md_red_200));
                }
                else{
                    photo = BitmapFactory.decodeFile(file.getAbsolutePath());
                    photoReminderHolder.defaultPhoto=false;
                    photoReminderHolder.setReminder(photo);
                }
                photoReminderHolder.setTime(format.format(photoReminder.getTime()));
                break;
            case AUD_TYPE:
                AudioReminderHolder audioReminderHolder = (AudioReminderHolder) holder;
                AudioReminder audioReminder = (AudioReminder) list.get(position);
                audioReminderHolder.setTitle(audioReminder.getTitle());
                audioReminderHolder.setTime(format.format(audioReminder.getTime()));
                audioReminderHolder.setFilePath(audioReminder.getFilePath());
                file = new File(audioReminder.getFilePath());
                if(!file.exists()) {
                    audioReminderHolder.getPlayButton().setIconResource(R.drawable.ic_baseline_dangerous_24);
                    audioReminderHolder.getCardView().setCardBackgroundColor(ContextCompat.getColor(audioReminderHolder.getCardView().getContext(), R.color.md_red_200));
                    audioReminderHolder.getSeekBar().setEnabled(false);
                    audioReminderHolder.setTime("File Error "+format.format(audioReminder.getTime()));
                    audioReminderHolder.getPlayButton().setOnClickListener(null);
                }
                audioReminderHolder.setRecord_time(audioReminder.getRecordTime());
                break;
        }

    }

    public int getItemViewType(int position) {
        Reminder selec = list.get(position);
        if (selec instanceof TextReminder) {
            return TEXT_TYPE;
        } else {
            if (selec instanceof PhotoReminder) {
                return IMG_TYPE;
            } else {
                if (selec instanceof AudioReminder) {
                    return AUD_TYPE;
                }
            }
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
