package com.github.fribourgsdp.radio

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.random.Random

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


        fun loadOrDefault(context: Context) : Task<Settings> {
            return try {
                Tasks.forResult(load(context))
            } catch (e: java.io.FileNotFoundException) {
                createDefaultSettings()
            }
        }


        fun createDefaultSettings(): Task<Settings> {
            val settings  = Settings(Language.ENGLISH)
            return Tasks.forResult(settings)
        }


    }


    fun save(context: Context, path: String = SETTINGS_DATA_PATH){
        val fs = fileSystemGetter.getFileSystem(context)
        fs.write(path, Json.encodeToString(this))
    }

    fun getLanguage(): Language{
        return language
    }


}