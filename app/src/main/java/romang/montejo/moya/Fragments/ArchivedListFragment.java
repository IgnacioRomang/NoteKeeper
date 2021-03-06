package romang.montejo.moya.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import romang.montejo.moya.Holders.ReminderAdapter;
import romang.montejo.moya.Model.Reminder;
import romang.montejo.moya.Persistence.DbCallBacks;
import romang.montejo.moya.Persistence.StorageManager;
import romang.montejo.moya.databinding.FragmentArchivedListBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArchivedListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArchivedListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ReminderAdapter adapter;
    private FragmentArchivedListBinding binding;
    private MutableLiveData<List<Reminder>> reminders;
    private String mParam1;
    private String mParam2;

    public ArchivedListFragment() {
        // Required empty public constructor
    }

    public static ArchivedListFragment newInstance(String param1, String param2) {
        ArchivedListFragment fragment = new ArchivedListFragment();
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
        binding = FragmentArchivedListBinding.inflate(inflater, container, false);
        reminders = new MutableLiveData<>();
        reminders.setValue(new ArrayList<>());
        adapter = new ReminderAdapter(reminders.getValue());
        adapter.setContext(getContext());
        StorageManager.getInstance(null).getArchivedReminders(new DbCallBacks.getRemainderCallback() {
            @Override
            public void result(boolean exito, List<Reminder> recordatorios) {
                if (exito) {
                    reminders.postValue(recordatorios);
                }
                binding.archivedprocessbar.setVisibility(View.INVISIBLE);
            }
        });
        reminders.observe(getViewLifecycleOwner(), new Observer<List<Reminder>>() {
            @Override
            public void onChanged(List<Reminder> reminders) {
                adapter.setList(reminders);
                adapter.notifyDataSetChanged();
            }
        });
        binding.loadingArchived.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.loadingArchived.setLayoutManager(layoutManager);
        binding.loadingArchived.setAdapter(adapter);
        return binding.getRoot();
    }
}