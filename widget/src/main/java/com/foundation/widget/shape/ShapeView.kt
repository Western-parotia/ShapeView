package com.foundation.widget.shape

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View

/**
 * 可实现xml写shape背景
 * 详细说明见[ShapeBuilder]
 */
class ShapeView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {
    private val mShapeHelper = ShapeInitHelper(this)

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

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
    fun buildShape(): ShapeBuilder {
        return mShapeHelper.builder
    }
}