package com.example.test;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class DeviceScanActivity extends Activity {

	private static GridviewAdapter mAdapter;

	private GridView gridView;

	private static final long SCAN_PERIOD = 500;

	private BluetoothAdapter bluetoothAdapter;
	private BleDevicesScanner scanner;

	static Context ctx;

	public static ImageView manImg;// 문제의 이미지객체

	FrameLayout fl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setTitle(R.string.title_devices);

		setContentView(R.layout.main);

		fl = (FrameLayout) findViewById(R.id.rootLayout);

		mAdapter = new GridviewAdapter(this);

		ctx = getApplicationContext();

		gridView = (GridView) findViewById(R.id.gridView);
		gridView.setAdapter(mAdapter);

		manImg = new ImageView(getBaseContext());
		manImg = (ImageView) fl.findViewById(R.id.manImg);
		manImg.setImageResource(R.drawable.man);

		final int bleStatus = BleUtils.getBleStatus(getBaseContext());
		switch (bleStatus) {
		case BleUtils.STATUS_BLE_NOT_AVAILABLE:
			ErrorDialog.newInstance(R.string.dialog_error_no_ble).show(
					getFragmentManager(), ErrorDialog.TAG);
			return;
		case BleUtils.STATUS_BLUETOOTH_NOT_AVAILABLE:
			ErrorDialog.newInstance(R.string.dialog_error_no_bluetooth).show(
					getFragmentManager(), ErrorDialog.TAG);
			return;
		default:
			bluetoothAdapter = BleUtils.getBluetoothAdapter(getBaseContext());
		}

		// initialized scanner
		scanner = new BleDevicesScanner(bluetoothAdapter,
				new BluetoothAdapter.LeScanCallback() {

					/*
					 * android.bluetooth.BluetoothAdapter.LeScanCallback
					 * :Callback interface used to deliver LE scan results.
					 */

					/*
					 * @see
					 * android.bluetooth.BluetoothAdapter.LeScanCallback#onLeScan
					 * (android.bluetooth.BluetoothDevice, int, byte[]) Callback
					 * reporting an LE device found during a device scan
					 * initiated by the
					 * startLeScan(BluetoothAdapter.LeScanCallback) function.
					 * 
					 * @param device Identifiers the remote device rssi The RSSI
					 * value for the remote device as reported by the Bluetooth
					 * hardware. 0 if no RSSI value is available. sacnRecord The
					 * content of the advertisement record offered by the remote
					 * device.
					 */
					@Override
					public void onLeScan(final BluetoothDevice device,
							final int rssi, byte[] scanRecord) {
						// TODO Auto-generated method stub
						mAdapter.addDevice(device, rssi);
						mAdapter.notifyDataSetChanged();
					}
				});
		scanner.setScanPeriod(SCAN_PERIOD);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.gatt_scan, menu);
		if (!AppConfig.DEBUG)
			menu.findItem(R.id.menu_demo).setVisible(false);
		if (scanner == null || !scanner.isScanning()) {
			menu.findItem(R.id.menu_stop).setVisible(false);
			menu.findItem(R.id.menu_scan).setVisible(true);
			menu.findItem(R.id.menu_refresh).setActionView(null);
		} else {
			menu.findItem(R.id.menu_stop).setVisible(true);
			menu.findItem(R.id.menu_scan).setVisible(false);
			menu.findItem(R.id.menu_refresh).setActionView(
					R.layout.ab_indeterminate_progress);
		}
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_scan:
			mAdapter.clear();
			if (scanner != null)
				scanner.start();
			invalidateOptionsMenu();
			break;
		case R.id.menu_stop:
			if (scanner != null)
				scanner.stop();
			invalidateOptionsMenu();
			break;
		case R.id.menu_demo:
			// create other activity, if needed
			break;
		}
		return true;
	}

	protected void onResume() {
		super.onResume();

		if (bluetoothAdapter == null)
			return;

		// Ensures Bluetooth is enables on the device.
		// If BLuetooth is not currently enabled, fire an intent to display a
		// dialog
		// asking the user to grant permission to enable it.
		if (!bluetoothAdapter.isEnabled()) {
			final Fragment f = getFragmentManager().findFragmentByTag(
					EnableBluetoothDialog.TAG);
			if (f == null)
				new EnableBluetoothDialog().show(getFragmentManager(),
						EnableBluetoothDialog.TAG);
			return;
		}
		init();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (scanner != null) {
			scanner.stop();
		}
	}

	private void init() {
		if (mAdapter == null) {
			mAdapter = new GridviewAdapter(getBaseContext());
			((GridView) gridView).setAdapter(mAdapter);
		}

		scanner.start();
		invalidateOptionsMenu();
	}

	public void onEnableBluetooth(EnableBluetoothDialog f) {
		bluetoothAdapter.enable();
		init();
	}

	public void onCancel(EnableBluetoothDialog f) {
		finish();
	}

	public void onDismiss(ErrorDialog f) {
		finish();
	}

	/*
	 * public static void moveMan(String deviceName, int rssiPercent) {
	 * 
	 * String rssi = String.valueOf(rssiPercent); Log.d("rssiPercent", rssi);
	 * 
	 * 
	 * TranslateAnimation ani = new TranslateAnimation(
	 * Animation.RELATIVE_TO_SELF, fromX, Animation.RELATIVE_TO_SELF, -3.0f,
	 * Animation.RELATIVE_TO_SELF, fromY, Animation.RELATIVE_TO_SELF, -4.0f);
	 * ani.setDuration(3000);
	 * 
	 * 
	 * Thread thread = new Thread(); if (rssiPercent >= 33 && rssiPercent <= 36
	 * && image != null) { //manImg.startAnimation(ani); thread.start(); }
	 * 
	 * else Log.d("warning", "manImg==null"); }
	 */

	/*
	 * public static class MyAsyncTask extends AsyncTask {
	 * 
	 * @Override protected Object doInBackground(Object... params) { // TODO
	 * Auto-generated method stub return null; }
	 * 
	 * protected void onPostExecute(ImageView image) { // update the UI with
	 * loaded information // doInBackground에서드의 작업 결과를 UI반영하는 역할을 담당하는 메소드 image
	 * = manImg;
	 * 
	 * 
	 * }
	 * 
	 * 
	 * 
	 * }
	 */

	public static void moveImage(int i) {
		switch (i) {
		case 0:
			Animation upLeft = AnimationUtils.loadAnimation(ctx,
					R.anim.slide_to_0);
			upLeft.setFillAfter(true);
			manImg.startAnimation(upLeft);
			break;

		case 1:
			Animation upRight = AnimationUtils.loadAnimation(ctx,
					R.anim.slide_to_1);
			upRight.setFillAfter(true);
			manImg.startAnimation(upRight);
			break;

		case 2:
			Animation downLeft = AnimationUtils.loadAnimation(ctx,
					R.anim.slide_to_2);
			downLeft.setFillAfter(true);
			manImg.startAnimation(downLeft);
			break;
		case 3:
			Animation downRight = AnimationUtils.loadAnimation(ctx,
					R.anim.slide_to_3);
			downRight.setFillAfter(true);
			manImg.startAnimation(downRight);
			break;
		}
	}
	/*
	 * public static void findPostitionOfMaxRssi(String maxRssiAddress) { int i;
	 * Animation left = AnimationUtils.loadAnimation(ctx, R.anim.slide_to_0);
	 * Animation right = AnimationUtils.loadAnimation(ctx, R.anim.slide_to_1);
	 * 
	 * for(i=0;i<3;i++) if (maxRssiAddress ==
	 * mAdapter.getDevice(i).getAddress()) { if (i == 1) {
	 * left.setFillAfter(true); manImg.startAnimation(left);
	 * 
	 * } else if (i == 2) { right.setFillAfter(true);
	 * manImg.startAnimation(right);
	 * 
	 * } } Log.d("strongestRssiPosition",String.valueOf(i)); }
	 */

	/*
	 * public static void getRssiOfDevice(String deviceName, int rssiPercent) {
	 * // String device=deviceName; // String rssi=String.valueOf(rssiPercent);
	 * 
	 * // Log.d("DeviceName", device); // Log.d("Rssi", rssi);
	 * 
	 * new curDevice(); new curDevice();
	 * 
	 * String device1rssi = String.valueOf(curDevice.rssi); String device2rssi =
	 * String.valueOf(curDevice.rssi);
	 * 
	 * Log.d("device1", device1rssi); Log.d("device2", device2rssi);
	 * 
	 * TranslateAnimation ani1 = new TranslateAnimation(
	 * Animation.RELATIVE_TO_SELF, device1X, Animation.RELATIVE_TO_SELF, -3.0f,
	 * Animation.RELATIVE_TO_SELF, device1Y, Animation.RELATIVE_TO_SELF, -4.0f);
	 * ani1.setDuration(1000);
	 * 
	 * TranslateAnimation ani2 = new TranslateAnimation(
	 * Animation.RELATIVE_TO_SELF, device2X, Animation.RELATIVE_TO_SELF, 3.0f,
	 * Animation.RELATIVE_TO_SELF, device2Y, Animation.RELATIVE_TO_SELF, -4.0f);
	 * ani2.setDuration(1000);
	 * 
	 * if (curDevice.rssi > curDevice.rssi && manImg != null) {
	 * manImg.startAnimation(ani1);
	 * 
	 * }
	 * 
	 * else if (curDevice.rssi < curDevice.rssi && manImg != null) {
	 * manImg.startAnimation(ani2); }
	 * 
	 * else if (manImg == null) Log.d("warning", "manImg==null");
	 * 
	 * }
	 * 
	 * public static class curDevice {
	 * 
	 * public static int rssi;
	 * 
	 * public static String name;
	 * 
	 * public int getRssi() { return curDevice.rssi; }
	 * 
	 * public void setRssi(int rssiPercent) { curDevice.rssi = rssiPercent; }
	 * 
	 * public String getName() { return curDevice.name; }
	 * 
	 * public void setName(String deviceName) { curDevice.name = deviceName; }
	 * 
	 * }
	 */
}
