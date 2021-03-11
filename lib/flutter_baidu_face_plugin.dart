import 'dart:async';

import 'package:flutter/services.dart';

class FlutterBaiduFacePlugin {
  static const MethodChannel _channel =
  const MethodChannel('flutter_baidu_face_plugin');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> getBatteryLevel() async {
    String batterLevel = 'Unknown battery level.';
    final int result = await _channel.invokeMethod('getBatteryLevel');
    batterLevel = 'Battery level at $result %.';
    return batterLevel;
  }

  /// 初始化iOS的License
  static Future initIOSSDK(String licenseID) async {
    var arguments = Map();
    arguments['licenseID'] = licenseID;
    _channel.invokeMethod('initIOSSDK', arguments);
  }

  /// 初始化Android的License
  static Future initAndroidSDK(String licenseID, String licenseFileName) async {
    var arguments = Map();
    arguments['licenseID'] = licenseID;
    arguments['licenseFileName'] = licenseFileName;
    _channel.invokeMethod('initBaiduFaceSDK', arguments);
  }

  /// 活体检测
  static Future<LivenessResult> livenessFace({language = 'zh'}) async {
    var arguments = Map();
    arguments['language'] = language;
    final Map<dynamic, dynamic> map = await _channel.invokeMethod('livenessFace', arguments);
    return map != null ? new LivenessResult.fromMap(map) : null;
  }

  /// 采集人脸
  static Future<DetectResult> detectFace({language = 'zh'}) async {
    var arguments = Map();
    arguments['language'] = language;
    final Map<dynamic, dynamic> map = await _channel.invokeMethod('detectFace', arguments);
    return map != null ? new DetectResult.fromMap(map) : null;
  }
}

class LivenessResult {

  final String success;
  final String image;

  LivenessResult({this.success, this.image});

  factory LivenessResult.fromMap(Map<dynamic, dynamic> map) =>
      new LivenessResult(
          success: map['success'],
          image: map['image']
      );

  @override
  String toString() => 'LivenessResult: $success,$image';
}

class DetectResult {

  final String success;
  final String image;

  DetectResult({this.success, this.image});

  factory DetectResult.fromMap(Map<dynamic, dynamic> map) => new DetectResult(
      success: map['success'],
      image: map['image']
  );

  @override
  String toString() => 'DetectResult: $success,$image';
}