package com.testdemo.testView

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log

/**
 * Create by Greyson
 */
class ColorfulDrawable() : Drawable() {

    private var paint: Paint = Paint()
    private val path = Path()

    /**
     *   _____
     *  /    /
     * /____/
     * 斜四方形的上、下边长度
     */
    private val inclinedRectWidth = 13f

    /**
     * 斜四方形的斜边宽度
     */
    private val inclinedLineWidth = 10f

    override fun draw(canvas: Canvas) {
        canvas.drawColor(0x3E82FB)
        paint.color = 0xAAC8FF
        Log.d("greyson", "width:$intrinsicWidth, height=$intrinsicHeight")
        val number = intrinsicWidth / inclinedRectWidth
        for (i in 0..number.toInt() + 1 step 2) {
            drawInclinedRect(canvas, i)
        }
    }

    private fun drawInclinedRect(canvas: Canvas, offset: Int) {
//        path.reset()
        path.moveTo(offset * inclinedRectWidth, 0f)
        path.rLineTo(inclinedRectWidth, 0f)
        path.rLineTo(-inclinedLineWidth, inclinedLineWidth)
        path.rLineTo(-inclinedRectWidth, 0f)
        path.rLineTo(inclinedLineWidth, -inclinedLineWidth)
        canvas.drawPath(path, paint)
    }

    override fun getMinimumWidth(): Int {
        return 400
    }

    override fun getIntrinsicHeight(): Int {
        return 30
    }

    override fun getIntrinsicWidth(): Int {
        return 600
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}