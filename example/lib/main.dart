import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_plugin_demo/flutter_plugin_demo.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String _batteryLevel = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    String platformVersion;
    try {
      platformVersion = await FlutterPluginDemo.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }
    if (!mounted) return;
    setState(() {
      _platformVersion = platformVersion;
    });
  }

  // 获取电量
  Future<void> getBatteryLevel() async {
    String batteryLevel;
    try{
      int result = await FlutterPluginDemo.getBatteryLevel() ;
      batteryLevel = ' $result % .';
    } on PlatformException {
      batteryLevel = "Failed to get batteryLevel";
    }
    if (!mounted) return;
    setState(() {
      _batteryLevel = batteryLevel;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              Text('Running on: $_platformVersion\n'),
              const SizedBox(height: 20,),
              Text('battery level: $_batteryLevel\n'),
              ElevatedButton(onPressed: (){
                getBatteryLevel();
                  }, child: const Text('click! get battery level'))
            ],
          ),
        ),
      ),
    );
  }
}
