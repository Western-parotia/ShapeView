package com.foundation.widget.shape;

import android.graphics.drawable.GradientDrawable;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * kotlin的IntDef无效，所以使用java写
 */
public class ShapeDef {
    @IntDef({GradientDrawable.RECTANGLE, GradientDrawable.OVAL, GradientDrawable.LINE, GradientDrawable.RING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Shape {
    }

    @IntDef({GradientDrawable.LINEAR_GRADIENT, GradientDrawable.RADIAL_GRADIENT, GradientDrawable.SWEEP_GRADIENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GradientType {
    }
}
