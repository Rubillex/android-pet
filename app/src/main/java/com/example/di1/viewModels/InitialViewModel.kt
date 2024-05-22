package com.example.di1.viewModels

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class InitialViewModel(application: Application) : AndroidViewModel(application = application) {
    private val _context = getApplication<Application>().applicationContext

    private val _wifiManager: WifiManager by lazy {
        _context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    private val _uiState = MutableStateFlow<ApplicationState>(
        ApplicationState.Default)
    val uiState: StateFlow<ApplicationState> = _uiState

    private val _isProcessed = MutableStateFlow(false)
    val isProcessed: StateFlow<Boolean> = _isProcessed

    fun checkPermissions(ssid: String, password: String) {
        val fineLocationPermission = ContextCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission = ContextCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            coarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            connectToWifi(ssid = ssid, password = password)
            return
        }

        requestPermissions()
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(getApplication(), arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ), 1)
    }

    private fun connectToWifi(ssid: String, password: String) {
        val wifiConfig = WifiConfiguration().apply {
            SSID = "\"$ssid\""
            preSharedKey = "\"$password\""
        }

        val netId = _wifiManager.addNetwork(wifiConfig)
        if (netId != -1) {
            _wifiManager.disconnect()
            _wifiManager.enableNetwork(netId, true)
            _wifiManager.reconnect()
//            Toast.makeText(this, "Подключение к Wi-Fi", Toast.LENGTH_SHORT).show()
        } else {
//            Toast.makeText(this, "Не удалось добавить сеть", Toast.LENGTH_SHORT).show()
        }
    }

    sealed class ApplicationState {
        object Default : ApplicationState()
        object Processed : ApplicationState()
    }
}