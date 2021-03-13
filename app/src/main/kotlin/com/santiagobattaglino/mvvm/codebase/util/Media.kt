package com.santiagobattaglino.mvvm.codebase.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.sqrt

fun getBitmap(imagePath: String, useOriginalSize: Boolean): Bitmap? {
    val maxPictureSize = 400000
    try {
        var rotationAngle = 0
        val exif = ExifInterface(imagePath)
        val orientString =
            exif.getAttribute(ExifInterface.TAG_ORIENTATION)
        val orientation =
            orientString?.toInt() ?: ExifInterface.ORIENTATION_NORMAL
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270
        val options = BitmapFactory.Options()
        val bm = BitmapFactory.decodeFile(imagePath, options)
        val currWidth = options.outWidth
        val currHeight = options.outHeight
        val matrix = Matrix()
        if (rotationAngle != 0) {
            matrix.preRotate(
                rotationAngle.toFloat(),
                bm.width.toFloat() / 2,
                bm.height.toFloat() / 2
            )
        }
        if (!useOriginalSize && currWidth * currHeight > maxPictureSize) {
            val scale =
                sqrt(maxPictureSize.toFloat() / (currWidth * currHeight).toDouble()).toFloat()
            matrix.preScale(scale, scale)
        }
        return Bitmap.createBitmap(bm, 0, 0, currWidth, currHeight, matrix, true)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

fun getBitmap(imagePath: String): Bitmap? {
    try {
        val bitmap = BitmapFactory.decodeFile(imagePath)

        val exif = ExifInterface(imagePath)
        val orientation =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        val matrix = Matrix()

        /*when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
        }*/

        val rotatedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return rotatedBitmap
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

fun writeBitmapJpgCompressedFile(context: Context, bitmap: Bitmap, timeStamp: String) {
    val storageDir: File? = context.getExternalFilesDir(null)
    val outputFile = File.createTempFile("add_incident_resized_${timeStamp}_", ".jpg", storageDir)
    try {
        val out = FileOutputStream(outputFile)

        /*val oldExif = ExifInterface(currentPhotoPath)
                val exifOrientation: String? = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION)

                if (exifOrientation != null) {
                    val newExif = ExifInterface(currentPhotoResizedPath)
                    newExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation)
                    newExif.saveAttributes()
                }*/

        Bitmap.createScaledBitmap(bitmap, 1440, 1080, true)
            .compress(Bitmap.CompressFormat.JPEG, 70, out)

        out.flush()
        out.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun reizeImageFileWithGlide(
    context: Context,
    file: File,
    outputFile: File,
    size: Int
) {
    val bitmap = GlideApp.with(context)
        .asBitmap()
        .load(file)
        // .apply(RequestOptions().override(size))
        // .apply(RequestOptions().override(size).downsample(DownsampleStrategy.AT_MOST))
        .apply(
            RequestOptions().override(size).downsample(DownsampleStrategy.CENTER_INSIDE)
                .skipMemoryCache(true).diskCacheStrategy(
                    DiskCacheStrategy.NONE
                )
        )
        .submit().get()

    val out = FileOutputStream(outputFile)
    bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.JPG_QUALITY, out)
    out.flush()
    out.close()

    //copyExif(file.absolutePath, outputFile.absolutePath)
}

fun getMediaLocation(path: String): DoubleArray {
    val exif = ExifInterface(path)
    val latLong = exif.latLong ?: doubleArrayOf(0.0, 0.0)
    Log.d("Media", "getMediaLocation lat: ${latLong[0]}, long: ${latLong[1]}")
    return latLong
}

fun copyExif(oldPath: String, newPath: String) {
    val oldExif = ExifInterface(oldPath)

    val attributes = arrayOf(
        // ExifInterface.TAG_APERTURE,
        ExifInterface.TAG_APERTURE_VALUE,
        ExifInterface.TAG_DATETIME,
        ExifInterface.TAG_DATETIME_DIGITIZED,
        ExifInterface.TAG_DATETIME_ORIGINAL,
        ExifInterface.TAG_EXPOSURE_TIME,
        ExifInterface.TAG_FLASH,
        ExifInterface.TAG_FOCAL_LENGTH,
        ExifInterface.TAG_GPS_ALTITUDE,
        ExifInterface.TAG_GPS_ALTITUDE_REF,
        ExifInterface.TAG_GPS_DATESTAMP,
        ExifInterface.TAG_GPS_LATITUDE,
        ExifInterface.TAG_GPS_LATITUDE_REF,
        ExifInterface.TAG_GPS_LONGITUDE,
        ExifInterface.TAG_GPS_LONGITUDE_REF,
        ExifInterface.TAG_GPS_PROCESSING_METHOD,
        ExifInterface.TAG_GPS_TIMESTAMP,
        // ExifInterface.TAG_IMAGE_LENGTH,
        // ExifInterface.TAG_IMAGE_WIDTH,
        //ExifInterface.TAG_ISO,
        //ExifInterface.TAG_ISO_SPEED_RATINGS,
        ExifInterface.TAG_MAKE,
        ExifInterface.TAG_MODEL,
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.TAG_SUBSEC_TIME,
        ExifInterface.TAG_SUBSEC_TIME_ORIGINAL,
        ExifInterface.TAG_SUBSEC_TIME_DIGITIZED,
        // ExifInterface.TAG_SUBSEC_TIME_DIG,
        // ExifInterface.TAG_SUBSEC_TIME_ORIG,
        ExifInterface.TAG_WHITE_BALANCE,
        ExifInterface.TAG_SUBJECT_LOCATION
    )

    val newExif = ExifInterface(newPath)
    for (i in attributes.indices) {
        val value = oldExif.getAttribute(attributes[i])
        Log.d("Media", "Exif Attribute ${attributes[i]}: $value")
        //if (value != null)
        //newExif.setAttribute(attributes[i], value)
    }
    //newExif.saveAttributes()
}