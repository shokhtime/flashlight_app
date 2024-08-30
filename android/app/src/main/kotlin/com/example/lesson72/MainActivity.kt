package com.example.lesson72

import android.os.Bundle
import android.content.Context
import android.hardware.camera2.CameraManager
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private val METHOD_CHANNEL = "com.example.lesson72/flashlight"
    private val EVENT_CHANNEL = "com.example.lesson72/flashlightStream"
    private var isFlashlightOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if flutterEngine is non-null and create channels
        flutterEngine?.dartExecutor?.binaryMessenger?.let { messenger ->
            // MethodChannel for toggling the flashlight
            MethodChannel(messenger, METHOD_CHANNEL).setMethodCallHandler { call, result ->
                if (call.method == "toggleFlashlight") {
                    val isOn = call.argument<Boolean>("isOn") ?: false
                    toggleFlashlight(isOn)
                    result.success(null)
                } else {
                    result.notImplemented()
                }
            }

            // EventChannel for streaming flashlight status
            EventChannel(messenger, EVENT_CHANNEL).setStreamHandler(object : EventChannel.StreamHandler {
                private var cameraManager: CameraManager? = null
                private var torchCallback: CameraManager.TorchCallback? = null

                override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                    cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
                    torchCallback = object : CameraManager.TorchCallback() {
                        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                            isFlashlightOn = enabled
                            events?.success(enabled)
                        }
                    }
                    cameraManager?.registerTorchCallback(torchCallback!!, null)
                }

                override fun onCancel(arguments: Any?) {
                    cameraManager?.unregisterTorchCallback(torchCallback!!)
                }
            })
        }
    }

    private fun toggleFlashlight(turnOn: Boolean) {
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, turnOn)
            isFlashlightOn = turnOn
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
