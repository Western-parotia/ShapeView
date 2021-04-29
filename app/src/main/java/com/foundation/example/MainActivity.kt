package com.foundation.example

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.foundation.widget.shape.ShapeFrameLayout
import com.foundation.widget.shape.ShapeTextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tv = findViewById<ShapeTextView>(R.id.tv)
        val fl = findViewById<ShapeFrameLayout>(R.id.fl)
        Handler(Looper.getMainLooper()).postDelayed({
            if (isFinishing) {
                return@postDelayed
            }
            tv.buildShape()
                .apply {
                    setShape(GradientDrawable.RECTANGLE)
                    setCornersRadius(100)
                }

            fl.setBackgroundColor(0x88888888.toInt())
        }, 3000)
    }
}