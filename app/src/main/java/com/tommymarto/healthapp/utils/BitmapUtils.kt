package com.tommymarto.healthapp.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View

fun Drawable.toBitmap(): Bitmap? {
    if (this is BitmapDrawable) {
        return this.bitmap
    }
    val bitmap = Bitmap.createBitmap(this.intrinsicWidth, this.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, canvas.width, canvas.height)
    this.draw(canvas)
    return bitmap
}

fun View.toBitmap(bitmapWidth: Int, bitmapHeight: Int): Bitmap? {
    val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    this.layout(0, 0, bitmapWidth, bitmapHeight)
    this.draw(canvas)
    return bitmap
}