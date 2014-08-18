package com.example.test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;

public class BleUtils {
	public static final int STATUS_BLE_ENABLED = 0;
	public static final int STATUS_BLUETOOTH_NOT_AVAILABLE = 1;
	public static final int STATUS_BLE_NOT_AVAILABLE = 2;
	public static final int STATUS_BLUETOOTH_DISABLED = 3;
	
	public static BluetoothAdapter getBluetoothAdapter(Context context) {
		//Initializes a Bluetooth adapter. 
		final BluetoothManager bluetoothManager=
				(BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		if(bluetoothManager==null) 
			return null;
		return bluetoothManager.getAdapter();		
	}
	
	public static int getBleStatus(Context context) {
		//Use this check to determine whether BLE is supported on the device.
		//Then you selectively disable BLE-related features.
		if(!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			return STATUS_BLE_NOT_AVAILABLE;
		}
		
		final BluetoothAdapter adapter= getBluetoothAdapter(context);
		//Checks if Bluetooth is supported on the device.
		if(adapter==null) {
			return STATUS_BLUETOOTH_NOT_AVAILABLE;
		}
		
		if(adapter.isEnabled()) 
			return STATUS_BLUETOOTH_DISABLED;
		
		return STATUS_BLE_ENABLED;
		
	}
}
