package com.e15.alarmnats.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.TextView

import com.e15.alarmnats.R
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

import java.io.IOException

class QRscanActivity : AppCompatActivity() {
    //    public static final String EXTRA_MESSAGE = "my message";
    internal lateinit var surfaceView: SurfaceView
    internal lateinit var textView: TextView
    internal lateinit var label: TextView

    internal lateinit var cameraSource: CameraSource
    internal lateinit var barcodeDetector: BarcodeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscan)

        surfaceView = findViewById<View>(R.id.camerapreview) as SurfaceView
        textView = findViewById<View>(R.id.textView) as TextView


        if (!intent.extras!!.getBoolean("isSettingNewAlarm")) {
            label = findViewById(R.id.qrMsg)
            label.text = intent.extras!!.getString("label")
        }

        barcodeDetector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build()
        cameraSource = CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(500, 500)
                .setAutoFocusEnabled(true).build()

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
                try {
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    //                    e.printStackTrace();
                }

            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val codes = detections.detectedItems
                if (codes.size() != 0) {
                    val thisCode = codes.valueAt(0).displayValue
                    textView.post { textView.text = thisCode }

                    if (intent.extras!!.getBoolean("isSettingNewAlarm")) {
                        val returnIntent = Intent()
                        returnIntent.putExtra("code", codes.valueAt(0).displayValue)
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    } else if (intent.extras!!.getString("answer") == thisCode) {
                        println("scan finished!!")
                        val returnIntent = Intent()
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        //
        super.onDestroy()
    }
}