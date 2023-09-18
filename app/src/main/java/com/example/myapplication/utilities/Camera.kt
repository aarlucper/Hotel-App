package com.example.myapplication.utilities

import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns

class Camera {

    //Devuelve el nombre del archivo correspondiente a un URI
    private fun getFilenameFromUri (uri: Uri, cursor: Cursor?) : String? {
        var filename : String? = null


        cursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1){
                    filename = cursor.getString(displayNameIndex)
                }
            }
        }
        return filename
    }
    //TODO: Meter este método en una clase de utilidades de fotos

}