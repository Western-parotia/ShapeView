package com.foundation.widget.shape

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.R
import androidx.appcompat.widget.AppCompatRadioButton

/**
 * 可实现xml写shape背景
 * 详细说明见[ShapeBuilder]
 */
open class ShapeRadioButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.radioButtonStyle) :
    AppCompatRadioButton(context, attrs, defStyleAttr), IShape {
    private var shapeHelper: ShapeInitHelper? = null

    init {
        getHelper().initAttrs(attrs)
    }

    /**
     * CheckedBox、RadioButton会在构造调用[verifyDrawable]等方法
     */
    private fun getHelper(): ShapeInitHelper {
        if (shapeHelper == null) {
            shapeHelper = ShapeInitHelper(this)
        }
        return shapeHelper!!
    }

    override fun onDraw(canvas: Canvas) {
        getHelper().onDraw(canvas)
        super.onDraw(canvas)
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return getHelper().verifyDrawable(who, super.verifyDrawable(who))
    }

    override fun getSuggestedMinimumHeight(): Int {
        return getHelper().getSuggestedMinimumHeight(super.getSuggestedMinimumHeight())
    }

    override fun getSuggestedMinimumWidth(): Int {
        return getHelper().getSuggestedMinimumWidth(super.getSuggestedMinimumWidth())
    }

    /**
     * 代码设置
     */
    override fun buildShape(): ShapeBuilder {
        return getHelper().builder
    }
}