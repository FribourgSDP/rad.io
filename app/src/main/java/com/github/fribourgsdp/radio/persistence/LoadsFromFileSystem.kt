package com.github.fribourgsdp.radio.persistence

import android.content.Context

/**
 * should be extended by the companion object of classes which use a filesystem
 * to save an instance of themselves.
 */
abstract class LoadsFromFileSystem<T : SavesToFileSystem<T>> {
    protected var fileSystemGetter: FileSystem.FileSystemGetter = AppSpecificFileSystem
    protected open var defaultPath: String = "default"+this.toString()+"file"

    /**
     * changes the default app specific file system wrapper to a custom one
     * useful for tests
     *
     * @param fsg the [FileSystem.FileSystemGetter] to will replace the current implementation
     */
    fun setFSGetter(fsg: FileSystem.FileSystemGetter) {
        fileSystemGetter = fsg
    }

    /**
     * loads a dataclass from the app-specific storage on the device.
     * There can only be a single datacalass stored on the device at per path
     * which is written with [SavesToFileSystem].save().
     * This function can be used from any activity of the app and retrieves the same data
     *
     * @param context the context to use for loading from a file (usually this in an activity)
     * @param path a specific path in app-specific storage if we don't want to use the default
     *      user location
     * @throws java.io.FileNotFoundException
     *
     * @return the dataclass saved on the device
     */
    abstract fun load(context: Context, path: String) : T

    /**
     * loads a dataclass from the app-specific storage on the device at the default path.
     * There can only be a single dataclass instance stored on the device at this path
     * which is written with [SavesToFileSystem].save().
     * This function can be used from any activity of the app and retrieves the same data
     *
     * @param context the context to use for loading from a file (usually this in an activity)
     * @throws java.io.FileNotFoundException
     *
     * @return the user saved on the device
     */
    fun load(context: Context): T {
        return load(context, defaultPath)
    }
}