package com.foundation.widget.shape

import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import kotlin.math.max
import kotlin.math.min

/**
 * xml shape效果的辅助类
 *
 * 自定义的实现逻辑（参考[ShapeView]）：
 * 初始化时调用[initAttrs]
 * onDraw时调用[onDraw]
 * verifyDrawable时调用[verifyDrawable]
 * getSuggestedMinimumHeight时调用[getSuggestedMinimumHeight]
 * getSuggestedMinimumWidth时调用[getSuggestedMinimumWidth]
 * 再自己加个buildShape()方法
 * 最后把attrs拷过去改成自己view即可
 *
 * 耗时测试结果：init<0.1ms，onDraw<0.1ms（view的background有优化[View.drawBackground]，会比shape多10%）
 */
class ShapeInitHelper(private val targetView: View) {
    val builder = ShapeBuilder(targetView)
    val maxWidthHeightData = MaxWidthHeightData(targetView)

    private var lastViewWidth = 0
    private var lastViewHeight = 0

    init {
        targetView.setWillNotDraw(false)//必须draw（ViewGroup.initViewGroup有设置WILL_NOT_DRAW）
    }

    /**
     * 初始化attrs固定写法（必须）：
     * mShapeHelper.initAttrs(attrs)
     */
    fun initAttrs(attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }
        val a = targetView.context.obtainStyledAttributes(attrs, R.styleable.ShapeInfo)
        initShape(a)
        initState(a)
        initMaxWidthHeight(a)
        a.recycle()
    }

    private fun initShape(a: TypedArray) {
        if (!a.hasValue(R.styleable.ShapeInfo_android_shape)) {
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
            builder.setCornersRadius(shapeTopLeftRadius, shapeTopRightRadius, shapeBottomRightRadius, shapeBottomLeftRadius)
        }

        //大小，GradientDrawable.updateGradientDrawableSize
        builder.setSize(getPx(a, R.styleable.ShapeInfo_shapeSizeWidth, builder.getDrawable().intrinsicWidth),
            getPx(a, R.styleable.ShapeInfo_shapeSizeHeight, builder.getDrawable().intrinsicHeight))

        //位置
        builder.gravity = a.getInt(R.styleable.ShapeInfo_shapeGravity, Gravity.CENTER)

        //margin
        val margin = getPx(a, R.styleable.ShapeInfo_shapeMargin)
        builder.setMargin(getPx(a, R.styleable.ShapeInfo_shapeMarginLeft, margin),
            getPx(a, R.styleable.ShapeInfo_shapeMarginTop, margin),
            getPx(a, R.styleable.ShapeInfo_shapeMarginRight, margin),
            getPx(a, R.styleable.ShapeInfo_shapeMarginBottom, margin))

        //描边，GradientDrawable.updateGradientDrawableStroke
        builder.setStroke(getPx(a, R.styleable.ShapeInfo_shapeStrokeWidth, -1),
            a.getColorStateList(R.styleable.ShapeInfo_shapeStrokeColor),
            getPx(a, R.styleable.ShapeInfo_shapeStrokeDashWidth),
            getPx(a, R.styleable.ShapeInfo_shapeStrokeDashGap))

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
        //实际上就是view的padding，还有bug，请直接使用view的padding

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
    }

    /**
     * 附加的功能selected、checked状态切换
     */
    private fun initState(a: TypedArray) {
        val dSelected = a.getDrawable(R.styleable.ShapeInfo_stateSelectedRes)
        val dChecked = a.getDrawable(R.styleable.ShapeInfo_stateCheckedRes)
        val dDefault = a.getDrawable(R.styleable.ShapeInfo_stateDefaultRes)
        if ((dSelected != null || dChecked != null) && dDefault != null) {
            val drawable = StateListDrawable().apply {
                if (dSelected != null) {
                    addState(intArrayOf(android.R.attr.state_selected), dSelected)
                }
                if (dChecked != null) {
                    addState(intArrayOf(android.R.attr.state_checked), dChecked)
                }
                addState(intArrayOf(), dDefault)
            }
            when (a.getInt(R.styleable.ShapeInfo_stateType, 0)) {
                0 -> {
                    targetView.background = drawable
                }
                1 -> {
                    setBounds(left = drawable)
                }
                2 -> {
                    setBounds(top = drawable)
                }
                3 -> {
                    setBounds(right = drawable)
                }
                4 -> {
                    setBounds(bottom = drawable)
                }
            }
        }
    }

    private fun initMaxWidthHeight(a: TypedArray) {
        maxWidthHeightData.initWithAttr(
            getPx(a, R.styleable.ShapeInfo_android_maxWidth, -1),
            getPx(a, R.styleable.ShapeInfo_android_maxHeight, -1)
        )
    }

    private fun setBounds(left: Drawable? = null, top: Drawable? = null, right: Drawable? = null, bottom: Drawable? = null) {
        (targetView as? TextView)?.let {
            val drawables = it.compoundDrawables
            it.setCompoundDrawablesWithIntrinsicBounds(left ?: drawables[0], top ?: drawables[1],
                right ?: drawables[2], bottom ?: drawables[3])
        }
    }

    /**
     * 重写onDraw固定写法（必须）：
     * super.onDraw(canvas)
     * mShapeHelper.onDraw(canvas)
     */
    fun onDraw(canvas: Canvas) {
        //计算shape的实际宽高
        var drawableWidth: Int
        var drawableHeight: Int
        var usedGravity = false
        val maxWidth = targetView.width - builder.marginRect.left - builder.marginRect.right//最大宽
        val maxHeight = targetView.height - builder.marginRect.top - builder.marginRect.bottom
        if (builder.getDrawable().intrinsicWidth >= 0 || builder.getDrawable().intrinsicHeight >= 0) {
            drawableWidth = if (builder.getDrawable().intrinsicWidth >= 0) builder.getDrawable().intrinsicWidth else targetView.width
            drawableHeight = if (builder.getDrawable().intrinsicHeight >= 0) builder.getDrawable().intrinsicHeight else targetView.height
            usedGravity = true
        } else {
            drawableWidth = maxWidth
            drawableHeight = maxHeight
        }
        if (drawableWidth > maxWidth) {
            drawableWidth = maxWidth
        }
        if (drawableWidth < 0) {
            drawableWidth = 0
        }
        if (drawableHeight > maxHeight) {
            drawableHeight = maxHeight
        }
        if (drawableHeight < 0) {
            drawableHeight = 0
        }

        //宽高变化
        if (lastViewWidth != drawableWidth || lastViewHeight != drawableHeight) {
            lastViewWidth = drawableWidth
            lastViewHeight = drawableHeight
            builder.getDrawable()
                .setBounds(0, 0, lastViewWidth, lastViewHeight)
        }

        //计算shape位置
        var translateX = 0
        var translateY = 0
        if (usedGravity) {
            when (builder.gravity) {
                Gravity.CENTER -> {
                    translateX = (targetView.width - drawableWidth) / 2
                    translateY = (targetView.height - drawableHeight) / 2
                }
                Gravity.LEFT, Gravity.START -> {
                    translateX = 0 + builder.marginRect.left
                    translateY = (targetView.height - drawableHeight) / 2
                }
                Gravity.TOP -> {
                    translateX = (targetView.width - drawableWidth) / 2
                    translateY = 0 + builder.marginRect.top
                }
                Gravity.RIGHT, Gravity.END -> {
                    translateX = targetView.width - drawableWidth - builder.marginRect.right
                    translateY = (targetView.height - drawableHeight) / 2
                }
                Gravity.BOTTOM -> {
                    translateX = (targetView.width - drawableWidth) / 2
                    translateY = targetView.height - drawableHeight - builder.marginRect.bottom
                }
                Gravity.LEFT or Gravity.TOP, Gravity.START or Gravity.TOP -> {
                    translateX = 0 + builder.marginRect.left
                    translateY = 0 + builder.marginRect.top
                }
                Gravity.RIGHT or Gravity.TOP, Gravity.END or Gravity.TOP -> {
                    translateX = targetView.width - drawableWidth - builder.marginRect.right
                    translateY = 0 + builder.marginRect.top
                }
                Gravity.LEFT or Gravity.BOTTOM -> {
                    translateX = 0 + builder.marginRect.left
                    translateY = targetView.height - drawableHeight - builder.marginRect.bottom
                }
                Gravity.RIGHT or Gravity.BOTTOM -> {
                    translateX = targetView.width - drawableWidth - builder.marginRect.right
                    translateY = targetView.height - drawableHeight - builder.marginRect.bottom
                }
            }
        } else {
            translateX = builder.marginRect.left
            translateY = builder.marginRect.top
        }

        //加上view的scroll
        translateX += targetView.scrollX
        translateY += targetView.scrollY

        //绘制上去
        if (translateX or translateY == 0) {
            builder.getDrawable()
                .draw(canvas)
        } else {
            canvas.translate(translateX.toFloat(), translateY.toFloat())
            builder.getDrawable()
                .draw(canvas)
            canvas.translate(-translateX.toFloat(), -translateY.toFloat())
        }
    }

    /**
     * 重写verifyDrawable固定写法（必须）：
     * return mShapeHelper.verifyDrawable(who, super.verifyDrawable(who))
     */
    fun verifyDrawable(who: Drawable, superBoolean: Boolean): Boolean {
        return superBoolean || (who == builder.getDrawable())
    }

    /**
     * 重写getSuggestedMinimumHeight固定写法（必须）：
     * return mShapeHelper.getSuggestedMinimumHeight(super.getSuggestedMinimumHeight())
     */
    fun getSuggestedMinimumHeight(superMinimumHeight: Int): Int {
        return max(superMinimumHeight, builder.getDrawable().minimumHeight)
    }

    /**
     * 重写getSuggestedMinimumWidth固定写法（必须）：
     * return mShapeHelper.getSuggestedMinimumWidth(super.getSuggestedMinimumWidth())
     */
    fun getSuggestedMinimumWidth(superMinimumWidth: Int): Int {
        return max(superMinimumWidth, builder.getDrawable().minimumWidth)
    }

    /**
     * 重写onMeasure固定写法（支持mim、max宽高必须）：
     * shapeHelper.getSuggestedMeasureWidthHeight(widthMeasureSpec, heightMeasureSpec) { wms, hms ->
     *     super.onMeasure(wms, hms)
     * }
     * 外包获取min、max相关见[maxWidthHeightData]
     */
    inline fun getSuggestedMeasureWidthHeight(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        callback: (widthMeasureSpec: Int, heightMeasureSpec: Int) -> Unit
    ) {
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        val newWidthMeasureSpec: Int
        if (widthMode == View.MeasureSpec.EXACTLY) {
            newWidthMeasureSpec = widthMeasureSpec
        } else {
            var newWidth = View.MeasureSpec.getSize(widthMeasureSpec)
            if (maxWidthHeightData.maxWidth >= 0) {
                newWidth = min(newWidth, maxWidthHeightData.maxWidth)
                newWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(newWidth, View.MeasureSpec.AT_MOST)
            } else {
                newWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(newWidth, widthMode)
            }
        }

        val newHeightMeasureSpec: Int
        if (heightMode == View.MeasureSpec.EXACTLY) {
            newHeightMeasureSpec = heightMeasureSpec
        } else {
            var newHeight = View.MeasureSpec.getSize(heightMeasureSpec)
            if (maxWidthHeightData.maxHeight >= 0) {
                newHeight = min(newHeight, maxWidthHeightData.maxHeight)
                newHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(newHeight, View.MeasureSpec.AT_MOST)
            } else {
                newHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(newHeight, heightMode)
            }
        }

        callback.invoke(newWidthMeasureSpec, newHeightMeasureSpec)
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