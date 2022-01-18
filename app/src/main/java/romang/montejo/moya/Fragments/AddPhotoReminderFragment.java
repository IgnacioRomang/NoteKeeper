package romang.montejo.moya.Fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

import romang.montejo.moya.MainViewModel;
import romang.montejo.moya.R;
import romang.montejo.moya.databinding.FragmentAddPhotoReminderBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddPhotoReminderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPhotoReminderFragment extends Fragment {

    private FragmentAddPhotoReminderBinding binding;
    private MainViewModel viewModel;
    private MaterialDatePicker datePicker;
    private MaterialTimePicker timePicker;
    private static Boolean error;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddPhotoReminderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddPhotoReminderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPhotoReminderFragment newInstance(String param1, String param2) {
        AddPhotoReminderFragment fragment = new AddPhotoReminderFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddPhotoReminderBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        binding.imageView.setImageBitmap(BitmapFactory.decodeFile(viewModel.currentPath));

        binding.dateText.setText(viewModel.getTimeString(MaterialDatePicker.todayInUtcMilliseconds()));

        viewModel.getCalendarMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Calendar>() {
            @Override
            public void onChanged(Calendar calendar) {
                binding.dateText.setText(viewModel.getTimeString(calendar.getTime().getTime()));
            }
        });

        //set de los pickets
        datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.datePicker))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(00)
                .setMinute(15)
                .build();

        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setTime(timePicker.getHour(), timePicker.getMinute());
            }
        });
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                viewModel.setDate(selection);
            }
        });
        // TODO: 10/1/2022 Realizar los observers de los livedata
        viewModel.getCalendarMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Calendar>() {
            @Override
            public void onChanged(Calendar calendar) {
                binding.dateText.setText(viewModel.getTimeString(calendar.getTime().getTime()));
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
                if (error) {
                    Toast.makeText(getActivity().getBaseContext(), getString(R.string.error_text_reminder), Toast.LENGTH_LONG).show();
                } else {
                    viewModel.createPhotoReminder(binding.tituloEditText.getText().toString(),binding.checkNotif.isChecked());
                    NavHostFragment.findNavController(AddPhotoReminderFragment.this).navigate(R.id.action_addPhotoReminderFragment_to_listFragment);
                }
            }
        });

        //viewModel.createPhotoReminder(Bitmap);
        return binding.getRoot();
    }
}