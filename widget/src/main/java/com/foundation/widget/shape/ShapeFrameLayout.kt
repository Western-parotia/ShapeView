package com.foundation.widget.shape

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * 可实现xml写shape背景
 * 详细说明见[ShapeBuilder]
 */
class ShapeFrameLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    FrameLayout(context, attrs, defStyleAttr) {
    private val mShapeHelper = ShapeInitHelper(this)

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        mShapeHelper.initAttrs(attrs)
    }

    override fun setBackground(background: Drawable?) {
        mShapeHelper.setBackground(background)
        super.setBackground(background)
    }

    fun buildShape(): ShapeBuilder {
        return mShapeHelper.builder
    }
}