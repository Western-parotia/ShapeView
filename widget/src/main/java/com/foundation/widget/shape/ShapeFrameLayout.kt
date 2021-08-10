package com.foundation.widget.shape

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * 可实现xml写shape背景
 * 详细说明见[ShapeBuilder]
 */
open class ShapeFrameLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr), IShape {
    private val mShapeHelper = ShapeInitHelper(this)

    init {
        mShapeHelper.initAttrs(attrs)
    }

    override fun onDraw(canvas: Canvas) {
        mShapeHelper.onDraw(canvas)
        super.onDraw(canvas)
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return mShapeHelper.verifyDrawable(who, super.verifyDrawable(who))
    }

    override fun getSuggestedMinimumHeight(): Int {
        return mShapeHelper.getSuggestedMinimumHeight(super.getSuggestedMinimumHeight())
    }

    override fun getSuggestedMinimumWidth(): Int {
        return mShapeHelper.getSuggestedMinimumWidth(super.getSuggestedMinimumWidth())
    }

    /**
     * 代码设置
     */
    override fun buildShape(): ShapeBuilder {
        return mShapeHelper.builder
    }
}