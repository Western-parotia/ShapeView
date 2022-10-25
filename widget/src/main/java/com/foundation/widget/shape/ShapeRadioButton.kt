package com.foundation.widget.shape

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

/**
 * 可实现xml写shape背景
 * 详细说明见[ShapeBuilder]
 */
open class ShapeRadioButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.radioButtonStyle) :
    AppCompatRadioButton(context, attrs, defStyleAttr), IShape {

    private var _shapeHelper: ShapeInitHelper? = null
        get() {
            if (field == null) {
                field = ShapeInitHelper(this)
            }
            return field
        }
    private val shapeHelper get() = _shapeHelper!!

    /**
     * 是否只能代码设置，点击无法改变选中状态
     */
    var onlyCodeChecked = false

    init {
        shapeHelper.initAttrs(attrs)
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.ShapeRadioButton)
            onlyCodeChecked = a.getBoolean(R.styleable.ShapeRadioButton_onlyCodeChecked, false)
            a.recycle()
        }
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

    private var isPerformClick = false
    override fun performClick(): Boolean {
        isPerformClick = true
        val result = super.performClick()
        isPerformClick = false
        return result
    }

    override fun setChecked(checked: Boolean) {
        if (!(onlyCodeChecked && isPerformClick)) {
            super.setChecked(checked)
        }
        isPerformClick = false
    }

    /**
     * 代码设置
     */
    override fun buildShape(): ShapeBuilder {
        return shapeHelper.builder
    }

    /**
     * [onlyCodeChecked]
     */
}