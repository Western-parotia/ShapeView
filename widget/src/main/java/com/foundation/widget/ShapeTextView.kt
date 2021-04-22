package com.foundation.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * 可实现xml写shape背景
 * 详细说明见[ShapeBuilder]
 */
class ShapeTextView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        AppCompatTextView(context, attrs, defStyleAttr) {
    private val mShapeBuilder = ShapeBuilder(this)

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        mShapeBuilder.initAttrs(attrs)
    }

    override fun setBackground(background: Drawable?) {
        mShapeBuilder.setBackground(background)
        super.setBackground(background)
    }

    fun buildShape(): ShapeBuilder {
        return mShapeBuilder
    }
}