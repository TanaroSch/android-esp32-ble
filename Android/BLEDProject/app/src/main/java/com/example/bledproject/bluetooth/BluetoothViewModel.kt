package com.example.bledproject.bluetooth


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import java.util.UUID


class BluetoothViewModel(
	myContext: Context,
	bluetoothManager: BluetoothManager,
	bluetoothAdapter: BluetoothAdapter
) {
	val devices = mutableStateListOf<BluetoothDevice>()

	val scanning = mutableStateOf(false)
	val context = myContext

	var connectedDevice = mutableStateOf("")

	// state of connection
	var connected = mutableStateOf(false)

	var thisGatt: BluetoothGatt? = null
	var writeCharacteristic: BluetoothGattCharacteristic? = null

	private val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
	private val scanCallback = object : ScanCallback() {
		override fun onScanResult(
			callbackType: Int,
			result: ScanResult
		) {
			val device = result.device
			// only add if not already in list
			if (!devices.contains(device)) devices.add(device)
		}
	}
	val gattObject = object : BluetoothGattCallback() {
		override fun onConnectionStateChange(
			gatt: BluetoothGatt,
			status: Int,
			newState: Int
		) {
			super.onConnectionStateChange(
				gatt,
				status,
				newState
			)
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				Log.d(
					"GattCallback",
					"Successfully connected to device"
				)
				connected.value = true
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
				Log.d(
					"GattCallback",
					"Successfully disconnected from device"
				)
				connected.value = false
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
				Log.d(
					"GattCallback",
					"Discovered Services"
				)
				thisGatt = gatt
				gatt?.services?.forEach { service ->
					Log.d(
						"GattCallback",
						"Service: ${service.uuid}"
					)
					service.characteristics.forEach { characteristic ->
						Log.d(
							"GattCallback",
							"Characteristic: ${characteristic.uuid}"
						)
						if (characteristic.uuid == UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")) {
							// Listen for changes on this characteristic
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
							gatt.setCharacteristicNotification(
								characteristic,
								true
							)
						}
						if (characteristic.uuid == UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E")) {
							Log.d(
								"GattCallback",
								"Found write characteristic"
							)
							writeCharacteristic = characteristic
						}
						characteristic.descriptors.forEach { descriptor ->
							Log.d(
								"GattCallback",
								"Descriptor: ${descriptor.uuid}"
							)
						}
					}
				}
			} else {
				Log.d(
					"GattCallback",
					"Failed to discover services"
				)
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
			Log.d(
				"GattCallback",
				"Characteristic changed"
			)
			if (characteristic.uuid == UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")) {
				// Characteristic changed
				val readValue = String(characteristic.value)
				Log.d(
					"BluetoothScreen",
					"Value: $readValue"
				)
				// TODO handle received data
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

	fun writeCharacteristic(message: String) {
		val characteristic = writeCharacteristic ?: return
		characteristic.setValue(message.toByteArray(Charsets.UTF_8))
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
		thisGatt?.writeCharacteristic(characteristic)
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
		connectedDevice.value = device.address
	}

	fun disconnect() {
		// disconnect from device
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
		thisGatt?.disconnect()
	}


}