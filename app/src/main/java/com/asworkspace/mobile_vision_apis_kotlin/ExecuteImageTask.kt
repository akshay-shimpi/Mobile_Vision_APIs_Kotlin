package com.asworkspace.mobilevisionapis

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import com.asworkspace.mobile_vision_apis_kotlin.RequestListener
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.FaceDetector
import java.io.IOException

class ExecuteImageTask(private val mContext: Context, private val mListener: RequestListener) :
    AsyncTask<Uri, Void, Bitmap>() {
    private val progressDialog: ProgressDialog?
    private var mTotalFaceCount: Int = 0

    init {
        progressDialog = ProgressDialog(mContext)
    }

    override fun onPreExecute() {
        progressDialog!!.setMessage("Processing Image...")
        progressDialog.show()
    }

    override fun doInBackground(vararg strings: Uri): Bitmap? {
        try {
            return processVisionImages(strings[0])
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    override fun onPostExecute(bitmap: Bitmap) {
        if (progressDialog != null && progressDialog.isShowing) {
            progressDialog.dismiss()
        }
        mListener.onFaceProcessComplete(bitmap, mTotalFaceCount)
    }


    @Throws(IOException::class)
    private fun processVisionImages(uri: Uri): Bitmap? {
        val myBitmap = MediaStore.Images.Media.getBitmap(mContext.contentResolver, uri)

        val myRectPaint = Paint()
        myRectPaint.strokeWidth = 10f
        myRectPaint.color = Color.RED
        myRectPaint.style = Paint.Style.STROKE

        val tempBitmap = Bitmap.createBitmap(myBitmap.width, myBitmap.height, Bitmap.Config.RGB_565)
        val tempCanvas = Canvas(tempBitmap)
        tempCanvas.drawBitmap(myBitmap, 0f, 0f, null)

        val faceDetector = FaceDetector.Builder(mContext).setTrackingEnabled(false)
            .build()
        if (!faceDetector.isOperational) {
            AlertDialog.Builder(mContext)
                .setMessage("Could not set up the face detector! please try to update google play services.")
                .show()
            return null
        }

        val frame = Frame.Builder().setBitmap(myBitmap).build()
        val faces = faceDetector.detect(frame)

        for (i in 0 until faces.size()) {
            val thisFace = faces.valueAt(i)
            val x1 = thisFace.position.x
            val y1 = thisFace.position.y
            val x2 = x1 + thisFace.width
            val y2 = y1 + thisFace.height
            tempCanvas.drawRoundRect(RectF(x1, y1, x2, y2), 2f, 2f, myRectPaint)
        }
        mTotalFaceCount = faces.size()
        return tempBitmap
    }
}
