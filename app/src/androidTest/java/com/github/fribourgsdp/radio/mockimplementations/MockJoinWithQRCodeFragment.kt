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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView= inflater.inflate(R.layout.fragment_join_with_qr_code, container, false)

        cancelButton  = rootView.findViewById(R.id.cancel_button)
        cancelButton.setOnClickListener{
            //setFragmentResult("idRequest", bundleOf("id" to 1001L))
            dismiss()
        }


        initializeView()


        return rootView
    }

    override fun initializeView(){
        // initialize scannerLiveview and textview.
        scannedTV = rootView.findViewById(R.id.idTVscanned)
        camera = makeMockScannerLiveView()
            }

}

fun makeMockScannerLiveView(): ScannerLiveView{
    val scannerLiveView = Mockito.mock(ScannerLiveView::class.java)
    return scannerLiveView
}