package romang.montejo.moya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import romang.montejo.moya.Fragments.SettingsFragment;
import romang.montejo.moya.Model.AudioReminder;
import romang.montejo.moya.Model.PhotoReminder;
import romang.montejo.moya.Model.Reminder;
import romang.montejo.moya.Persistence.DbCallBacks;
import romang.montejo.moya.Persistence.StorageManager;
import romang.montejo.moya.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private MainViewModel viewModel;
    private Boolean start;

    private Runnable tenSecond = new Runnable() {
        public void run() {
            binding.progressBar.setVisibility(View.GONE);
            start=true;
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

        NotificationsManager.getInstance(getBaseContext());

        start = false;
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        viewModel.liveData = new MutableLiveData<>();

        List<Reminder> lista = new ArrayList<>();
        /*
        AudioReminder test1 = new AudioReminder("hOLA",new Long(1321321),true);
        test1.setFilePath("SADADAS");
        PhotoReminder test2 = new PhotoReminder("Esto no tiene foto",new Long(1321321),true);
        test2.setCurrentPhotoPath("fasfasafsfa");
        lista.add(test1);
        lista.add(test2);
        */

        // TODO: 18/1/2022 crear una clase que maneje las notificaciones que sea singleton 

        viewModel.liveData.setValue(lista);

        viewModel.liveData.observe(MainActivity.this, new Observer<List<Reminder>>() {
            @Override
            public void onChanged(List<Reminder> reminders) {
                if (!reminders.isEmpty()) {
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });
        binding.progressBar.postDelayed(tenSecond, 10000);
        viewModel.setContext(getApplicationContext());
        viewModel.getResult().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean && start) {
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
    }
    @Override
    public boolean onSupportNavigateUp() {

        if (!(navController.navigateUp() || super.onSupportNavigateUp())) {
            onBackPressed();
        }
        return true;
    }
}