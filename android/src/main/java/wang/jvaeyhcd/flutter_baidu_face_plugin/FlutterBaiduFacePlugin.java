package wang.jvaeyhcd.flutter_baidu_face_plugin;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import static android.content.Context.BATTERY_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

/** FlutterBaiduFacePlugin */
public class FlutterBaiduFacePlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  private static final String CHANNEL_METHOD_NAME = "flutter_baidu_face_plugin";
  private static final String GET_PLATFORM_VERSION = "getPlatformVersion";
  private static final String GET_BATTERY_LEVEL = "getBatteryLevel";

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), CHANNEL_METHOD_NAME);
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method == null) {
      result.notImplemented();
      return;
    }
    switch (call.method) {
      case GET_PLATFORM_VERSION: {
        result.success("Android " + Build.VERSION.RELEASE + " Brand " + Build.BRAND + " Model " + Build.MODEL);
        break;
      }
      case GET_BATTERY_LEVEL: {

        break;
      }
      default: {
        result.notImplemented();
        break;
      }
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  private int getBatteryLevel() {
    return 1;
  }
}

class FaceCallback {

  private Result result;

  public FaceCallback(Result result) {
    this.result = result;
  }

  public void success(String image) {
    Map<String, String> map = new HashMap<>();
    map.put("success", "true");
    map.put("image", image);
    result.success(map);
  }

  public void failed() {
    Map<String, String> map = new HashMap<>();
    map.put("success", "false");
    result.success(map);
  }
}