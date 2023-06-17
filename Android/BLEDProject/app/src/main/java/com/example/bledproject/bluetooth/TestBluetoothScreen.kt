package com.example.bledproject.bluetooth

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat

@Composable
fun TestBluetoothScreen(bluetoothViewModel: BluetoothViewModel) {

	// show toast when connected
	LaunchedEffect(
		key1 = bluetoothViewModel.connected.value,
		block = {
			if (bluetoothViewModel.connected.value) {
				Toast.makeText(
					bluetoothViewModel.context,
					"Connected to ${bluetoothViewModel.connectedDevice.value}",
					Toast.LENGTH_SHORT
				)
					.show()
			} else {
				Toast.makeText(
					bluetoothViewModel.context,
					"Disconnected",
					Toast.LENGTH_SHORT
				)
					.show()
			}
		}
	)

	Column {
		Row() {
			Button(
				modifier = Modifier,
				onClick = {
					if (bluetoothViewModel.scanning.value) {
						bluetoothViewModel.stopScan()
					} else {
						bluetoothViewModel.startScan()
					}
				}) {
				if (bluetoothViewModel.scanning.value) {
					Text("Stop Scan")
				} else {
					Text("Start Scan")
				}
			}
			if (bluetoothViewModel.connected.value) {
				// disconnect button
				Button(
					modifier = Modifier.fillMaxWidth(),
					onClick = {
						bluetoothViewModel.disconnect()
					}) {
					Text("Disconnect")
				}
			}
		}


		LazyColumn(modifier = Modifier.weight(1f)) {
			bluetoothViewModel.devices.forEach() { device ->
				item {
					Row() {
						if (ActivityCompat.checkSelfPermission(
								bluetoothViewModel.context,
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
							return@item
						}
						Text(device.name ?: "Unnamed device")
						Text(
							modifier = Modifier.weight(1f),
							text = device.address
						)
						Button(onClick = {
							bluetoothViewModel.connectToDevice(device)
						}) {
							Text("Connect")
						}
					}

				}
			}
		}
		if (bluetoothViewModel.connected.value) {
			Row() {
				Text("Connected to: ")
				Text(text = bluetoothViewModel.connectedDevice.value)
			}
			Row() {
				Text("Read Characteristic: ")
				Text(text = bluetoothViewModel.receivedData.value)
				Button(onClick = {
					bluetoothViewModel.writeCharacteristic("Test send")
				}) {
					Text(text = "Write")
				}
			}

		}

	}
}