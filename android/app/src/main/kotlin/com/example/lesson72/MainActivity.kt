package com.example.lesson72

import android.os.Bundle
import android.content.Context
import android.hardware.camera2.CameraManager
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.example.lesson72/flashlight"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // flutterEngine ning null emasligini tekshiramiz
        flutterEngine?.let {
            MethodChannel(it.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
                if (call.method == "toggleFlashlight") {
                    val isOn = call.argument<Boolean>("isOn")
                    if (isOn != null) {
                        toggleFlashlight(isOn)
                        result.success(null)
                    } else {
                        result.error("INVALID_ARGUMENT", "Argument 'isOn' is null", null)
                    }
                } else {
                    result.notImplemented()
                }
            }
        }
    }

    private fun toggleFlashlight(turnOn: Boolean) {
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, turnOn)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
