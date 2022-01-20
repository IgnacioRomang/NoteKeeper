package romang.montejo.moya.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import romang.montejo.moya.R;
import romang.montejo.moya.MainViewModel;
import romang.montejo.moya.databinding.FragmentAddTextReminderBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddTextReminderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTextReminderFragment extends Fragment {
    private FragmentAddTextReminderBinding binding;
    private MaterialDatePicker datePicker;
    private MaterialTimePicker timePicker;
    private MutableLiveData<Calendar> calendarLiveData;
    private static Boolean error;

    private MainViewModel viewModel;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public AddTextReminderFragment() {
        // Required empty public constructor
    }
    public static AddTextReminderFragment newInstance(String param1, String param2) {
        AddTextReminderFragment fragment = new AddTextReminderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        error = false;
        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddTextReminderBinding.inflate(inflater, container, false);
        calendarLiveData = new MutableLiveData<>();
        calendarLiveData.setValue(Calendar.getInstance());
        //set de los pickets

        datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.datePicker))
                .setSelection(calendarLiveData.getValue().getTimeInMillis())
                .build();

        binding.dateText.setText(viewModel.getTimeString(calendarLiveData.getValue().getTimeInMillis()));

        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(calendarLiveData.getValue().get(Calendar.HOUR))
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


        // Configuro botones para mostrar los Pickers
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

        binding.addFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //verifico que los campos ttitulo y cuerpo no esten empty
                if (binding.tituloEditText.getText().toString().isEmpty()) {
                    binding.editTextTextTituloName.setError(getString(R.string.no_titulo));
                    error = true;
                } else {
                    error = false;
                    binding.editTextTextTituloName.setError(null);
                }
                if (binding.reminderEditText.getText().toString().isEmpty()) {
                    error = true;
                    binding.editTextTextMultiLine.setError(getString(R.string.no_reminder));
                } else {
                    error = false;
                    binding.editTextTextMultiLine.setError(null);
                }
                if (error) {
                    Toast.makeText(getActivity().getBaseContext(), getString(R.string.error_text_reminder), Toast.LENGTH_LONG).show();
                } else {
                    viewModel.getCalendarMutableLiveData().setValue(calendarLiveData.getValue());
                    viewModel.createTextReminder(binding.tituloEditText.getText().toString(),
                            binding.reminderEditText.getText().toString(),
                            binding.checkNotif.isChecked());
                    NavHostFragment.findNavController(AddTextReminderFragment.this).navigate(R.id.action_addTextReminderFragment_to_listFragment);
                }
            }
        });
        return binding.getRoot();
    }


}