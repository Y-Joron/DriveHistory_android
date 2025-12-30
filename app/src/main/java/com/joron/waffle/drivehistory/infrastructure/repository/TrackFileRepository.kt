package com.joron.waffle.drivehistory.infrastructure.repository

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.Charset

object TrackFileRepository {

    private const val TAG = "TrackFileRepository"
    private const val LOCATIONS_FILE_SUFFIX = ".los"
    private const val LOCATIONS_DIR = "/locations"

    fun readLocations(context: Context, trackUuid: String): String {
        Log.d(TAG, "enter readLocations trackUuid = $trackUuid")
        val rootPath = context.filesDir.path
        val name = "$rootPath$LOCATIONS_DIR/$trackUuid$LOCATIONS_FILE_SUFFIX"
        val file = File(name)
        val strBuilder = StringBuilder()
        BufferedReader(
            InputStreamReader(
                FileInputStream(file),
                Charset.forName("UTF-8"),
            ),
        ).use { input ->
            var line: String? = ""
            while ((input.readLine().also { line = it }) != null) {
                strBuilder.append(line)
            }
        }
        return strBuilder.toString()
    }

    fun writeLocations(
        context: Context,
        trackUuid: String,
        locations: String,
    ) {
        Log.d(TAG, "enter writeLocations trackUuid = $trackUuid")
        val rootPath = context.filesDir.path
        val dirFile = File(rootPath + LOCATIONS_DIR)
        if (!dirFile.exists()) {
            dirFile.mkdirs()
        }

        val name = "${dirFile.path}/$trackUuid$LOCATIONS_FILE_SUFFIX"
        val file = File(name)
        BufferedWriter(
            OutputStreamWriter(
                FileOutputStream(file, false),
                Charset.forName("UTF-8"),
            ),
        ).use { it.write(locations) }
    }

}