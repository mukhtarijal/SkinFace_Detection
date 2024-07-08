package com.dicoding.capstone.dermaface.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object MainUtil {
    private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

    fun createTempImageUri(context: Context): Uri {
        val cacheDir = File(context.cacheDir, "images")
        if (!cacheDir.exists()) cacheDir.mkdirs()
        val imageFile = File(cacheDir, "$timeStamp.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
    }
}
