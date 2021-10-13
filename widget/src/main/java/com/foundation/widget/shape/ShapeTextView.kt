package com.foundation.widget.shape

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * 可实现xml写shape背景
 * 详细说明见[ShapeBuilder]
 */
open class ShapeTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.textViewStyle) :
    AppCompatTextView(context, attrs, defStyleAttr), IShape {

    private var _shapeHelper: ShapeInitHelper? = null
        get() {
            if (field == null) {
                field = ShapeInitHelper(this)
            }
            return field
        }
    private val shapeHelper get() = _shapeHelper!!

    init {
        shapeHelper.initAttrs(attrs)
    }

    override fun onDraw(canvas: Canvas) {
        shapeHelper.onDraw(canvas)
        super.onDraw(canvas)
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return shapeHelper.verifyDrawable(who, super.verifyDrawable(who))
    }

    override fun getSuggestedMinimumHeight(): Int {
        return shapeHelper.getSuggestedMinimumHeight(super.getSuggestedMinimumHeight())
    }

    override fun getSuggestedMinimumWidth(): Int {
        return shapeHelper.getSuggestedMinimumWidth(super.getSuggestedMinimumWidth())
    }

    /**
     * 代码设置
     */
    override fun buildShape(): ShapeBuilder {
        return shapeHelper.builder
    }
}