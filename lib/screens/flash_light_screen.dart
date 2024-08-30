import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:google_fonts/google_fonts.dart';

class FlashLightScreen extends StatefulWidget {
  const FlashLightScreen({super.key});

  @override
  State<FlashLightScreen> createState() => _FlashLightScreenState();
}

class _FlashLightScreenState extends State<FlashLightScreen> {
  bool isOn = false;
  static const methodChannel = MethodChannel('com.example.lesson72/flashlight');
  static const eventChannel =
      EventChannel('com.example.lesson72/flashlightStream');

  Future<void> toggleFlashlight(bool isOn) async {
    try {
      await methodChannel.invokeMethod('toggleFlashlight', {'isOn': isOn});
    } on PlatformException catch (e) {
      print("Failed to toggle flashlight: '${e.message}'.");
    }
  }

  void _toggleFlashlight() {
    setState(() {
      isOn = !isOn;
    });
    toggleFlashlight(isOn);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      body: StreamBuilder<dynamic>(
        stream: eventChannel.receiveBroadcastStream(),
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            isOn = snapshot.data as bool;
          }

          return Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                GestureDetector(
                  onTap: _toggleFlashlight,
                  child: Container(
                    width: 130,
                    height: 130,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      color: Colors.white,
                      boxShadow: [
                        BoxShadow(
                          color:
                              isOn ? Colors.red : Colors.white.withOpacity(0.8),
                          spreadRadius: 18,
                          blurRadius: 7,
                          offset: const Offset(0, 3),
                        ),
                      ],
                    ),
                    child: Icon(
                      CupertinoIcons.power,
                      size: 50,
                      color: isOn ? Colors.red : Colors.black,
                    ),
                  ),
                ),
                const SizedBox(height: 50),
                Text(
                  isOn ? "Flashlight On" : "Flashlight Off",
                  style: GoogleFonts.dmSans(
                    color: isOn ? Colors.red : Colors.white,
                    fontSize: 30,
                  ),
                ),
              ],
            ),
          );
        },
      ),
    );
  }
}
