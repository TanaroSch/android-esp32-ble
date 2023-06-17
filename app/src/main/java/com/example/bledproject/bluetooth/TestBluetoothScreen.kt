package com.example.bledproject.bluetooth

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.example.bledproject.MainActivity
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

@Composable
fun TestBluetoothScreen(bluetoothViewModel: BluetoothViewModel) {
	Column {
		Button(modifier = Modifier.fillMaxWidth(), onClick = { bluetoothViewModel.startScan() }) {
			Text("Start Scan")
		}

		LazyColumn (modifier = Modifier.weight(1f)) {
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
						Text(device.address)
						Button(onClick = {
							bluetoothViewModel.connectToDevice(device)
						}) {
							Text("Connect")
						}
					}

				}
			}
		}
		Row() {
			Text("Read Characteristic: ")
			Text(text = bluetoothViewModel.connected.value)
		}
	}
}