package com.wielabs.loudcar.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wielabs.loudcar.model.LedText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MainActivityViewModel extends ViewModel {
    boolean mScanning = false;
    public boolean isReverseLedText = false;
    private static final long SCAN_PERIOD = 10000;
    private Handler handler;
    public BluetoothAdapter bluetoothAdapter;
    BluetoothLeScanner bluetoothLeScanner;
    Set<String> leDeviceAddresses = new HashSet();
    private final ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    BluetoothDevice device = result.getDevice();
                    if (!leDeviceAddresses.contains(device.getAddress())) {
                        leDeviceAddresses.add(device.getAddress());
                        mLeDevices.add(device);
                        leDevices.setValue(mLeDevices);
                    }
                }
            };
    private final ArrayList<BluetoothDevice> mLeDevices = new ArrayList();
    private final ArrayList<LedText> previewTexts = new ArrayList<LedText>();
    private final MutableLiveData<ArrayList<BluetoothDevice>> leDevices = new MutableLiveData<>(new ArrayList<>());

    @Inject
    MainActivityViewModel() {
        super();
    }

    private final MutableLiveData<Integer> introPosition = new MutableLiveData(-1);

    public LiveData<Integer> getIntroPosition() {
        return introPosition;
    }

    public void setIntroPosition(int newPosition) {
        introPosition.setValue(newPosition);
    }

    public ScanCallback getLeScanCallback() {
        return leScanCallback;
    }

    public LiveData<ArrayList<BluetoothDevice>> getLeDevices() {
        return leDevices;
    }

    public ArrayList<LedText> getPreviewTexts() {
        return previewTexts;
    }

    public boolean isBluetoothEnabled() {
        if (bluetoothAdapter == null)
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public void toggleBluetooth() {
        if (bluetoothAdapter == null)
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
            return;
        if (!bluetoothAdapter.isEnabled())
            bluetoothAdapter.enable();
        else
            bluetoothAdapter.disable();
    }

    public void scanLeDevices() {
        // Perform some null checks.
        if (bluetoothAdapter == null)
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothLeScanner == null)
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        if (handler == null)
            handler = new Handler();

        if (!mScanning) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(() -> {
                mScanning = false;
                bluetoothLeScanner.stopScan(getLeScanCallback());
            }, SCAN_PERIOD);

            // Clear previously scanned devices if any.
            mLeDevices.clear();
            leDeviceAddresses.clear();
            mScanning = true;
            bluetoothLeScanner.startScan(getLeScanCallback());
        } else {
            mScanning = false;
            bluetoothLeScanner.stopScan(getLeScanCallback());

            // Restart scan.
            scanLeDevices();
        }
    }

    public void addAndSubmitNewLedText() {
        previewTexts.add(LedText.getDefaultLedTextForId(previewTexts.size() + 1));
    }

    public void removeAndSubmitLedTexts(int position) {
        previewTexts.remove(position);

        // Rearrange ids of LedText from 1 - 8 again.
        for (int i = 0; i < previewTexts.size(); i++) {
            if (previewTexts.get(i).getId() != i + 1) {
                LedText ledText = previewTexts.get(i);
                ledText.setId(i + 1);
                previewTexts.set(i, ledText);
            }
        }
    }

    public String getReverseString(String text) {
        StringBuilder previewTextBuilder = new StringBuilder();
        previewTextBuilder.append(text);
        return previewTextBuilder.reverse().toString();
    }
}
