package romang.montejo.moya.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import romang.montejo.moya.Model.Reminder;
import romang.montejo.moya.Persistence.DbCallBacks;
import romang.montejo.moya.Persistence.StorageManager;
import romang.montejo.moya.R;
import romang.montejo.moya.Holders.ReminderAdapter;
import romang.montejo.moya.MainViewModel;
import romang.montejo.moya.databinding.FragmentListBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {
    private FragmentListBinding binding;
    private MainViewModel viewModel;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int OK_CODE = 0;
    private static final int IMG_CODE = 393963;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
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
        binding = FragmentListBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        viewModel.adapter = new ReminderAdapter(viewModel.getList());
        StorageManager.getInstance(getActivity().getBaseContext()).getAllReminders(new DbCallBacks.getRemainderCallback() {
            @Override
            public void result(boolean exito, List<Reminder> recordatorios) {
                viewModel.setList(recordatorios);
                if(viewModel.adapter.getItemCount()== 0){
                    viewModel.adapter.addList(recordatorios);
                }
            }
        });
        binding.recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(viewModel.adapter);

        binding.floatingActionButton.setMainFabClosedDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24));
        binding.floatingActionButton.addActionItem(new SpeedDialActionItem.Builder(R.id.photo_button, R.drawable.ic_baseline_add_a_photo_24)
                .setFabBackgroundColor(getResources().getColor(R.color.md_blue_200))
                .setFabImageTintColor(Color.BLACK)
                .create());
        binding.floatingActionButton.addActionItem(new SpeedDialActionItem.Builder(R.id.audio, R.drawable.ic_baseline_mic_24)
                .setFabBackgroundColor(getResources().getColor(R.color.md_green_200))
                .setFabImageTintColor(Color.BLACK)
                .create());
        binding.floatingActionButton.addActionItem(new SpeedDialActionItem.Builder(R.id.text, R.drawable.ic_baseline_create_24)
                .setFabBackgroundColor(getResources().getColor(R.color.md_orange_100))
                .setFabImageTintColor(Color.BLACK)
                .create());
        binding.floatingActionButton.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override

            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                // TODO: 7/1/2022 Agregar mas botones
                switch (actionItem.getId()) {
                    case R.id.text:
                        NavHostFragment.findNavController(ListFragment.this).navigate(R.id.action_listFragment_to_addTextReminderFragment);
                        break;
                    case R.id.photo_button:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ContextCompat.checkSelfPermission(getActivity().getBaseContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                camaraStart();
                            } else {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 0);
                                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                    Toast.makeText(getContext(), getString(R.string.permisos_camera_error), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        break;
                    case R.id.audio:
                        NavHostFragment.findNavController(ListFragment.this).navigate(R.id.action_listFragment_to_addAudioReminderFragment);
                        break;
                }
                return false;
            }
        });
        return binding.getRoot();
    }
    private File createImageFile() throws IOException {
        // https://developer.android.com/training/camera/photobasics?hl=es-419
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        viewModel.currentPath = image.getAbsolutePath();
        return image;
    }
    private void camaraStart() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.provider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, IMG_CODE);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu,menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ajustesSelect:
                NavHostFragment.findNavController(ListFragment.this).navigate(R.id.action_listFragment_to_settingsFragment);
                break;
            case R.id.app_bar_search:
                NavHostFragment.findNavController(ListFragment.this).navigate(R.id.action_listFragment_to_findRecFragment);
                break;
            case R.id.archived:
                NavHostFragment.findNavController(ListFragment.this).navigate(R.id.action_listFragment_to_archivedListFragment);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //esto quizas no es necesario pero por las dudas
            switch (requestCode) {
                case IMG_CODE:
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(Uri.parse(viewModel.currentPath));
                    getActivity().sendBroadcast(mediaScanIntent);
                    //viewModel.setPhoto((Bitmap)data.getExtras().get("data"));
                    NavHostFragment.findNavController(ListFragment.this).navigate(R.id.action_listFragment_to_addPhotoReminderFragment);
                    break;
            }

        }
    }
}