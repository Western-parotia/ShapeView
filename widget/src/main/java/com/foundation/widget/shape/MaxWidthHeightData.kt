package com.foundation.widget.shape

import android.view.View

class MaxWidthHeightData(private val targetView: View) {

    private var _maxWidth = -1
    var maxWidth
        get() = _maxWidth
        set(value) {
            if (_maxWidth == value) {
                return
            }
            _maxWidth = value
            targetView.requestLayout()
        }
    private var _maxHeight = -1
    var maxHeight
        get() = _maxHeight
        set(value) {
            if (_maxHeight == value) {
                return
            }
            _maxHeight = value
            targetView.requestLayout()
        }

    internal fun initWithAttr(maxWidth: Int, maxHeight: Int) {
        _maxWidth = maxWidth
        _maxHeight = maxHeight
    }
}