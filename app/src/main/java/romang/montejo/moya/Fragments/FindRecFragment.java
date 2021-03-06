package romang.montejo.moya.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import romang.montejo.moya.Holders.ReminderAdapter;
import romang.montejo.moya.Model.Reminder;
import romang.montejo.moya.Persistence.DbCallBacks;
import romang.montejo.moya.Persistence.StorageManager;
import romang.montejo.moya.R;
import romang.montejo.moya.ViewModels.MainViewModel;
import romang.montejo.moya.databinding.FragmentFindRecBinding;


public class FindRecFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentFindRecBinding binding;
    private MutableLiveData<List<Reminder>> reminders;
    private MainViewModel viewModel;
    private ReminderAdapter adapter;
    private MutableLiveData<Boolean> notFinded;
    private String mParam1;
    private String mParam2;

    public FindRecFragment() {
        // Required empty public constructor
    }

    public static FindRecFragment newInstance(String param1, String param2) {
        FindRecFragment fragment = new FindRecFragment();
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
        notFinded = new MutableLiveData<>();
        //notFinded.setValue(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFindRecBinding.inflate(inflater, container, false);
        reminders = new MutableLiveData<>();
        adapter = new ReminderAdapter(new ArrayList<>());
        adapter.setContext(getContext());
        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        notFinded.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (!aBoolean) {
                    binding.searchprogressbar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "No se encontro nada con ese nombre", Toast.LENGTH_LONG).show();
                }
            }
        });
        binding.searchb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.search.clearFocus();
                String search = binding.search.getText().toString();
                if (search.isEmpty()) {
                    Toast.makeText(getContext(), getString(R.string.no_text_search), Toast.LENGTH_LONG).show();
                } else {
                    binding.searchprogressbar.setVisibility(View.VISIBLE);
                    StorageManager.getInstance(null).getFindRemineders(search, new DbCallBacks.getRemainderCallback() {
                        @Override
                        public void result(boolean exito, List<Reminder> remindersRoom) {
                            if (exito) {
                                reminders.postValue(viewModel.filter(remindersRoom, binding.chipGroup.getCheckedChipIds()));
                            } else {
                                notFinded.postValue(exito);
                            }
                        }
                    });
                }
            }
        });
        reminders.observe(getViewLifecycleOwner(), new Observer<List<Reminder>>() {
            @Override
            public void onChanged(List<Reminder> reminders) {
                adapter.setList(reminders);
                adapter.notifyDataSetChanged();
                binding.searchprogressbar.setVisibility(View.INVISIBLE);
            }
        });
        binding.searchrecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.searchrecyclerView.setLayoutManager(layoutManager);
        binding.searchrecyclerView.setAdapter(adapter);
        return binding.getRoot();
    }
}