package com.emptyshop.qrcodereader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode

import com.google.zxing.integration.android.IntentIntegrator

class MainActivity : AppCompatActivity (), View.OnClickListener {
    private lateinit var scanBtnZXing : Button
    private lateinit var messageText : TextView
    private lateinit var messageFormat : TextView
    private lateinit var codeScanner : CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

        codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS

        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        scannerView.isVisible = false

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread{
                scannerView.isVisible = false
                messageText.text = it.text
                messageFormat.text = it.barcodeFormat.toString()
            }
        }

        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread{
                Toast.makeText(this, "Camera Initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
                messageText.text = String()
                messageFormat.text = String()
                scannerView.isVisible = false
            }
        }

        // referencing and initializing
        // the button and textviews
        scanBtnZXing = findViewById(R.id.scanBtnZXing)
        messageText = findViewById(R.id.textContent)
        messageFormat = findViewById(R.id.textFormat)

        // adding listener to the button for ZXing
        scanBtnZXing.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onClick(v: View?) {
        // we need to create the object
        // of IntentIntegrator class
        // which is the class of ZXing QR library
        val intentIntegrator = IntentIntegrator(this)
        intentIntegrator.setPrompt("Scan a barcode or QR Code")
        intentIntegrator.setOrientationLocked(true)
        intentIntegrator.initiateScan()
    }

    fun codeScannerClicked(v : View){
        findViewById<CodeScannerView>(R.id.scanner_view).isVisible = true
        codeScanner.startPreview()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        // if the intentResult is null then
        // toast a message as "cancelled"
        if (intentResult != null){
            if (intentResult.contents.isNullOrEmpty()){
                Toast.makeText(baseContext, "Cancelled", Toast.LENGTH_SHORT).show()
            }
            else{
                // if the intentResult is not null we'll set
                // the content and format of scan message
                messageText.text = intentResult.contents
                messageFormat.text = intentResult.formatName
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}