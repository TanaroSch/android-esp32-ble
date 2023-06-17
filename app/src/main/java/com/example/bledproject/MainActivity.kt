package com.example.bledproject

import android.bluetooth.BluetoothManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.bledproject.bluetooth.BluetoothScreen
import com.example.bledproject.bluetooth.BluetoothViewModel
import com.example.bledproject.bluetooth.TestBluetoothScreen
import com.example.bledproject.ui.theme.BLEDProjectTheme
import com.juul.kable.Scanner
import com.juul.kable.logs.Logging
import com.juul.kable.logs.SystemLogEngine
import kotlinx.coroutines.flow.toList

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val bluetoothViewModel = BluetoothViewModel(application)

		setContent {
			BLEDProjectTheme {
				// A surface container using the 'background' color from the theme
				TestBluetoothScreen(bluetoothViewModel)
			}
		}
	}
}
