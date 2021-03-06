package com.github.fribourgsdp.radio.persistence

import android.content.Context
import kotlinx.serialization.Serializable

@Serializable
/**
 * should be extended by classes which use a filesystem to save an instance of themselves.
 * A class which extends this function should also extend the FileSystemLoader class
 * to get access to the fileSystemGetter.
 */
abstract class SavesToFileSystem<T>(protected val defaultPath: String) {

    /**
     * saves a dataclass to the app-specific storage on the device.
     * There can only be a single dataclass stored on the device at the default path.
     * This function can be used from any activity of the app to save data accessible from anywhere
     *
     * @param context the context to use for saving to a file (usually this in an activity)
     * @param path a specific path in app-specific storage if we don't want to use the default
     *      user location
     */
    abstract fun save(context: Context, path: String)

    /**
     * saves a dataclass instance to the app-specific storage on the device at the default path.
     * There can only be a single dataclass instance stored on the device at the default path,
     * so this can overwrite a previous instance.
     * This function can be used from any activity of the app to save data accessible from anywhere.
     *
     * @param context the context to use for saving to a file (usually this in an activity)
     */
    fun save(context: Context){
        save(context, defaultPath)
    }
}
