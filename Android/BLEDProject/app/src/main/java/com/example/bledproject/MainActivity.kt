package com.example.bledproject

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.core.app.ActivityCompat
import com.example.bledproject.bluetooth.BluetoothViewModel
import com.example.bledproject.bluetooth.TestBluetoothScreen
import com.example.bledproject.data.UserStore
import com.example.bledproject.ui.theme.BLEDProjectTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		// userStore for storing device address
		val userStore = UserStore(applicationContext)

		// setup bluetooth view model
		val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
		val bluetoothAdapter = bluetoothManager.adapter
		val bluetoothViewModel = BluetoothViewModel(
			application,
			bluetoothAdapter,
			userStore,
			::messageHandler
		)

		// auto connect to bluetooth device on start if it was connected before
		CoroutineScope(Dispatchers.IO).launch {
			if (userStore.getAccessToken(getString(R.string.bluetoothDeviceAddress)) != "") {
				println("Access token bluetooth" + userStore.getAccessToken(getString(R.string.bluetoothDeviceAddress)))
				// create BluetoothDevice from address

				val bluetoothDevice: BluetoothDevice? =
						bluetoothAdapter.getRemoteDevice(userStore.getAccessToken(getString(R.string.bluetoothDeviceAddress)))
				if (bluetoothDevice != null) {
					bluetoothViewModel.connectToDevice(bluetoothDevice)
				} else {
					println("Bluetooth device is null")
				}
			} else {
				println("Access token bluetooth is empty" + userStore.getAccessToken(getString(R.string.bluetoothDeviceAddress)))
			}
		}


		setContent {
			BLEDProjectTheme {

				if (ActivityCompat.checkSelfPermission(
						bluetoothViewModel.context,
						Manifest.permission.BLUETOOTH_CONNECT
					) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
						bluetoothViewModel.context,
						Manifest.permission.BLUETOOTH_SCAN
					) != PackageManager.PERMISSION_GRANTED
				) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
						ActivityCompat.requestPermissions(
							this as Activity,
							arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
							1
						)
					} else {
						Toast.makeText(
							this,
							"newer android version required",
							Toast.LENGTH_SHORT
						).show()
						Text(text = "newer android version required")
					}
				} else {
					TestBluetoothScreen(bluetoothViewModel)
				}


			}
		}
	}

	private fun messageHandler(message: String) {
		// ADD MESSAGE HANDLER HERE
		println("Message handler: " + message)
	}

	@Deprecated("Deprecated in Java")
	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(
			requestCode,
			permissions,
			grantResults
		)
		when (requestCode) {
			1 -> {
				if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// Permission granted
					// restart activity
					val intent = Intent(
						this,
						MainActivity::class.java
					)
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
					startActivity(intent)
					finish()
				} else {
					// Permission denied
					Toast.makeText(
						this,
						"Bluetooth Permission required",
						Toast.LENGTH_SHORT
					).show()
				}
				return
			}
		}
	}

}
