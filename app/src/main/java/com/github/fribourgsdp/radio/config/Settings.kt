package com.github.fribourgsdp.radio.config

import android.content.Context
import com.github.fribourgsdp.radio.config.language.Language
import com.github.fribourgsdp.radio.persistence.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Settings(val language : Language) : SavesToFileSystem<Settings>(SETTINGS_DATA_PATH) {

    companion object : LoadsFromFileSystem<Settings>(){
        const val SETTINGS_DATA_PATH = "settings_data_file"
        override var defaultPath = SETTINGS_DATA_PATH

        override fun load(context: Context, path: String) : Settings {
            val fs = fileSystemGetter.getFileSystem(context)
            return Json.decodeFromString(fs.read(path))
        }

        fun loadOrDefault(context: Context) : Settings {
            return try { load(context)
            } catch (e: java.io.FileNotFoundException) {
                createDefaultSettings()
            }
        }

        fun createDefaultSettings(): Settings {
            return Settings(Language.ENGLISH)
        }
    }

    override fun save(context: Context, path: String){
        val fs = fileSystemGetter.getFileSystem(context)
        fs.write(path, Json.encodeToString(this))
    }
}