package com.foundation.widget.shape

import android.graphics.Canvas
import android.graphics.drawable.Drawable

interface IShape {

    /**
     * 重写onDraw固定写法（必须）：
     * super.onDraw(canvas)
     * shapeHelper.onDraw(canvas)
     */
    fun onDraw(canvas: Canvas)

    /**
     * 重写verifyDrawable固定写法（必须）：
     * return shapeHelper.verifyDrawable(who, super.verifyDrawable(who))
     */
    fun verifyDrawable(who: Drawable): Boolean

    /**
     * 重写getSuggestedMinimumHeight固定写法（必须）：
     * return shapeHelper.getSuggestedMinimumHeight(super.getSuggestedMinimumHeight())
     */
    fun getSuggestedMinimumHeight(): Int

    /**
     * 重写getSuggestedMinimumWidth固定写法（必须）：
     * return shapeHelper.getSuggestedMinimumWidth(super.getSuggestedMinimumWidth())
     */
    fun getSuggestedMinimumWidth(): Int

    fun buildShape(): ShapeBuilder
}