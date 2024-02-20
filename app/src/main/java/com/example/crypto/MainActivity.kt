package com.example.crypto

import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.crypto.helpers.ForegroundService
import com.example.crypto.ui.theme.CryptoTheme


class MainActivity : ComponentActivity() {

    private val RECEIVE_SMS_PERMISSION_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("PERMISSION", (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS)
                == PackageManager.PERMISSION_GRANTED).toString()
        )
        Log.e("PERMISSION", (ContextCompat.checkSelfPermission(this, android.Manifest.permission.FOREGROUND_SERVICE)
                == PackageManager.PERMISSION_GRANTED).toString()
        )
        Log.e("PERMISSION", (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED).toString()
        )
        if (
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.FOREGROUND_SERVICE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
            Log.e("PERMISSION", "ВСЁ ОК =)")
            if (!foregroundServiceRunning()) {
                startService()
            }
        } else {
            Log.e("PERMISSION", "НУ ПОПРОСИ ЖЕ СУКААА")
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.FOREGROUND_SERVICE, android.Manifest.permission.POST_NOTIFICATIONS), RECEIVE_SMS_PERMISSION_CODE);
        }

        setContent {
            CryptoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECEIVE_SMS_PERMISSION_CODE) {
            Log.d("QQQ", grantResults.toString())
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!foregroundServiceRunning()) {
                    startService()
                }
            }
        }
    }

    private fun startService() {
        Log.e("BR", "startService")
        ForegroundService.startService(this, "test")
    }

    private fun foregroundServiceRunning(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (ForegroundService::class.java.getName() == service.service.className) {
                return true
            }
        }
        return false
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CryptoTheme {
        Greeting("Android")
    }
}