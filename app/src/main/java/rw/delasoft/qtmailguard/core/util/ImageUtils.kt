package rw.delasoft.qtmailguard.core.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory

object ImageUtils {

    fun decodeBitmap(bytes: ByteArray?): Bitmap? {
        if (bytes == null || bytes.isEmpty()) return null

        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

            options.inSampleSize = calculateInSampleSize(options, 800, 600)
            options.inJustDecodeBounds = false

            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        } catch (e: Exception) {
            null
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight &&
                halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    fun isValidImage(bytes: ByteArray?): Boolean {
        if (bytes == null || bytes.size < 4) return false

        val pngSignature = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47)
        val jpegSignature = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte())

        return bytes.take(4).toByteArray().contentEquals(pngSignature) ||
                bytes.take(3).toByteArray().contentEquals(jpegSignature)
    }
}
