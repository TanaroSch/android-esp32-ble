package com.example.bledproject.bluetooth


import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import com.example.bledproject.MainActivity
import hilt_aggregated_deps._dagger_hilt_android_internal_modules_ApplicationContextModule
import kotlinx.coroutines.currentCoroutineContext
import java.util.UUID


class BluetoothViewModel(myContext: Context) {
	val devices = mutableStateListOf<BluetoothDevice>()
	val scanning = mutableStateOf(false)
	var connected = mutableStateOf("")
	val context = myContext
	val bluetoothManager =
			myContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
	val bluetoothAdapter = bluetoothManager.adapter
	val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
	val scanCallback = object : ScanCallback() {
		override fun onScanResult(
			callbackType: Int,
			result: ScanResult
		) {
			val device = result.device
			//only add if not already in list
			if (!devices.contains(device))
				devices.add(device)
		}
	}
	val gattObject = object : BluetoothGattCallback() {
		override fun onConnectionStateChange(
			gatt: BluetoothGatt,
			status: Int,
			newState: Int
		) {
			super.onConnectionStateChange(gatt, status, newState)
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				Log.d("GattCallback", "Successfully connected to device")
				if (ActivityCompat.checkSelfPermission(
						context,
						Manifest.permission.BLUETOOTH_CONNECT
					) != PackageManager.PERMISSION_GRANTED
				) {
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
					return
				}
				gatt.discoverServices()
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				Log.d("GattCallback", "Successfully disconnected from device")
			}
		}

		override fun onServicesDiscovered(
			gatt: BluetoothGatt?,
			status: Int
		) {
			super.onServicesDiscovered(
				gatt,
				status
			)
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.d("GattCallback", "Discovered Services")
				gatt?.services?.forEach { service ->
					Log.d("GattCallback", "Service: ${service.uuid}")
					service.characteristics.forEach { characteristic ->
						Log.d("GattCallback", "Characteristic: ${characteristic.uuid}")
						characteristic.descriptors.forEach { descriptor ->
							Log.d("GattCallback", "Descriptor: ${descriptor.uuid}")
						}
					}
				}
			} else {
				Log.d("GattCallback", "Failed to discover services")
			}
		}

		override fun onCharacteristicChanged(
			gatt: BluetoothGatt,
			characteristic: BluetoothGattCharacteristic,
			value: ByteArray
		) {
			super.onCharacteristicChanged(
				gatt,
				characteristic,
				value
			)
			Log.d("GattCallback", "Characteristic changed")
			//TODO: change UUID
			if (characteristic.uuid == UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")) {
				Log.d("GattCallback", "Heart Rate Measurement: ${value[1]}")
			}
		}
	}

	fun startScan() {
		scanning.value = true
		val scanSettings = ScanSettings.Builder()
			.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
			.build()
		val scanFilter = ScanFilter.Builder()
			.build()
		val scanFilters = listOf(scanFilter)

		if (ActivityCompat.checkSelfPermission(
				context,
				Manifest.permission.BLUETOOTH_SCAN
			) != PackageManager.PERMISSION_GRANTED
		) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return
		}
		bluetoothLeScanner.startScan(
			scanFilters,
			scanSettings,
			scanCallback
		)

	}

	fun stopScan() {
		scanning.value = false
		if (ActivityCompat.checkSelfPermission(
				context,
				Manifest.permission.BLUETOOTH_SCAN
			) != PackageManager.PERMISSION_GRANTED
		) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return
		}
		bluetoothLeScanner.stopScan(scanCallback)
	}

	fun connectToDevice(device: BluetoothDevice) {
		if (ActivityCompat.checkSelfPermission(
				context,
				Manifest.permission.BLUETOOTH_CONNECT
			) != PackageManager.PERMISSION_GRANTED
		) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return
		}


		device.connectGatt(
			context,
			false,
			gattObject
		)
		connected.value = device.address
	}


}