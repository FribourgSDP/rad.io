package com.github.fribourgsdp.radio.game.prep

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.github.fribourgsdp.radio.R
import com.google.zxing.WriterException


class CreateQRCodeFragment(val ctx: Context, private val lobbyId: Long ): DialogFragment() {
    // variables for imageview, edittext,
    // button, bitmap and qrencoder.
    private lateinit var qrCodeIV: ImageView
    private lateinit var rootView : View

    private lateinit var cancelButton: Button

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_create_qr_code, container, false)
        initializeQrCode()
        return rootView
    }


    private fun initializeQrCode(){
        // initializing all variables.
        qrCodeIV = rootView.findViewById(R.id.idIVQrcode)

        cancelButton = rootView.findViewById(R.id.cancel_button)
        cancelButton.setOnClickListener{
            dismiss()
        }

        // initializing onclick listener for button.
        generateQRCode()

    }

    private fun generateQRCode(){


        // below line is for getting
        // the windowmanager service.
        val manager = ctx.getSystemService(AppCompatActivity.WINDOW_SERVICE) as WindowManager

        // initializing a variable for default display.
        val display = manager.defaultDisplay

        // creating a variable for point which
        // is to be displayed in QR Code.
        val point = Point()
        display.getSize(point)

        // getting width and
        // height of a point
        val width = point.x
        val height = point.y

        // generating dimension from width and height.
        var dimen = if (width < height) width else height
        dimen = dimen * 3 / 4

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        val qrgEncoder = QRGEncoder(lobbyId.toString(), null, QRGContents.Type.TEXT, dimen)
        try {
            // getting our qrcode in the form of bitmap.
            val bitmap = qrgEncoder.encodeAsBitmap()
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
            qrCodeIV.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString())
        }
    }
}