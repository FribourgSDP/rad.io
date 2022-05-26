package com.github.fribourgsdp.radio

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

@SuppressLint("CustomSplashScreen")
/**
 * The [SplashScreen] that appears at the start of the app.
 *
 * NB: I decided to use a custom slash screen as it was possible to be done on more API levels.
 */
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val startIntent = Intent(this, MainActivity::class.java)
            startActivity(startIntent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, LOAD_TIME_IN_MILLIS);
    }

    companion object {
        const val LOAD_TIME_IN_MILLIS = 1000L
    }
}