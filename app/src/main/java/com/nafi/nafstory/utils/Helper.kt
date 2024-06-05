package com.nafi.nafstory.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

private const val FILENAME_FORMAT = "dd-MMM-yyyy"

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

// Membuat file sementara dengan format waktu saat ini
fun createCustomTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

// Mengubah URI menjadi file
fun uriToFile(selectedImg: Uri, context: Context): File? {
    val contentResolver: ContentResolver = context.contentResolver

    // Validasi URI
    if (selectedImg.scheme != ContentResolver.SCHEME_CONTENT) return null

    val myFile = createCustomTempFile(context)

    return try {
        contentResolver.openInputStream(selectedImg)?.use { inputStream ->
            FileOutputStream(myFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        myFile
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

// Mengurangi ukuran gambar dengan mengompresinya
fun reduceFileImage(file: File): File? {
    val bitmap = BitmapFactory.decodeFile(file.path) ?: return null

    var compressQuality = 100
    var streamLength: Int

    try {
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)

        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, outputStream)
        }
        return file
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
}
