package com.github.fribourgsdp.radio.config

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.github.fribourgsdp.radio.MainActivity
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.config.language.Language
import com.github.fribourgsdp.radio.config.language.LanguageManager


open class SettingsActivity : MyAppCompatActivity() {


    private lateinit var spinnerLanguage: Spinner
    private lateinit var settings : Settings
    private lateinit var settingsBuilder : SettingsBuilder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        val settings = Settings.loadOrDefault(this)
        this.settings = settings
        settingsBuilder = SettingsBuilder(settings)
        initLanguageSpinner(settings.language)


    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initLanguageSpinner(actualLanguage : Language){


        spinnerLanguage = findViewById(R.id.spinner_language)
        val languageList: Array<Language> =  Language.values()

        val adapter: ArrayAdapter<Language> = ArrayAdapter<Language>(this,android.R.layout.simple_spinner_item,languageList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.adapter = adapter
        spinnerLanguage.setSelection(actualLanguage.ordinal, false);
        spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                if (parent != null && view != null) {
                    onItemSelectedHandler(parent, view, position, id)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun onItemSelectedHandler(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
        val adapter: Adapter = adapterView.adapter
        val language: Language = adapter.getItem(position) as Language
        settingsBuilder.language(language)
        settings = settingsBuilder.build()
        settings.save(this)
        setLocale(language.code)
    }



    private fun setLocale(language : String){
        val languageManager  = getLanguageManager()
        if(languageManager.getLang() != language) {
            languageManager.setLang(language)
            val refresh = Intent(this, SettingsActivity::class.java)
            finish()
            startActivity(refresh)
        }
    }

    protected open fun getLanguageManager() : LanguageManager{
        return LanguageManager(this)
    }

}
