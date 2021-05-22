package com.foundation.widget.shape

import android.content.res.ColorStateList
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.NonNull
import androidx.annotation.Px
import androidx.core.content.ContextCompat

/**
 * 可实现xml写shape背景[R.styleable.ShapeTextView]、[R.styleable.ShapeView]、[R.styleable.ShapeFrameLayout]
 *
 * 原理就是设置一个GradientDrawable背景，所以和background会互相覆盖
 *
 * shape相关属性及注释：
 * 1.点击[R.styleable.ShapeTextView_android_shape]，再点android:shape，GradientDrawable开头的styleable均是
 * 2.查看类源码：[GradientDrawable]
 *
 * 待补充：
 * 1.useLevel属性：是否启用缩放（缩放级别见Drawable.setLevel），根布局是缩放RING，gradient布局是LINEAR缩放横轴，RADIAL缩放半径，SWEEP缩放角度
 *                  几乎没啥用，暂时不做
 * 2.gradientRadius：原xml支持比例，受反射限制并且高api没有对应方法，待开发
 */
class ShapeBuilder(private val targetView: View) {
    internal var drawable: GradientDrawable? = null
        @NonNull
        get() {
            if (field == null) {
                field = GradientDrawable()
                //延迟调用，设置背景
                targetView.post { targetView.background = drawable }
            }
            return field
        }

    /**
     * 将mPathIsDirty改为true并请求重绘
     */
    private fun invalidateSelf() {
        drawable?.orientation = drawable?.orientation//调用一次orientation就行了，省的反射
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // set方法
    /////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 圆角类型
     */
    fun setShape(@ShapeDef.Shape shape: Int) {
        drawable?.shape = shape
    }

    /**
     * 填充色
     */
    fun setSolidColorInt(@ColorInt color: Int) {
        drawable?.setColor(color)
    }

    fun setSolidColorRes(@ColorRes color: Int) {
        setSolidColorStateList(ContextCompat.getColorStateList(targetView.context, color))
    }

    fun setSolidColorStateList(color: ColorStateList?) {
        drawable?.color = color
    }

    /**
     * 圆角
     */
    fun setCornersRadius(@Px radius: Int) {
        drawable?.cornerRadius = radius.toFloat()
    }

    /**
     * 注意顺序
     */
    fun setCornersRadius(@Px topLeftRadius: Int = 0, @Px topRightRadius: Int = 0, @Px bottomRightRadius: Int = 0, @Px bottomLeftRadius: Int = 0) {
        setCornersRadius(floatArrayOf(topLeftRadius.toFloat(), topLeftRadius.toFloat(),
            topRightRadius.toFloat(), topRightRadius.toFloat(),
            bottomRightRadius.toFloat(), bottomRightRadius.toFloat(),
            bottomLeftRadius.toFloat(), bottomLeftRadius.toFloat()))
    }

    fun setCornersRadius(radii: FloatArray?) {
        drawable?.cornerRadii = radii
    }

    /**
     * 宽高，暂时没用
     */
    fun setSize(@Px width: Int, @Px height: Int) {
        drawable?.setSize(width, height)
    }

    /**
     * grand类型
     */
    fun setGradientType(@ShapeDef.GradientType type: Int) {
        drawable?.gradientType = type
    }

    /**
     * 中心点，比例
     */
    fun setGradientCenter(x: Float, y: Float) {
        drawable?.setGradientCenter(x, y)
    }

    /**
     * @param colors 渐变颜色，长度3或2：start、center、end或start、end，如[红，黄，蓝]
     * @param offsets 每个渐变的差值率，长度3或2，如：[0,0.5,1]
     */
    fun setGradientColors(@ColorInt colors: IntArray?, offsets: FloatArray? = null) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            drawable?.setColors(colors, offsets)
        } else {
            if (offsets != null) {
                ShapeReflectionUtils.setValue(drawable?.constantState, "mPositions", offsets)
            }
            drawable?.colors = colors
        }
    }

    fun setGradientAngle(angle: Int) {
        setOrientation(when (((angle + 720) % 360) / 45) {
            1 -> GradientDrawable.Orientation.BL_TR
            2 -> GradientDrawable.Orientation.BOTTOM_TOP
            3 -> GradientDrawable.Orientation.BR_TL
            4 -> GradientDrawable.Orientation.RIGHT_LEFT
            5 -> GradientDrawable.Orientation.TR_BL
            6 -> GradientDrawable.Orientation.TOP_BOTTOM
            7 -> GradientDrawable.Orientation.TL_BR
            else -> GradientDrawable.Orientation.LEFT_RIGHT
        })
    }

    fun setOrientation(orientation: GradientDrawable.Orientation) {
        drawable?.orientation = orientation
    }

    /**
     * 目前代码只支持固定值，用到再说
     */
    fun setGradientRadius(@Px radius: Int) {
        drawable?.gradientRadius = radius.toFloat()
    }

    fun setPadding(@Px left: Int, @Px top: Int, @Px right: Int, @Px bottom: Int) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            drawable?.setPadding(left, top, right, bottom)
        } else {//逻辑同上面源码
            val rect = ShapeReflectionUtils.setRect(
                drawable?.constantState, "mPadding",
                Rect(left, top, right, bottom)
            )
            ShapeReflectionUtils.setValue(drawable, "mPadding", rect)
            invalidateSelf()
        }
    }

    fun setRingInnerRadius(@Px radius: Int) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            drawable?.innerRadius = radius
        } else {//逻辑同上源码
            ShapeReflectionUtils.setValue(drawable?.constantState, "mInnerRadius", radius)
            invalidateSelf()
        }
    }

    fun setRingInnerRadiusRatio(ratio: Float) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            drawable?.innerRadiusRatio = ratio
        } else {//逻辑同上源码
            ShapeReflectionUtils.setValue(drawable?.constantState, "mInnerRadiusRatio", ratio)
            invalidateSelf()
        }
    }

    fun setRingThickness(@Px thickness: Int) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            drawable?.thickness = thickness
        } else {//逻辑同上源码
            ShapeReflectionUtils.setValue(drawable?.constantState, "mThickness", thickness)
            invalidateSelf()
        }
    }

    fun setRingThicknessRatio(ratio: Float) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            drawable?.thicknessRatio = ratio
        } else {//逻辑同上源码
            ShapeReflectionUtils.setValue(drawable?.constantState, "mThicknessRatio", ratio)
            invalidateSelf()
        }
    }
}