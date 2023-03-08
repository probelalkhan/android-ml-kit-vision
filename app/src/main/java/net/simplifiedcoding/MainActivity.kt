package net.simplifiedcoding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.*
import com.google.mlkit.vision.barcode.common.Barcode
import net.simplifiedcoding.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val cameraPermission = android.Manifest.permission.CAMERA
    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher = registerForActivityResult(RequestPermission()) { isGranted ->
        if (isGranted) {
            startScanner()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonOpenScanner.setOnClickListener {
            requestCameraAndStartScanner()
        }
    }

    private fun requestCameraAndStartScanner() {
        if (isPermissionGranted(cameraPermission)) {
            startScanner()
        } else {
            requestCameraPermission()
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
        ScannerActivity.startScanner(this) { barcodes ->
            barcodes.forEach { barcode ->
                when (barcode.valueType) {
                    Barcode.TYPE_URL -> {
                        binding.textViewQrType.text = "URL"
                        binding.textViewQrContent.text = barcode.url.toString()
                    }
                    Barcode.TYPE_CONTACT_INFO -> {
                        binding.textViewQrType.text = "Contact"
                        binding.textViewQrContent.text = barcode.contactInfo.toString()
                    }
                    else -> {
                        binding.textViewQrType.text = "Other"
                        binding.textViewQrContent.text = barcode.rawValue.toString()
                    }
                }
            }
        }
    }
}