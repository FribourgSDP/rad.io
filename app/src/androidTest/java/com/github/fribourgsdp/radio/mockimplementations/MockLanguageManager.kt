package com.github.fribourgsdp.radio.mockimplementations

import android.content.Context
import com.github.fribourgsdp.radio.config.language.LanguageManager

class MockLanguageManager(context: Context) : LanguageManager(context) {

    override fun getLang() : String? {
        return "en"
    }

    override fun setLang(code :String){

    }
}