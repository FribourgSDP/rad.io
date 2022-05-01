package com.github.fribourgsdp.radio

import android.Manifest.permission
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import eu.livotov.labs.android.camview.ScannerLiveView
import eu.livotov.labs.android.camview.scanner.decoder.zxing.ZXDecoder
import android.Manifest.permission.CAMERA
import android.Manifest.permission.VIBRATE
import android.Manifest.permission.VIBRATE


import android.view.View
import eu.livotov.labs.android.camview.ScannerLiveView.ScannerViewEventListener


class TestQrCodeRead : AppCompatActivity() {
    private lateinit var camera: ScannerLiveView
    private lateinit var scannedTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_qr_code_read)

        // check permission method is to check that the
        // camera permission is granted by user or not.
        // request permission method is to request the
        // camera permission if not given.
        if (checkPermission()) {
            // if permission is already granted display a toast message
            Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show()
        } else {
            requestPermission()
        }

        // initialize scannerLiveview and textview.
        scannedTV = findViewById(R.id.idTVscanned)
        camera = findViewById<View>(R.id.camview) as ScannerLiveView
        camera.scannerViewEventListener = object : ScannerViewEventListener {
            override fun onScannerStarted(scanner: ScannerLiveView) {
                // method is called when scanner is started
                Toast.makeText(this@TestQrCodeRead, "Scanner Started", Toast.LENGTH_SHORT).show()
            }

            override fun onScannerStopped(scanner: ScannerLiveView) {
                // method is called when scanner is stopped.
                Toast.makeText(this@TestQrCodeRead, "Scanner Stopped", Toast.LENGTH_SHORT).show()
            }

            override fun onScannerError(err: Throwable) {
                // method is called when scanner gives some error.
                Toast.makeText(
                    this@TestQrCodeRead,
                    "Scanner Error: " + err.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onCodeScanned(data: String) {
                // method is called when camera scans the
                // qr code and the data from qr code is
                // stored in data in string format.
                scannedTV.setText(data)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val decoder = ZXDecoder()
        // 0.5 is the area where we have
        // to place red marker for scanning.
        decoder.scanAreaPercent = 0.8
        // below method will set secoder to camera.
        camera.decoder = decoder
        camera.startScanner()
    }

    override fun onPause() {
        // on app pause the
        // camera will stop scanning.
        camera.stopScanner()
        super.onPause()
    }

    private fun checkPermission(): Boolean {
        // here we are checking two permission that is vibrate
        // and camera which is granted by user and not.
        // if permission is granted then we are returning
        // true otherwise false.
        val camera_permission = ContextCompat.checkSelfPermission(applicationContext, CAMERA)
        val vibrate_permission = ContextCompat.checkSelfPermission(applicationContext, VIBRATE)
        return camera_permission == PackageManager.PERMISSION_GRANTED && vibrate_permission == PackageManager.PERMISSION_GRANTED
    }


    private fun requestPermission() {
        // this method is to request
        // the runtime permission.
        val PERMISSION_REQUEST_CODE = 200
        ActivityCompat.requestPermissions(this, arrayOf(CAMERA, VIBRATE), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // this method is called when user
        // allows the permission to use camera.
        if (grantResults.size > 0) {
            val cameraaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            val vibrateaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
            if (cameraaccepted && vibrateaccepted) {
                Toast.makeText(this, "Permission granted..", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Permission Denined \n You cannot use app without providing permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}