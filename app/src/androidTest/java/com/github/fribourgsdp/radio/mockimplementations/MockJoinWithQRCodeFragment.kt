package com.github.fribourgsdp.radio.mockimplementations

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.github.fribourgsdp.radio.JoinWithQRCodeFragment
import com.github.fribourgsdp.radio.R
import eu.livotov.labs.android.camview.ScannerLiveView
import io.agora.rtc.IRtcEngineEventHandler
import org.mockito.Mockito

class MockJoinWithQRCodeFragment(ctx: Context, activity: Activity) : JoinWithQRCodeFragment(ctx, activity) {


    private var join = false
    private var data  = "1001"
    fun setJoin(){
        join = true
    }
    fun setData(s : String){
        data = s
    }
    fun changeCancelButton(){

        if(join){
            cancelButton = rootView.findViewById(R.id.cancel_button)
            cancelButton.setOnClickListener{
                actOnScannedData(data)
                dismiss()
            }
        }
    }



    override fun initializeCamera(){
        // initialize scannerLiveview and textview.

        camera = makeMockScannerLiveView()
        changeCancelButton()
    }

    override fun requestPermission() {

    }

}

fun makeMockScannerLiveView(): ScannerLiveView{
    val scannerLiveView = Mockito.mock(ScannerLiveView::class.java)
    return scannerLiveView
}