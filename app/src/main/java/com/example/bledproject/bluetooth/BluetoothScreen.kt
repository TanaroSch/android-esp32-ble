package com.example.bledproject.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
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
import java.util.UUID

@SuppressLint("MissingPermission")
@Composable
fun BluetoothScreen() {
	val devices = remember { mutableStateListOf<BluetoothDevice>() }
	val scanning = remember { mutableStateOf(false) }
	val context = LocalContext.current

	Column {
		Button(
			onClick = {
				scanning.value = !scanning.value
				if (scanning.value) {
					startScan(devices)
				} else {
					stopScan()
				}
			}
		) {
			Text(if (scanning.value) "Stop Scan" else "Start Scan")
		}

		LazyColumn {
			itemsIndexed(devices.toList()) { index, device ->
				Row() {
					Text(device.name ?: "Unnamed device")
					Text(device.address)
					Button(onClick = {
						// connect to ble device with
						val device = devices[index]
						val gatt = device.connectGatt(
							context,
							false,
							object : BluetoothGattCallback() {
								override fun onConnectionStateChange(
									gatt: BluetoothGatt?,
									status: Int,
									newState: Int
								) {
									super.onConnectionStateChange(
										gatt,
										status,
										newState
									)
									if (newState == BluetoothProfile.STATE_CONNECTED) {
										// Connected to device
										gatt?.discoverServices()
									} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
										// Disconnected from device
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
										val services = gatt?.services
										services?.forEach { service ->
											Log.d(
												"BluetoothScreen",
												"Service UUID: ${service.uuid}"
											)
											service.characteristics.forEach { characteristic ->
												Log.d(
													"BluetoothScreen",
													"Characteristic UUID: ${characteristic.uuid}"
												)
												if (characteristic.uuid == UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")) {
													// Listen to characteristic
													gatt.setCharacteristicNotification(
														characteristic,
														true
													)
													val descriptor =
															characteristic.getDescriptor(UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E"))
													descriptor.value =
															BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
													gatt.writeDescriptor(descriptor)
												} else if (characteristic.uuid == UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E")) {
													// Write to characteristic every 5 seconds
													val writeCharacteristic = characteristic
													val handler =
															android.os.Handler(Looper.getMainLooper())
													handler.postDelayed(
														object : Runnable {
															override fun run() {
																writeCharacteristic.setValue("hello world")
																gatt.writeCharacteristic(writeCharacteristic)
																handler.postDelayed(
																	this,
																	5000
																)
															}
														},
														5000
													)
												}
											}
										}
									}
								}

								override fun onCharacteristicChanged(
									gatt: BluetoothGatt?,
									characteristic: BluetoothGattCharacteristic?
								) {
									super.onCharacteristicChanged(
										gatt,
										characteristic
									)
									if (characteristic?.uuid == UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")) {
										// Characteristic changed
										val value = characteristic?.getStringValue(0)
										Log.d(
											"BluetoothScreen",
											"Value: $value"
										)
									}
								}
							})
					}) {
						Text(text = "Connect")
					}
				}
			}
		}
	}
}


@SuppressLint("MissingPermission")
private fun startScan(devices: MutableList<BluetoothDevice>) {
	val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
	val scanner = bluetoothAdapter.bluetoothLeScanner
	val settings = ScanSettings.Builder()
		.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
		.build()
	val filter = ScanFilter.Builder()
		.build()

	scanner.startScan(
		listOf(filter),
		settings,
		object : ScanCallback() {
			override fun onScanResult(
				callbackType: Int,
				result: ScanResult
			) {
				super.onScanResult(
					callbackType,
					result
				)
				if (!devices.contains(result.device))
					devices.add(result.device)
			}
		})
}

@SuppressLint("MissingPermission")
private fun stopScan() {
	val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
	val scanner = bluetoothAdapter.bluetoothLeScanner
	scanner.stopScan(
		object : ScanCallback() {}
	)
}
