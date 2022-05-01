package com.github.fribourgsdp.radio

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.zxing.WriterException


class CreateQRCodeFragment(val ctx: Context, val lobbyId: Long ): DialogFragment() {
    // variables for imageview, edittext,
    // button, bitmap and qrencoder.
    private lateinit var qrCodeIV: ImageView
    private lateinit var rootView : View

    private lateinit var cancelButton: Button
    lateinit var bitmap: Bitmap
    lateinit var qrgEncoder: QRGEncoder

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

        if (TextUtils.isEmpty(lobbyId.toString())) {

            // if the edittext inputs are empty then execute
            // this method showing a toast message.
            Toast.makeText(
                ctx,
                "Enter some text to generate QR Code",
                Toast.LENGTH_SHORT
            ).show()
        } else {
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
            qrgEncoder =
                QRGEncoder(lobbyId.toString(), null, QRGContents.Type.TEXT, dimen)
            try {
                // getting our qrcode in the form of bitmap.
                bitmap = qrgEncoder.encodeAsBitmap()
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

}