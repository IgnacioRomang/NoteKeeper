package romang.montejo.moya.Activitys;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import romang.montejo.moya.R;
import romang.montejo.moya.Util.NotificationsManager;
import romang.montejo.moya.ViewModels.MainViewModel;
import romang.montejo.moya.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private MainViewModel viewModel;
    private MutableLiveData<Boolean> start;

    private Runnable tenSecond = new Runnable() {
        public void run() {
            binding.progressBar.setVisibility(View.GONE);
            //start.postValue(true);
        }
    };

    @Override
    protected void attachBaseContext(Context baseContext) {
        super.attachBaseContext(baseContext);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(baseContext);
        boolean isDark = prefs.getBoolean("darkmode", false);

        if (isDark)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NotificationsManager.context = getApplicationContext();
        //NotificationsManager notificationsManager = NotificationsManager.getInstance(getBaseContext());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.result = new MutableLiveData<>();
        viewModel.resultSave = new MutableLiveData<>();

        //viewModel.liveData = new MutableLiveData<>();


        /*
        List<Reminder> lista = new ArrayList<>();
        AudioReminder test1 = new AudioReminder("Audio sin audio",new Long(1321321),true);
        test1.setFilePath("No path");
        PhotoReminder test2 = new PhotoReminder("Foto sin Foto",new Long(1321321),true);
        test2.setCurrentPhotoPath("No path");
        lista.add(test1);
        lista.add(test2);
        */
        viewModel.getResult().observe(MainActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.progressBar.setVisibility(View.GONE);
                viewModel.adapter.notifyDataSetChanged();
            }
        });

        binding.progressBar.postDelayed(tenSecond, 10000);
        viewModel.resultSave.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(getBaseContext(), getString(R.string.save_ok), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.save_sad), Toast.LENGTH_SHORT).show();
                }
            }
        });
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (binding.progressBar.getVisibility() == View.VISIBLE && destination.getId() != R.id.listFragment) {
                    binding.progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (!(navController.navigateUp() || super.onSupportNavigateUp())) {
            onBackPressed();
        }
        return true;
    }

}