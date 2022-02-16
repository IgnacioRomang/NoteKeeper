package romang.montejo.moya.Fragments;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordPermissionHandler;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import romang.montejo.moya.ViewModels.MainViewModel;
import romang.montejo.moya.R;
import romang.montejo.moya.databinding.FragmentAddAudioReminderBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddAudioReminderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddAudioReminderFragment extends Fragment {

    private FragmentAddAudioReminderBinding binding;
    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    //private String fileName;
    private File file;
    private long record_time;

    private MainViewModel viewModel;
    private MaterialDatePicker datePicker;
    private MaterialTimePicker timePicker;
    private MutableLiveData<Calendar> calendarLiveData;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public AddAudioReminderFragment() {
        // Required empty public constructor
    }

    public static AddAudioReminderFragment newInstance(String param1, String param2) {
        AddAudioReminderFragment fragment = new AddAudioReminderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void finishJob(){
        viewModel.currentPath= file.getAbsolutePath();
        String title = binding.tituloEditText.getText().toString();
        if(title.isEmpty()){
            title = file.getName();
        }
        if(mediaPlayer.isPlaying()){mediaPlayer.stop();}
        viewModel.getCalendarMutableLiveData().setValue(calendarLiveData.getValue());
        viewModel.createAudioReminder(title,binding.checkNotif.isChecked(),record_time);
        file = null;
        NavHostFragment.findNavController(AddAudioReminderFragment.this).navigate(R.id.action_addAudioReminderFragment_to_listFragment);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddAudioReminderBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        binding.recordButton.setRecordView(binding.recordView);

        binding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                binding.sendButton.setVisibility(View.INVISIBLE);
                binding.visualizerCard.setVisibility(View.INVISIBLE);
                binding.visualizerLineBar.setVisibility(View.VISIBLE);
                // TODO: 16/1/2022 Ocultar botones de enviar
                if (file != null) {
                    file.delete();
                    binding.visualizerCard.setVisibility(View.INVISIBLE);
                }
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                try {
                    createFile();
                    mediaRecorder.setOutputFile(file.getAbsolutePath());
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancel() {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                file.delete();
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                binding.sendButton.setVisibility(View.VISIBLE);
                binding.visualizerCard.setVisibility(View.VISIBLE);
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                record_time = recordTime;
                mediaPlayer= MediaPlayer.create(getContext(), Uri.fromFile(file));
                binding.visualizerLineBar.setVisibility(View.VISIBLE);
                binding.visualizerLineBar.setColor(getContext().getColor(R.color.md_blue_200));
                binding.visualizerLineBar.setDensity(60);
                binding.visualizerLineBar.setPlayer(mediaPlayer.getAudioSessionId());
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        binding.playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    }
                });
            }

            @Override
            public void onLessThanSecond() {

            }
        });

        binding.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    binding.playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    mediaPlayer.pause();
                } else {
                    binding.playButton.setImageResource(R.drawable.ic_baseline_pause_24);
                    mediaPlayer.start();
                }
            }
        });

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle(getString(R.string.adv_titulo))
                        .setMessage(getString(R.string.adv_content));
                dialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishJob();
                    }
                });
                dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                if(calendarLiveData.getValue().getTimeInMillis() <= (Calendar.getInstance().getTimeInMillis()-360*1000)&& binding.checkNotif.isChecked()){
                    dialog.show();
                }
                else{
                    finishJob();
                }
            }
        });

        binding.recordView.setRecordPermissionHandler(new RecordPermissionHandler() {
            @Override
            public boolean isPermissionGranted() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return true;
                }
                boolean recordPermissionAvailable = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED;
                if (recordPermissionAvailable) {
                    return true;
                }
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);
                return false;
            }
        });
        calendarLiveData = new MutableLiveData<>();
        calendarLiveData.setValue(Calendar.getInstance());

        datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.datePicker))
                .setSelection(calendarLiveData.getValue().getTimeInMillis())
                .build();

        binding.dateText.setText(viewModel.getTimeString(calendarLiveData.getValue().getTimeInMillis()));
        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(calendarLiveData.getValue().get(Calendar.HOUR_OF_DAY))
                .setMinute(calendarLiveData.getValue().get(Calendar.MINUTE))
                .build();

        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.getCalendarMutableLiveData().postValue(viewModel.setTime(calendarLiveData.getValue(),timePicker.getHour(),timePicker.getMinute()));
            }
        });
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                viewModel.getCalendarMutableLiveData().postValue(viewModel.setDate(calendarLiveData.getValue(),selection));
            }
        });
        viewModel.getCalendarMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Calendar>() {
            @Override
            public void onChanged(Calendar calendar) {
                binding.dateText.setText(viewModel.getTimeString(calendarLiveData.getValue().getTimeInMillis()));
            }
        });
        binding.checkNotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                binding.clockButton.setClickable(isChecked);
                binding.calendarButton.setClickable(isChecked);
                if (isChecked) {
                    binding.NofiCard.setCardBackgroundColor(getResources().getColor(R.color.md_blue_50));
                } else {
                    binding.NofiCard.setCardBackgroundColor(getResources().getColor(R.color.md_grey_400));
                }
            }
        });
        binding.calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(getActivity().getSupportFragmentManager(), "DatePicker");
            }
        });
        binding.clockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.show(getActivity().getSupportFragmentManager(), "TimePicker");
            }
        });
        return binding.getRoot();
    }

    private void createFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String audioFileName = "3GP_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = File.createTempFile(
                audioFileName,
                ".3gp",
                storageDir
        );
    }
}