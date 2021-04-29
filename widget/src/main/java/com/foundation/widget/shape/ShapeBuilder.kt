package com.foundation.widget.shape

import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.*
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
 * 2.gradientRadius：原xml支持比例，待开发
 */
class ShapeBuilder(private val targetView: View) {
    private var drawable: GradientDrawable? = null
        @NonNull
        get() {
            if (field == null) {
                field = GradientDrawable()
                //延迟调用，设置背景
                targetView.post { targetView.background = drawable }
            }
            return field
        }

    @IntDef(GradientDrawable.RECTANGLE, GradientDrawable.OVAL, GradientDrawable.LINE, GradientDrawable.RING)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Shape

    @IntDef(GradientDrawable.LINEAR_GRADIENT, GradientDrawable.RADIAL_GRADIENT, GradientDrawable.SWEEP_GRADIENT)
    @Retention(AnnotationRetention.SOURCE)
    annotation class GradientType

    /**
     * 自定义view初始化attrs调用
     */
    internal fun initAttrs(attrs: AttributeSet?) {
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
        setShape(shape)

        //填充色，GradientDrawable.updateGradientDrawableSolid
        setSolidColorStateList(a.getColorStateList(R.styleable.ShapeInfo_shapeSolidColor/*mGd?.color，api24*/))

        //圆角，GradientDrawable.updateDrawableCorners
        val radius = getPx(a, R.styleable.ShapeInfo_shapeCornersRadius/*mGd?.cornerRadius，api24*/)
        setCornersRadius(radius)
        val shapeTopLeftRadius = getPx(a, R.styleable.ShapeInfo_shapeCornersTopLeftRadius, radius)
        val shapeTopRightRadius = getPx(a, R.styleable.ShapeInfo_shapeCornersTopRightRadius, radius)
        val shapeBottomLeftRadius = getPx(a, R.styleable.ShapeInfo_shapeCornersBottomLeftRadius, radius)
        val shapeBottomRightRadius = getPx(a, R.styleable.ShapeInfo_shapeCornersBottomRightRadius, radius)
        if (shapeTopLeftRadius != radius || shapeTopRightRadius != radius ||
            shapeBottomLeftRadius != radius || shapeBottomRightRadius != radius) {
            setCornersRadius(shapeTopLeftRadius, shapeTopRightRadius, shapeBottomLeftRadius, shapeBottomRightRadius)
        }

        //大小，GradientDrawable.updateGradientDrawableSize
        setSize(getPx(a, R.styleable.ShapeInfo_shapeSizeWidth, drawable!!.intrinsicWidth),
            getPx(a, R.styleable.ShapeInfo_shapeSizeHeight, drawable!!.intrinsicHeight))

        //渐变，GradientDrawable.updateGradientDrawableGradient
        if (a.hasValue(R.styleable.ShapeInfo_shapeGradientType)) {
            setGradientType(a.getInt(R.styleable.ShapeInfo_shapeGradientType, GradientDrawable.LINEAR_GRADIENT/*mGd?.gradientType，api24*/))
            val shapeGradientCenterX = getFloatOrFraction(a, R.styleable.ShapeInfo_shapeGradientCenterX, 0.5f/*mGd?.gradientCenterX，api24*/)
            val shapeGradientCenterY = getFloatOrFraction(a, R.styleable.ShapeInfo_shapeGradientCenterY, 0.5f/*mGd?.gradientCenterY，api24*/)
            setGradientCenter(shapeGradientCenterX, shapeGradientCenterY)
            //val colors = mGd?.colors，api24
            //低api有bug，set方法和solidColor冲突
            if (a.hasValue(R.styleable.ShapeInfo_shapeGradientStartColor)) {
                val shapeGradientStartColor = getColor(a, R.styleable.ShapeInfo_shapeGradientStartColor)
                val hasCenterColor = a.hasValue(R.styleable.ShapeInfo_shapeGradientCenterColor)
                val shapeGradientCenterColor = getColor(a, R.styleable.ShapeInfo_shapeGradientCenterColor)
                val shapeGradientEndColor = getColor(a, R.styleable.ShapeInfo_shapeGradientEndColor)
                if (hasCenterColor) {
                    setGradientColors(intArrayOf(shapeGradientStartColor, shapeGradientCenterColor, shapeGradientEndColor),
                        floatArrayOf(0f, if (shapeGradientCenterX != 0.5f) shapeGradientCenterX else shapeGradientCenterY, 1f))
                } else {
                    setGradientColors(intArrayOf(shapeGradientStartColor, shapeGradientEndColor))
                }
            }
            setGradientAngle(a.getInt(R.styleable.ShapeInfo_shapeGradientAngle, 0/*预览图bug，默认其实就是左右*/))
            //目前代码只支持固定值，用到再说
            setGradientRadius(getPx(a, R.styleable.ShapeInfo_shapeGradientRadius))
        }

        //padding，GradientDrawable.updateGradientDrawablePadding
        val shapePadding = getPx(a, R.styleable.ShapeInfo_shapePadding)
        setPadding(getPx(a, R.styleable.ShapeInfo_shapePaddingLeft, shapePadding),
            getPx(a, R.styleable.ShapeInfo_shapePaddingTop, shapePadding),
            getPx(a, R.styleable.ShapeInfo_shapePaddingRight, shapePadding),
            getPx(a, R.styleable.ShapeInfo_shapePaddingBottom, shapePadding))

        //圆环相关，GradientDrawable.updateStateFromTypedArray
        if (shape == GradientDrawable.RING) {
            val shapeInnerRadius = getPx(a, R.styleable.ShapeInfo_shapeInnerRadius, -1/*mGd?.innerRadius，api29*/)
            val shapeInnerRadiusRatio = a.getFloat(R.styleable.ShapeInfo_shapeInnerRadiusRatio, 3f /*mGd?.innerRadiusRatio，api29*/)
            val shapeThickness = getPx(a, R.styleable.ShapeInfo_shapeThickness, -1/*mGd?.thickness，api29*/)
            val shapeThicknessRatio = a.getFloat(R.styleable.ShapeInfo_shapeThicknessRatio, 9f/*mGd?.thicknessRatio，api29*/)
            setRingInnerRadius(shapeInnerRadius)
            if (shapeInnerRadius == -1) {
                setRingInnerRadiusRatio(shapeInnerRadiusRatio)
            }
            setRingThickness(shapeThickness)
            if (shapeThickness == -1) {
                setRingThicknessRatio(shapeThicknessRatio)
            }
        }

        targetView.background = drawable

        a.recycle()
    }

