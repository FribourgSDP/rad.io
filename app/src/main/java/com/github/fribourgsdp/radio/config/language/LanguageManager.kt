package com.github.fribourgsdp.radio.config.language

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import java.util.*

open class LanguageManager(private var context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("LANG", Context.MODE_PRIVATE)

    fun updateResource(code: String){
        val locale = Locale(code)
        Locale.setDefault(locale)
        val resources : Resources = context.getResources()
        val metrics : DisplayMetrics = resources.displayMetrics
        val configuration : Configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, metrics)
        setLang(code)
    }

    open fun getLang() : String? {
        return sharedPreferences.getString("lang", "en")
    }

    open fun setLang(code :String){
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("lang", code)
        editor.commit()
    }


}