package com.asworkspace.mobile_vision_apis_kotlin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.asworkspace.mobilevisionapis.ExecuteImageTask
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), RequestListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        btnSelectImage.setOnClickListener { startSelectingImage() }
    }

    private fun startSelectingImage() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                processVisionImages(resultUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                print(error)
            }
        }
    }

    private fun processVisionImages(resultUri: Uri?) {
        ExecuteImageTask(this@MainActivity, this).execute(resultUri)
    }

    override fun onFaceProcessComplete(bitMap: Bitmap, fCount: Int) {
        imgPhoto.setImageDrawable(BitmapDrawable(resources, Bitmap.createBitmap(bitMap)))
        tvInfo.text = "Total Number of face detected is $fCount"
    }
}
