package com.foundation.widget.shape

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.Px
import androidx.core.content.ContextCompat

/**
 * 可实现xml写shape背景[R.styleable.ShapeTextView]、[R.styleable.ShapeView]、[R.styleable.ShapeFrameLayout]
 *
 * 原理就是设置一个GradientDrawable背景，所以和background会互相覆盖
 *
 * shape相关属性及注释：
 * 1.参见注释声明的地方：[R.styleable.ShapeInfo_shapeSolidColor]
 * 2.点击[R.styleable.ShapeTextView_android_shape]，再点android:shape，GradientDrawable开头的styleable均是
 * 3.查看类源码：[GradientDrawable]
 */
class ShapeBuilder(private val targetView: View) {
    private var drawable: GradientDrawable? = null

    internal fun setDrawable(d: GradientDrawable?) {
        drawable = d;
    }

    /**
     * 将mPathIsDirty改为true并请求重绘
     */
    private fun invalidateSelf() {
        getDrawable().orientation = getDrawable().orientation//调用一次orientation就行了，省的反射
    }

    /**
     * 低api没有对应set方法，反射设置属性
     * 注意：这里的反射在api29以上无效，目前这几个属性都是白名单，所以不涉及反射限制
     */
    private fun setDrawableValue(failedName: String, value: Any?) {
        try {
            getDrawable().constantState?.let {
                val f = it.javaClass.getDeclaredField(failedName)
                f.isAccessible = true
                f[it] = value
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // 公共方法
    /////////////////////////////////////////////////////////////////////////////////////////////////

    fun getDrawable(): GradientDrawable {
        if (drawable == null) {
            drawable = GradientDrawable()
            drawable?.level = 10000
            //延迟调用，设置背景
            targetView.post { targetView.background = drawable }
        }
        return drawable!!
    }

    /**
     * 圆角类型
     */
    fun setShape(@ShapeDef.Shape shape: Int) {
        getDrawable().shape = shape
    }

    /**
     * 填充色
     */
    fun setSolidColorInt(@ColorInt color: Int) {
        getDrawable().setColor(color)
    }

    fun setSolidColorRes(@ColorRes color: Int) {
        setSolidColorStateList(ContextCompat.getColorStateList(targetView.context, color))
    }

    fun setSolidColorStateList(color: ColorStateList?) {
        getDrawable().color = color
    }

    /**
     * 圆角
     */
    fun setCornersRadius(@Px radius: Int) {
        getDrawable().cornerRadius = radius.toFloat()
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
        getDrawable().cornerRadii = radii
    }

    /**
     * 宽高，用处不大
     */
    fun setSize(@Px width: Int, @Px height: Int) {
        getDrawable().setSize(width, height)
    }

    /**
     * grand类型
     */
    fun setGradientType(@ShapeDef.GradientType type: Int) {
        getDrawable().gradientType = type
    }

    /**
     * 中心点，比例
     */
    fun setGradientCenter(x: Float, y: Float) {
        getDrawable().setGradientCenter(x, y)
    }

    /**
     * @param colors 渐变颜色，长度3或2：start、center、end或start、end，如[红，黄，蓝]
     * @param offsets 每个渐变的差值率，长度3或2，如：[0,0.5,1]
     */
    fun setGradientColors(@ColorInt colors: IntArray?, offsets: FloatArray? = null) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            getDrawable().setColors(colors, offsets)
        } else {
            if (offsets != null) {
                setDrawableValue("mPositions", offsets)
            }
            getDrawable().colors = colors
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
        getDrawable().orientation = orientation
    }

    /**
     * 目前代码只支持固定值，用到再说
     */
    fun setGradientRadius(@Px radius: Int) {
        getDrawable().gradientRadius = radius.toFloat()
    }

    fun setRingInnerRadius(@Px radius: Int) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            getDrawable().innerRadius = radius
        } else {//逻辑同上源码
            setDrawableValue("mInnerRadius", radius)
            invalidateSelf()
        }
    }

    fun setRingInnerRadiusRatio(ratio: Float) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            getDrawable().innerRadiusRatio = ratio
        } else {//逻辑同上源码
            setDrawableValue("mInnerRadiusRatio", ratio)
            invalidateSelf()
        }
    }

    fun setRingThickness(@Px thickness: Int) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            getDrawable().thickness = thickness
        } else {//逻辑同上源码
            setDrawableValue("mThickness", thickness)
            invalidateSelf()
        }
    }

    fun setRingThicknessRatio(ratio: Float) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            getDrawable().thicknessRatio = ratio
        } else {//逻辑同上源码
            setDrawableValue("mThicknessRatio", ratio)
            invalidateSelf()
        }
    }
}