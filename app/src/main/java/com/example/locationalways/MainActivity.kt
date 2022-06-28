package com.example.locationalways

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity(), LocationListener {
    private var locationManager: LocationManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions(PERMISSIONS, PERMISSION_ALL)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                requestLocation()
                handler.postDelayed(this, HANDLER_DELAY.toLong())
            }
        }, START_HANDLER_DELAY.toLong())

    }

    override fun onLocationChanged(location: Location) {
        Log.d("mylog", "Got Location: " + location.latitude + ", " + location.longitude)
        Toast.makeText(
            this@MainActivity,
            "Got Coordinates: " + location.latitude + ", " + location.longitude,
            Toast.LENGTH_SHORT
        ).show()
        locationManager!!.removeUpdates(this)
    }

    private fun requestLocation() {
        if (locationManager == null) locationManager =
            getSystemService(LOCATION_SERVICE) as LocationManager
        if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                locationManager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    GPS_TIME_INTERVAL.toLong(), GPS_DISTANCE.toFloat(), this
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    requestLocation()
                    handler.postDelayed(this, HANDLER_DELAY.toLong())
                }
            }, START_HANDLER_DELAY.toLong())
        } else {
            finish()
        }
    }

    companion object {
        private const val GPS_TIME_INTERVAL = 1000 * 60 * 5 // get gps location every 1 min
        private const val GPS_DISTANCE = 1000 // set the distance value in meter
        private const val HANDLER_DELAY = 1000 * 5
        private const val START_HANDLER_DELAY = 0
        val PERMISSIONS = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,

            )
        }
        const val PERMISSION_ALL = 1
    }
}