    /**
     * 自定义view重写setBackground调用
     */
    internal fun setBackground(background: Drawable?) {
        drawable = if (background is GradientDrawable) background else null
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

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // set方法
    /////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 圆角类型
     */
    fun setShape(@Shape shape: Int) {
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
    fun setCornersRadius(radius: Int) {
        drawable?.cornerRadius = radius.toFloat()
    }

    /**
     * 注意顺序
     */
    fun setCornersRadius(topLeftRadius: Int = 0, topRightRadius: Int = 0, bottomRightRadius: Int = 0, bottomLeftRadius: Int = 0) {
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
    fun setSize(width: Int, height: Int) {
        drawable?.setSize(width, height)
    }

    /**
     * grand类型
     */
    fun setGradientType(@GradientType type: Int) {
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
                ReflectionUtils.setValue(drawable?.constantState, "mPositions", offsets)
            }
            drawable?.colors = colors
        }
    }

    fun setGradientAngle(angle: Int) {
        setOrientation(when ((angle % 360) / 45) {
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
    fun setGradientRadius(radius: Int) {
        drawable?.gradientRadius = radius.toFloat()
    }

    fun setPadding(@Px left: Int, @Px top: Int, @Px right: Int, @Px bottom: Int) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            drawable?.setPadding(left, top, right, bottom)
        } else {//逻辑同上面源码
            val rect = ReflectionUtils.setRect(
                drawable?.constantState, "mPadding",
                Rect(left, top, right, bottom)
            )
            ReflectionUtils.setValue(drawable, "mPadding", rect)
            drawable?.invalidateSelf()
        }
    }

    fun setRingInnerRadius(radius: Int) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            drawable?.innerRadius = radius
        } else {//逻辑同上源码
            ReflectionUtils.setValue(drawable?.constantState, "mInnerRadius", radius)
            ReflectionUtils.setValue(drawable, "mPathIsDirty", true)
            drawable?.invalidateSelf()
        }
    }

    fun setRingInnerRadiusRatio(ratio: Float) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            drawable?.innerRadiusRatio = ratio
        } else {//逻辑同上源码
            ReflectionUtils.setValue(drawable?.constantState, "mInnerRadiusRatio", ratio)
            ReflectionUtils.setValue(drawable, "mPathIsDirty", true)
            drawable?.invalidateSelf()
        }
    }

    fun setRingThickness(thickness: Int) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            drawable?.thickness = thickness
        } else {//逻辑同上源码
            ReflectionUtils.setValue(drawable?.constantState, "mThickness", thickness)
            ReflectionUtils.setValue(drawable, "mPathIsDirty", true)
            drawable?.invalidateSelf()
        }
    }

    fun setRingThicknessRatio(ratio: Float) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            drawable?.thicknessRatio = ratio
        } else {//逻辑同上源码
            ReflectionUtils.setValue(drawable?.constantState, "mThicknessRatio", ratio)
            ReflectionUtils.setValue(drawable, "mPathIsDirty", true)
            drawable?.invalidateSelf()
        }
    }
}