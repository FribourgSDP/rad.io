package com.github.fribourgsdp.radio.game.prep

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import eu.livotov.labs.android.camview.ScannerLiveView
import eu.livotov.labs.android.camview.scanner.decoder.zxing.ZXDecoder
import androidx.fragment.app.setFragmentResult
import com.github.fribourgsdp.radio.R

open class JoinWithQRCodeFragment(val ctx: Context, val activity: Activity): DialogFragment() {

    protected lateinit var rootView : View
    protected lateinit var camera: ScannerLiveView
    protected lateinit var cancelButton : Button

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {

        isCancelable = false

        rootView= inflater.inflate(R.layout.fragment_join_with_qr_code, container, false)
        // check permission method is to check that the
        // camera permission is granted by user or not.
        // request permission method is to request the
        // camera permission if not given.
        if (checkPermission()) {
            // if permission is already granted display a toast message
            Toast.makeText(ctx, "Permission Granted..", Toast.LENGTH_SHORT).show()
        } else {
            requestPermission()
        }
        cancelButton = rootView.findViewById(R.id.cancel_button)
        cancelButton.setOnClickListener{
            dismiss()
        }
        initializeCamera()

        return rootView
    }




    protected open fun initializeCamera(){
        // initialize scannerLiveview
        camera = rootView.findViewById<View>(R.id.camview) as ScannerLiveView
        camera.scannerViewEventListener = object : ScannerLiveView.ScannerViewEventListener {
            override fun onScannerStarted(scanner: ScannerLiveView) {
                // method is called when scanner is started
                Toast.makeText(ctx, "Scanner Started", Toast.LENGTH_SHORT).show()
            }

            override fun onScannerStopped(scanner: ScannerLiveView) {
                // method is called when scanner is stopped.
                Toast.makeText(ctx, "Scanner Stopped", Toast.LENGTH_SHORT).show()
            }

            override fun onScannerError(err: Throwable) {
                // method is called when scanner gives some error.
                Toast.makeText(ctx,"Scanner Error: " + err.message,Toast.LENGTH_SHORT).show()
            }

            override fun onCodeScanned(data: String) {
                // method is called when camera scans the
                // qr code and the data from qr code is
                // stored in data in string format.
                actOnScannedData(data)

            }
        }
    }

    protected fun actOnScannedData(data :String){
        if(checkScanFormat(data)){
            setFragmentResult("idRequest", bundleOf("id" to data.toLong()))
            dismiss()
        }
    }

    private fun checkScanFormat(data: String) : Boolean{
        return try{
            data.toLong()
            true
        }catch(e : NumberFormatException){
            Toast.makeText(ctx,"False format",Toast.LENGTH_SHORT).show()
            false
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
        val cameraPermission = ContextCompat.checkSelfPermission(ctx,
            Manifest.permission.CAMERA
        )
        val vibratePermission = ContextCompat.checkSelfPermission(ctx,
            Manifest.permission.VIBRATE
        )
        return cameraPermission == PackageManager.PERMISSION_GRANTED && vibratePermission == PackageManager.PERMISSION_GRANTED
    }


    protected open fun requestPermission() {
        // this method is to request
        // the runtime permission.
        val permissionRequestCode = 200
        ActivityCompat.requestPermissions(activity, arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.VIBRATE
        ), permissionRequestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // this method is called when user
        // allows the permission to use camera.
        if (grantResults.isNotEmpty()) {
            val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            val vibrateAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
            if (cameraAccepted && vibrateAccepted) {
                Toast.makeText(ctx, "Permission granted..", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(ctx,"Permission Denied \n You cannot use app without providing permission",Toast.LENGTH_SHORT).show()
            }
        }
    }
}


