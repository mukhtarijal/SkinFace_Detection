package com.dicoding.capstone.dermaface.utils

import android.graphics.Bitmap
import java.nio.ByteBuffer
import java.nio.ByteOrder

object ScanUtil {
    private const val INPUT_SIZE = 224

    fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)
        val byteBuffer = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * 3).apply {
            order(ByteOrder.nativeOrder())
        }
        val intValues = IntArray(INPUT_SIZE * INPUT_SIZE)
        resizedBitmap.getPixels(intValues, 0, resizedBitmap.width, 0, 0, resizedBitmap.width, resizedBitmap.height)
        var pixelIndex = 0
        for (i in 0 until INPUT_SIZE) {
            for (j in 0 until INPUT_SIZE) {
                val pixelValue = intValues[pixelIndex++]
                byteBuffer.putFloat(((pixelValue shr 16 and 0xFF) / 255.0f))
                byteBuffer.putFloat(((pixelValue shr 8 and 0xFF) / 255.0f))
                byteBuffer.putFloat(((pixelValue and 0xFF) / 255.0f))
            }
        }
        return byteBuffer
    }
}
