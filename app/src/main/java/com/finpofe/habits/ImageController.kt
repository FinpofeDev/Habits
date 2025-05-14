package com.finpofe.habits

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object ImageController {
    fun seleccionarFotoDesdeGaleria(activity: Activity, codigo: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        activity.startActivityForResult(intent, codigo)
    }

    fun guardarImagen(context: Context, bitmap: Bitmap? = null): String{
        return try {
            val directory = File(context.filesDir, "pfp")
            if(!directory.exists()) {
                directory.mkdirs()
            }

            val fileName = "image_${System.currentTimeMillis()}.png"
            val file = File(directory, fileName)

            val fos = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.PNG, 90, fos)
            fos.close()

            fileName
        }catch(e: IOException) {
            ""
        }
    }

    fun cargarImagen(context: Context, filename: String): Bitmap? {
        return try {
            val file = File(File(context.filesDir, "pfp"), filename)
            BitmapFactory.decodeStream(FileInputStream(file))
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}