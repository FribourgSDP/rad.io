package com.github.fribourgsdp.radio.mockimplementations

import android.app.Activity
import android.content.Context
import com.github.fribourgsdp.radio.game.prep.JoinWithQRCodeFragment
import com.github.fribourgsdp.radio.R
import eu.livotov.labs.android.camview.ScannerLiveView
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