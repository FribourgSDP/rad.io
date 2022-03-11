package com.github.fribourgsdp.radio

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.fribourgsdp.radio.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fireBaseButton = findViewById<Button>(R.id.FireBaseButton)
        fireBaseButton.setOnClickListener{
            startActivity(Intent(this,FireBaseTestActivity::class.java))
        }

       /* binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonGoToGoogleSignIn.setOnClickListener{
            startActivity(Intent(this, FireBaseTestActivity::class.java))
            finish()
        }*/



    }
}