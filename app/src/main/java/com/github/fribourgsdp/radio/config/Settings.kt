package com.github.fribourgsdp.radio.config

import android.content.Context
import com.github.fribourgsdp.radio.Language
import com.github.fribourgsdp.radio.persistence.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
class Settings(private var language : Language) {


    companion object {
        const val SETTINGS_DATA_PATH = "settings_data_file"
        private var fileSystemGetter: FileSystem.FileSystemGetter =
            AppSpecificFileSystem.AppSpecificFSGetter


        fun load(context: Context, path: String = SETTINGS_DATA_PATH) : Settings {
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


    fun save(context: Context, path: String = SETTINGS_DATA_PATH){
        val fs = fileSystemGetter.getFileSystem(context)
        fs.write(path, Json.encodeToString(this))
    }

    fun getLanguage(): Language {
        return language
    }


}