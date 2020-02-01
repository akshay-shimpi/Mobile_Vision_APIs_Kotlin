package com.asworkspace.mobile_vision_apis_kotlin

import android.graphics.Bitmap

interface RequestListener {
    fun onFaceProcessComplete(bitMap: Bitmap, fCount: Int)
}