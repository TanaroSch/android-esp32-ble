package com.example.bledproject

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

		val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
		val bluetoothAdapter = bluetoothManager.adapter
		val bluetoothViewModel = BluetoothViewModel(application, bluetoothManager, bluetoothAdapter)

		// auto connect to bluetooth device on start if it was connected before
		val userStore = UserStore(applicationContext)
		CoroutineScope(Dispatchers.IO).launch {
			if (userStore.getAccessToken(getString(R.string.bluetoothDeviceAddress)) != "") {
				println("Access token bluetooth" + userStore.getAccessToken(getString(R.string.bluetoothDeviceAddress)))
				// create BluetoothDevice from address

				val bluetoothDevice: BluetoothDevice? = bluetoothAdapter.getRemoteDevice(userStore.getAccessToken(getString(R.string.bluetoothDeviceAddress)))
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
				// A surface container using the 'background' color from the theme
				TestBluetoothScreen(bluetoothViewModel)
			}
		}
	}
}
