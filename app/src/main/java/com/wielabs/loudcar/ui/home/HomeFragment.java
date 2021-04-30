package com.wielabs.loudcar.ui.home;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.method.TransformationMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavArgs;

import com.wielabs.loudcar.R;
import com.wielabs.loudcar.databinding.FragmentHomeBinding;
import com.wielabs.loudcar.ui.MainActivity;
import com.wielabs.loudcar.ui.MainActivityViewModel;
import com.wielabs.loudcar.util.SharedPreferencesUtil;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements View.OnClickListener, LedTextAdapter.LedTextAdapterListener {
    private FragmentHomeBinding binding;
    private LedTextAdapter ledTextAdapter;
    private String queryText;

    private final BroadcastReceiver bluetoothStateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                binding.bluetoothToggle.setChecked(bluetoothState == BluetoothAdapter.STATE_ON);
            }
        }
    };

    @Inject
    MainActivityViewModel mainActivityViewModel;

    @Inject
    SharedPreferencesUtil sharedPreferencesUtil;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        queryText = HomeFragmentArgs.fromBundle(getArguments()).getText();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (sharedPreferencesUtil.isFirstLaunch())
            ((MainActivity) requireActivity()).navController.navigate(HomeFragmentDirections.Companion.actionHomeFragmentToBaseIntroFragment());

        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (!mainActivityViewModel.isBluetoothEnabled())
            ((MainActivity) requireActivity()).requestBluetoothPermission();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().registerReceiver(bluetoothStateChangeReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        if (queryText != null && !queryText.isEmpty())
            binding.ledTv.setText(queryText);

        binding.directTopIv.setOnClickListener(this);
        binding.directLeftIv.setOnClickListener(this);
        binding.directBottomIv.setOnClickListener(this);
        binding.directRightIv.setOnClickListener(this);
        binding.newLedTextFab.setOnClickListener(this);
        binding.findDevicesButton.setOnClickListener(this);
        binding.bluetoothToggle.setOnClickListener(this);

        // Display UI according to user preferences.
        int direction = sharedPreferencesUtil.getDirectionSelection();
        focusOnSelectedDirection(direction == 1 ? R.id.direct_left_iv : (direction == 2 ? R.id.direct_right_iv : (direction == 3 ? R.id.direct_top_iv : R.id.direct_bottom_iv)));

        binding.reverseTextSwitch.setChecked(sharedPreferencesUtil.getReverseTextToggle());
        binding.speedSlider.setValue(sharedPreferencesUtil.getSpeedSelection());

        binding.reverseTextSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferencesUtil.setReverseText(isChecked);
            mainActivityViewModel.isReverseLedText = isChecked;

            if (isChecked || mainActivityViewModel.isReverseLedText) {
                //binding.ledTv.setScaleX(-1);
                binding.ledTv.setRotationX(180);
                binding.ledTv.setRotationY(180);
            }
                //binding.ledTv.setText(mainActivityViewModel.getReverseString(binding.ledTv.getText().toString()));
            else
                binding.ledTv.setRotationY(0);
                binding.ledTv.setRotationX(0);
        });

        binding.speedSlider.addOnChangeListener((slider, value, fromUser) -> sharedPreferencesUtil.setSpeed((int) value));
        binding.increaseSpeedIv.setOnClickListener(v -> {
            int currentSpeedValue = (int) binding.speedSlider.getValue();
            binding.speedSlider.setValue(currentSpeedValue < 7 ? currentSpeedValue + 1 : currentSpeedValue);
        });

        binding.decreaseSpeedIv.setOnClickListener(v -> {
            int currentSpeedValue = (int) binding.speedSlider.getValue();
            binding.speedSlider.setValue(currentSpeedValue > 1 ? currentSpeedValue - 1 : currentSpeedValue);
        });

        ledTextAdapter = new LedTextAdapter(new LedTextAdapter.LedTextDiffUtil(), this);
        binding.previewEditTextRv.setAdapter(ledTextAdapter);

        // Populate an empty LedText if empty.
        if (mainActivityViewModel.getPreviewTexts().isEmpty())
            mainActivityViewModel.addAndSubmitNewLedText();
        ledTextAdapter.submitList(new ArrayList<>(mainActivityViewModel.getPreviewTexts()));

        // Setup fontsSpinner.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.fonts_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.fontsSpinner.setAdapter(adapter);
    }

    private void focusOnSelectedDirection(int selectedDirectionId) {
        if (selectedDirectionId == R.id.direct_bottom_iv) {
            binding.directBottomIv.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_400));
            binding.directLeftIv.setBackgroundColor(getAttrForResourceId(android.R.attr.colorControlHighlight));
            binding.directRightIv.setBackgroundColor(getAttrForResourceId(android.R.attr.colorControlHighlight));
            binding.directTopIv.setBackgroundColor(getAttrForResourceId(android.R.attr.colorControlHighlight));
        } else if (selectedDirectionId == R.id.direct_right_iv) {
            binding.directBottomIv.setBackgroundColor(getAttrForResourceId(android.R.attr.colorControlHighlight));
            binding.directLeftIv.setBackgroundColor(getAttrForResourceId(android.R.attr.colorControlHighlight));
            binding.directRightIv.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_400));
            binding.directTopIv.setBackgroundColor(getAttrForResourceId(android.R.attr.colorControlHighlight));
        } else if (selectedDirectionId == R.id.direct_top_iv) {
            binding.directBottomIv.setBackgroundColor(getAttrForResourceId(android.R.attr.colorControlHighlight));
            binding.directLeftIv.setBackgroundColor(getAttrForResourceId(android.R.attr.colorControlHighlight));
            binding.directRightIv.setBackgroundColor(getAttrForResourceId(android.R.attr.colorControlHighlight));
            binding.directTopIv.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_400));
        } else {
            binding.directBottomIv.setBackgroundColor(getAttrForResourceId(android.R.attr.colorControlHighlight));
            binding.directLeftIv.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_400));
            binding.directRightIv.setBackgroundColor(getAttrForResourceId(android.R.attr.colorControlHighlight));
            binding.directTopIv.setBackgroundColor(getAttrForResourceId(android.R.attr.colorControlHighlight));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.direct_bottom_iv:
            case R.id.direct_right_iv:
            case R.id.direct_top_iv:
            case R.id.direct_left_iv: {
                int viewId = v.getId();
                sharedPreferencesUtil.setDirection(viewId == R.id.direct_bottom_iv ? 4 : (viewId == R.id.direct_top_iv ? 3 : (viewId == R.id.direct_right_iv ? 2 : 1)));
                focusOnSelectedDirection(viewId);
                break;
            }
            case R.id.new_led_text_fab: {
                if (mainActivityViewModel.getPreviewTexts().size() < 8) {
                    mainActivityViewModel.addAndSubmitNewLedText();

                    // Submit list.
                    ledTextAdapter.submitList(new ArrayList<>(mainActivityViewModel.getPreviewTexts()));
                } else
                    Toast.makeText(requireContext(), getString(R.string.max_led_texts_toast_msg), Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.find_devices_button: {
                if (mainActivityViewModel.isBluetoothEnabled())
                    if (((MainActivity) requireActivity()).isLocationPermissionGranted())
                        ((MainActivity) requireActivity()).navController.navigate(HomeFragmentDirections.Companion.actionHomeFragmentToFindDevicesFragment());
                    else
                        ((MainActivity) requireActivity()).requestLocationPermission();
                else
                    ((MainActivity) requireActivity()).requestBluetoothPermission();

            }
            case R.id.bluetooth_toggle: {
                mainActivityViewModel.toggleBluetooth();
            }
            default: {
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == MainActivity.REQUEST_LOCATION)
            ((MainActivity) requireActivity()).navController.navigate(HomeFragmentDirections.Companion.actionHomeFragmentToFindDevicesFragment());

    }

    private int getAttrForResourceId(int resourceId) {
        TypedValue outValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(resourceId, outValue, true);
        return outValue.data;
    }

    @Override
    public void onRemoveLedText(int position) {
        mainActivityViewModel.removeAndSubmitLedTexts(position);

        binding.ledTv.setText(getString(R.string.preview));

        // Submit list.
        ledTextAdapter.submitList(new ArrayList<>(mainActivityViewModel.getPreviewTexts()));
    }

    @Override
    public void onLedTextChanged(String previewText) {
        if (!mainActivityViewModel.isReverseLedText) {
            binding.ledTv.setText(previewText);
            binding.ledTv.setRotationY(0);
            binding.ledTv.setRotationX(0);
        }
        else {
            binding.ledTv.setText(previewText);
            binding.ledTv.setRotationY(180);
            binding.ledTv.setRotationX(0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        requireActivity().unregisterReceiver(bluetoothStateChangeReceiver);
    }
}
