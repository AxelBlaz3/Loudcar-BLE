package com.wielabs.loudcar.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import com.wielabs.loudcar.R;
import com.wielabs.loudcar.databinding.ActivityMainBinding;
import com.wielabs.loudcar.ui.home.HomeFragmentDirections;
import com.wielabs.loudcar.util.Constants;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener, View.OnClickListener {
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int REQUEST_LOCATION = 2;
    private ActivityMainBinding binding;
    public NavController navController;

    @Inject
    MainActivityViewModel mainActivityViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Loudcar);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navController = ((NavHostFragment) (getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment))).getNavController();

        Intent appLinkIntent = getIntent();
        Uri intentData = appLinkIntent.getData();
        if (intentData != null)
            handleIntent(intentData);

        // Determine whether BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.speechRecognition.setOnClickListener(this);
        binding.settings.setOnClickListener(this);
        binding.home.setOnClickListener(this);
        navController.addOnDestinationChangedListener(this);

        binding.toolbar.setNavigationOnClickListener(v -> {
            navController.navigateUp();
        });
    }

    private void handleIntent(Uri intentData) {
        String queryParam = intentData.getQueryParameter(Constants.PARAM_THING_TYPE);
        if (queryParam != null && !queryParam.isEmpty())
            navController.navigate(HomeFragmentDirections.Companion.actionGlobalHomeFragment(queryParam));
    }

    @Override
    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
        switch (destination.getId()) {
            case R.id.baseIntroFragment: {
                binding.setHideNavigationIcon(true);
                binding.setHideLogo(true);
                binding.setHideTitle(true);
                binding.setHideBottomBar(true);
                break;
            }
            case R.id.homeFragment: {
                binding.setHideNavigationIcon(true);
                binding.setHideLogo(false);
                binding.setHideTitle(false);
                binding.setTitle(getString(R.string.app_name));
                binding.setHideBottomBar(false);
                focusOnSelectedNavItem(R.id.homeFragment);
                break;
            }
            case R.id.settingsFragment: {
                binding.setHideNavigationIcon(false);
                binding.setHideLogo(true);
                binding.setHideTitle(false);
                binding.setTitle(destination.getLabel().toString());
                binding.setHideBottomBar(false);
                focusOnSelectedNavItem(R.id.settingsFragment);
                break;
            }
            case R.id.findDevicesFragment: {
                binding.setHideNavigationIcon(false);
                binding.setHideLogo(true);
                binding.setHideTitle(false);
                binding.setTitle(destination.getLabel().toString());
                binding.setHideBottomBar(false);
                break;
            }

            default: {
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.speech_recognition: {
                navController.navigate(HomeFragmentDirections.Companion.actionGlobalSpeechRecognitionBottomSheetDialog());
                break;
            }
            case R.id.settings: {
                navController.navigate(HomeFragmentDirections.Companion.actionGlobalSettingsFragment());
                break;
            }
            case R.id.home: {
                navController.navigate(HomeFragmentDirections.Companion.actionGlobalHomeFragment(""));
                break;
            }
            default: {
            }
        }
    }

    private void focusOnSelectedNavItem(int selectedItemId) {
        if (R.id.homeFragment == selectedItemId) {
            binding.home.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_400)));
            binding.settings.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey)));
        } else if (R.id.settingsFragment == selectedItemId) {
            binding.home.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey)));
            binding.settings.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_400)));
        }
    }

    public void requestBluetoothPermission() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    public boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
    }
}
