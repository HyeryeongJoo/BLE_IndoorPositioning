package com.example.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridviewAdapter extends BaseAdapter {

	private final LayoutInflater inflater;

	List<Integer> rssiList;

	int maxRssiValue;
	BluetoothDevice maxRssiValueDevice;

	private final ArrayList<BluetoothDevice> leDevices;
	private final HashMap<BluetoothDevice, Integer> rssiMap = new HashMap<BluetoothDevice, Integer>();

	private Map<BluetoothDevice, Integer> sorted = new HashMap<BluetoothDevice, Integer>();

	public GridviewAdapter(Context context) {
		leDevices = new ArrayList<BluetoothDevice>();
		inflater = LayoutInflater.from(context);
		rssiList = new ArrayList<Integer>(rssiMap.values());
	}

	public void addDevice(BluetoothDevice device, int rssi) {
		if (!leDevices.contains(device)) {
			leDevices.add(device);
		}
		rssiMap.put(device, rssi);
	}

	public BluetoothDevice getDevice(int position) {
		return leDevices.get(position);
	}

	public void clear() {
		leDevices.clear();
	}

	public int giveRssi(int i) {
		BluetoothDevice device = leDevices.get(i);
		return rssiMap.get(device);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return leDevices.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return leDevices.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/*
	 * public static BluetoothDevice
	 * getMaxRssiDeviceAddress(Map<BluetoothDevice,Integer> map) {
	 * BluetoothDevice keyOfMaxValue= Collections.max(map.entrySet(), new
	 * Comparator<Entry<BluetoothDevice,Integer>>() { public int
	 * compare(Entry<BluetoothDevice,Integer> m1,Entry<BluetoothDevice,Integer>
	 * m2 ) { return m1.getValue()>m2.getValue() ? 1: -1; } }).getKey(); return
	 * keyOfMaxValue;
	 * 
	 * }
	 */

	public static BluetoothDevice getMaxRssiDeviceAddress(
			Map<BluetoothDevice, Integer> map) {
		Comparator<Map.Entry<BluetoothDevice, Integer>> comparator = new Comparator<Map.Entry<BluetoothDevice, Integer>>() {

			@Override
			public int compare(Entry<BluetoothDevice, Integer> lhs,
					Entry<BluetoothDevice, Integer> rhs) {
				// TODO Auto-generated method stub
				return lhs.getValue().compareTo(rhs.getValue());
			}

		};
		return Collections.max(map.entrySet(), comparator).getKey();
	}

	/*
	 * public static Integer sortByValues(Map<BluetoothDevice,Integer> map) {
	 * 
	 * int maxRssi; BluetoothDevice maxRssiDevice;
	 * 
	 * List<Map.Entry<BluetoothDevice, Integer>> entries= new
	 * LinkedList<Map.Entry<BluetoothDevice,Integer>> (map.entrySet());
	 * 
	 * Collections.sort(entries, new
	 * Comparator<Map.Entry<BluetoothDevice,Integer>>() {
	 * 
	 * 
	 * @Override public int compare(Entry<BluetoothDevice, Integer> lhs,
	 * Entry<BluetoothDevice, Integer> rhs) { // TODO Auto-generated method stub
	 * return lhs.getValue().compareTo(rhs.getValue()); }
	 * 
	 * });
	 * 
	 * //LinkedHashMap will keep the keys in the order they are inserted //which
	 * is currently sorted on natural ordering Map<BluetoothDevice, Integer>
	 * sortedMap=new LinkedHashMap<BluetoothDevice, Integer>();
	 * 
	 * for(Map.Entry<BluetoothDevice, Integer> entry:entries) {
	 * sortedMap.put(entry.getKey(), entry.getValue()); }
	 * 
	 * 
	 * 
	 * Collection<Integer> c=sortedMap.values(); maxRssi=Collections.max(c);
	 * maxRssiDevice=
	 * 
	 * return maxRssi;
	 * 
	 * }
	 */

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;

		BluetoothDevice maxRssiDevice;

		if (view == null) {
			view = inflater.inflate(R.layout.gridview_row, null);
			viewHolder = new ViewHolder();
			viewHolder.rssiImg = (ImageView) view.findViewById(R.id.imageView);
			viewHolder.deviceName = (TextView) view
					.findViewById(R.id.device_name);
			viewHolder.deviceAddress = (TextView) view
					.findViewById(R.id.device_address);
			viewHolder.deviceRssi = (TextView) view
					.findViewById(R.id.device_rssi);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		BluetoothDevice device = leDevices.get(i);

		final String deviceName = device.getName();
		if (deviceName != null && deviceName.length() > 0)
			viewHolder.deviceName.setText(deviceName);
		else
			viewHolder.deviceName.setText(R.string.unknown_device);

		viewHolder.deviceAddress.setText(device.getAddress());
		viewHolder.deviceRssi.setText("" + rssiMap.get(device) + " dBm");
		Log.d("position: ", String.valueOf(i));
		Log.d("rssi: ", String.valueOf(rssiMap.get(device)));

		final int rssiPercent = (int) (100.0f * (127.0f + rssiMap.get(device)) / (127.0f + 20.0f));
		viewHolder.rssiImg.setImageLevel(rssiPercent);

		maxRssiDevice = getMaxRssiDeviceAddress(rssiMap);
		Log.d("Max rssi device address", maxRssiDevice.toString());

		if (device.getAddress() == maxRssiDevice.toString()) {
			Log.d("Max rssi position", String.valueOf(i));
			DeviceScanActivity.moveImage(i);
		}

		// strongestRssiPosition=DeviceScanActivity.moveManToStrongestRssi(device);
		/*
		 * List<BluetoothDevice> keys= new
		 * ArrayList<BluetoothDevice>(sorted.keySet()); Object maxRssiKey=
		 * keys.get(j);
		 * strongestRssiPosition=DeviceScanActivity.moveManToStrongestRssi
		 * (maxRssiKey); Log.d("Max rssi device address",maxRssiKey.toString());
		 */
		// Log.d("strongestRssiPosition",String.valueOf(strongestRssiPosition));
		// MyAsyncTask.execute(strongestRssiPosition);

		// DeviceScanActivity.moveImage(strongestRssiPosition);
		return view;
	};

	public static class ViewHolder {
		public ImageView rssiImg;

		public TextView deviceName;
		public TextView deviceAddress;
		public TextView deviceRssi;
	}

}
