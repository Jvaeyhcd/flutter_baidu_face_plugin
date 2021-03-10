import 'dart:async';

import 'package:flutter/services.dart';

class FlutterBaiduFacePlugin {
  static const MethodChannel _channel =
  const MethodChannel('flutter_baidu_face_plugin');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  Future<String> getBatteryLevel() async {
    String batterLevel = 'Unknown battery level.';
    final int result = await _channel.invokeMethod('getBatteryLevel');
    batterLevel = 'Battery level at $result %.';
    return batterLevel;
  }

  /// 初始化iOS的License
  Future initIOSSDK(String licenceId) async {
    var arguments = Map();
    arguments['licenseId'] = licenceId;
    _channel.invokeMethod('initIOSSDK', arguments);
  }

  /// 活体检测
  Future<LivenessResult> liveness({language = 'zh'}) async {
    var arguments = Map();
    arguments['language'] = language;
    final Map<dynamic, dynamic> map = await _channel.invokeMethod('liveness', arguments);
    return map != null ? new LivenessResult.fromMap(map) : null;
  }

  /// 采集人脸
  Future<DetectResult> detect({language = 'zh'}) async {
    var arguments = Map();
    arguments['language'] = language;
    final Map<dynamic, dynamic> map = await _channel.invokeMethod('detect', arguments);
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