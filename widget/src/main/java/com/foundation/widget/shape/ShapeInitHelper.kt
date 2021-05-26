package com.foundation.widget.shape

import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt

/**
 * shape的实现逻辑
 * view初始化：[initAttrs]
 * view当设置背景时：[setBackground]
 * 设置属性：[builder]
 * 见实现[ShapeTextView]
 */
class ShapeInitHelper(private val targetView: View) {
    val builder = ShapeBuilder(targetView)

    /**
     * 自定义view初始化attrs调用
     */
    fun initAttrs(attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }
        val a = targetView.context.obtainStyledAttributes(attrs, R.styleable.ShapeInfo)
        if (!a.hasValue(R.styleable.ShapeInfo_android_shape)) {
            a.recycle()
            return
        }

        //类型，GradientDrawable.updateStateFromTypedArray
        val shape = a.getInt(R.styleable.ShapeInfo_android_shape, GradientDrawable.RECTANGLE)
        builder.setShape(shape)

        //填充色，GradientDrawable.updateGradientDrawableSolid
        builder.setSolidColorStateList(a.getColorStateList(R.styleable.ShapeInfo_shapeSolidColor/*mGd?.color，api24*/))

        //圆角，GradientDrawable.updateDrawableCorners
        val radius = getPx(a, R.styleable.ShapeInfo_shapeCornersRadius/*mGd?.cornerRadius，api24*/)
        builder.setCornersRadius(radius)
        val shapeTopLeftRadius = getPx(a, R.styleable.ShapeInfo_shapeCornersTopLeftRadius, radius)
        val shapeTopRightRadius = getPx(a, R.styleable.ShapeInfo_shapeCornersTopRightRadius, radius)
        val shapeBottomLeftRadius = getPx(a, R.styleable.ShapeInfo_shapeCornersBottomLeftRadius, radius)
        val shapeBottomRightRadius = getPx(a, R.styleable.ShapeInfo_shapeCornersBottomRightRadius, radius)
        if (shapeTopLeftRadius != radius || shapeTopRightRadius != radius ||
            shapeBottomLeftRadius != radius || shapeBottomRightRadius != radius) {
            builder.setCornersRadius(shapeTopLeftRadius, shapeTopRightRadius, shapeBottomLeftRadius, shapeBottomRightRadius)
        }

        //大小，GradientDrawable.updateGradientDrawableSize
        builder.setSize(getPx(a, R.styleable.ShapeInfo_shapeSizeWidth, builder.drawable!!.intrinsicWidth),
            getPx(a, R.styleable.ShapeInfo_shapeSizeHeight, builder.drawable!!.intrinsicHeight))

        //渐变，GradientDrawable.updateGradientDrawableGradient
        if (a.hasValue(R.styleable.ShapeInfo_shapeGradientType) || a.hasValue(R.styleable.ShapeInfo_shapeGradientStartColor)) {
            builder.setGradientType(a.getInt(R.styleable.ShapeInfo_shapeGradientType, GradientDrawable.LINEAR_GRADIENT/*mGd?.gradientType，api24*/))
            val shapeGradientCenterX = getFloatOrFraction(a, R.styleable.ShapeInfo_shapeGradientCenterX, 0.5f/*mGd?.gradientCenterX，api24*/)
            val shapeGradientCenterY = getFloatOrFraction(a, R.styleable.ShapeInfo_shapeGradientCenterY, 0.5f/*mGd?.gradientCenterY，api24*/)
            builder.setGradientCenter(shapeGradientCenterX, shapeGradientCenterY)
            //val colors = mGd?.colors，api24
            //低api有bug，set方法和solidColor冲突
            if (a.hasValue(R.styleable.ShapeInfo_shapeGradientStartColor)) {
                val shapeGradientStartColor = getColor(a, R.styleable.ShapeInfo_shapeGradientStartColor)
                val hasCenterColor = a.hasValue(R.styleable.ShapeInfo_shapeGradientCenterColor)
                val shapeGradientCenterColor = getColor(a, R.styleable.ShapeInfo_shapeGradientCenterColor)
                val shapeGradientEndColor = getColor(a, R.styleable.ShapeInfo_shapeGradientEndColor)
                if (hasCenterColor) {
                    builder.setGradientColors(intArrayOf(shapeGradientStartColor, shapeGradientCenterColor, shapeGradientEndColor),
                        floatArrayOf(0f, if (shapeGradientCenterX != 0.5f) shapeGradientCenterX else shapeGradientCenterY, 1f))
                } else {
                    builder.setGradientColors(intArrayOf(shapeGradientStartColor, shapeGradientEndColor))
                }
            }
            builder.setGradientAngle(a.getInt(R.styleable.ShapeInfo_shapeGradientAngle, 0/*预览图bug，默认其实就是左右*/))
            //目前代码只支持固定值，用到再说
            builder.setGradientRadius(getPx(a, R.styleable.ShapeInfo_shapeGradientRadius))
        }

        //padding，GradientDrawable.updateGradientDrawablePadding
        val shapePadding = getPx(a, R.styleable.ShapeInfo_shapePadding)
        builder.setPadding(getPx(a, R.styleable.ShapeInfo_shapePaddingLeft, shapePadding),
            getPx(a, R.styleable.ShapeInfo_shapePaddingTop, shapePadding),
            getPx(a, R.styleable.ShapeInfo_shapePaddingRight, shapePadding),
            getPx(a, R.styleable.ShapeInfo_shapePaddingBottom, shapePadding))

        //圆环相关，GradientDrawable.updateStateFromTypedArray
        if (shape == GradientDrawable.RING) {
            val shapeInnerRadius = getPx(a, R.styleable.ShapeInfo_shapeInnerRadius, -1/*mGd?.innerRadius，api29*/)
            val shapeInnerRadiusRatio = a.getFloat(R.styleable.ShapeInfo_shapeInnerRadiusRatio, 3f /*mGd?.innerRadiusRatio，api29*/)
            val shapeThickness = getPx(a, R.styleable.ShapeInfo_shapeThickness, -1/*mGd?.thickness，api29*/)
            val shapeThicknessRatio = a.getFloat(R.styleable.ShapeInfo_shapeThicknessRatio, 9f/*mGd?.thicknessRatio，api29*/)
            builder.setRingInnerRadius(shapeInnerRadius)
            if (shapeInnerRadius == -1) {
                builder.setRingInnerRadiusRatio(shapeInnerRadiusRatio)
            }
            builder.setRingThickness(shapeThickness)
            if (shapeThickness == -1) {
                builder.setRingThicknessRatio(shapeThicknessRatio)
            }
        }

        targetView.background = builder.drawable

        a.recycle()
    }

    /**
     * 自定义view重写setBackground调用
     */
    fun setBackground(background: Drawable?) {
        builder.drawable = if (background is GradientDrawable) background else null
    }

    private fun getPx(a: TypedArray, index: Int, def: Int = 0) = a.getDimensionPixelSize(index, def)

    @ColorInt
    private fun getColor(a: TypedArray, index: Int, def: Int = 0) = a.getColor(index, def)

    private fun getFloatOrFraction(a: TypedArray, index: Int, defaultValue: Float = 0f): Float {
        val tv = a.peekValue(index)
        var v = defaultValue
        if (tv != null) {
            val vIsFraction = tv.type == TypedValue.TYPE_FRACTION
            v = if (vIsFraction) tv.getFraction(1.0f, 1.0f) else tv.float
        }
        return v
    }
}