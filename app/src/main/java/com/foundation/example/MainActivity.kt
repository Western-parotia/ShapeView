package com.foundation.example

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.foundation.widget.shape.ShapeCheckedBox
import com.foundation.widget.shape.ShapeTextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tv = findViewById<ShapeTextView>(R.id.v0)
        Handler(Looper.getMainLooper()).postDelayed({
            if (isFinishing) {
                return@postDelayed
            }
            tv.buildShape()
                .apply {
                    setShape(GradientDrawable.RECTANGLE)
                    setCornersRadius(100)
                }
        }, 3000)

        val cb = findViewById<ShapeCheckedBox>(R.id.v2)
        Handler(Looper.getMainLooper()).postDelayed({
            cb.onlyCodeChecked = false
        }, 5000)
        Handler(Looper.getMainLooper()).postDelayed({
            cb.toggle()
        }, 4000)
    }
}