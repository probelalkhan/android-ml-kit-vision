package net.simplifiedcoding.mlkitsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.*
import net.simplifiedcoding.mlkitsample.databinding.ActivityMainBinding
import net.simplifiedcoding.mlkitsample.facedetector.FaceDetectionActivity
import net.simplifiedcoding.mlkitsample.qrscanner.ScannerActivity

class MainActivity : AppCompatActivity() {

    private val cameraPermission = android.Manifest.permission.CAMERA
    private lateinit var binding: ActivityMainBinding
    private var action = Action.QR_SCANNER

    private val requestPermissionLauncher =
        registerForActivityResult(RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonOpenScanner.setOnClickListener {
            this.action = Action.QR_SCANNER
            requestCameraAndStart()
        }

        binding.buttonFaceDetect.setOnClickListener {
            this.action = Action.FACE_DETECTION
            requestCameraAndStart()
        }
    }

    private fun requestCameraAndStart() {
        if (isPermissionGranted(cameraPermission)) {
            startCamera()
        } else {
            requestCameraPermission()
        }
    }

    private fun startCamera() {
        when (action) {
            Action.QR_SCANNER -> startScanner()
            Action.FACE_DETECTION -> FaceDetectionActivity.startActivity(this)
        }
    }

    private fun requestCameraPermission() {
        when {
            shouldShowRequestPermissionRationale(cameraPermission) -> {
                cameraPermissionRequest(
                    positive = { openPermissionSetting() }
                )
            }
            else -> {
                requestPermissionLauncher.launch(cameraPermission)
            }
        }
    }

    private fun startScanner() {
        ScannerActivity.startScanner(this)
    }
}