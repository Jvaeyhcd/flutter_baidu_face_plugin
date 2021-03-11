package wang.jvaeyhcd.flutter_baidu_face_plugin;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;

import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceEnvironment;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.LivenessTypeEnum;
import com.baidu.idl.face.platform.listener.IInitCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import wang.jvaeyhcd.flutter_baidu_face_plugin.constant.ArgumentConstant;
import wang.jvaeyhcd.flutter_baidu_face_plugin.ui.FaceDetectActivity;
import wang.jvaeyhcd.flutter_baidu_face_plugin.ui.FaceSDKResSettings;

import static android.content.Context.BATTERY_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

/**
 * FlutterBaiduFacePlugin
 */
public class FlutterBaiduFacePlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, ActivityResultListener {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;

    private static Activity activity;

    private static final String CHANNEL_METHOD_NAME = "flutter_baidu_face_plugin";
    private static final String GET_PLATFORM_VERSION = "getPlatformVersion";
    private static final String GET_BATTERY_LEVEL = "getBatteryLevel";
    private static final String LIVENESS_FACE = "livenessFace";
    private static final String DETECT_FACE = "detectFace";
    private static final String INIT_BAIDU_FACE_SDK = "initBaiduFaceSDK";

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
                result.success("" + getBatteryLevel());
                break;
            }
            case DETECT_FACE: {
                detectFace(call.hasArgument(ArgumentConstant.LANGUAGE) ? call.<String>argument(ArgumentConstant.LANGUAGE) : null);
                break;
            }
            case LIVENESS_FACE: {
                livenessFace(call.hasArgument(ArgumentConstant.LANGUAGE) ? call.<String>argument(ArgumentConstant.LANGUAGE) : null);
                break;
            }
            case INIT_BAIDU_FACE_SDK: {
                String licenseID = call.hasArgument(ArgumentConstant.LICENSE_ID) ? call.<String>argument(ArgumentConstant.LICENSE_ID) : null;
                String licenseFileName = call.hasArgument(ArgumentConstant.LICENSE_FILENAME) ? call.<String>argument(ArgumentConstant.LICENSE_FILENAME) : null;
                initBaiduFaceSDK(licenseID, licenseFileName);
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

    private void initBaiduFaceSDK(String licenseID, String licenseFileName) {
        if (activity == null) {
            // 初始化失败
            return;
        }
        FaceSDKManager.getInstance().initialize(
                activity.getApplicationContext(), licenseID, licenseFileName, new IInitCallback() {
                    @Override
                    public void initSuccess() {
                        Log.e("DEBUG", "Success");
                    }

                    @Override
                    public void initFailure(int i, String s) {
                        Log.e("DEBUG", "Failed:" + s);
                    }
                });

        FaceConfig config = FaceSDKManager.getInstance().getFaceConfig();
        // 设置可检测的最小人脸阈值
        config.setMinFaceSize(FaceEnvironment.VALUE_MIN_FACE_SIZE);
        // 设置可检测到人脸的阈值
        config.setNotFaceValue(FaceEnvironment.VALUE_NOT_FACE_THRESHOLD);
        // 设置模糊度阈值
        config.setBlurnessValue(FaceEnvironment.VALUE_BLURNESS);
        // 设置光照阈值（范围0-255）
        config.setBrightnessValue(FaceEnvironment.VALUE_BRIGHTNESS);
        // 设置遮挡阈值
        config.setOcclusionValue(FaceEnvironment.VALUE_OCCLUSION);
        // 设置人脸姿态角阈值
        config.setHeadPitchValue(FaceEnvironment.VALUE_HEAD_PITCH);
        config.setHeadYawValue(FaceEnvironment.VALUE_HEAD_YAW);
        // 设置闭眼阈值
        config.setEyeClosedValue(FaceEnvironment.VALUE_CLOSE_EYES);
        // 设置图片缓存数量
        config.setCacheImageNum(FaceEnvironment.VALUE_CACHE_IMAGE_NUM);
        // 设置活体动作，通过设置list，LivenessTypeEunm.Eye, LivenessTypeEunm.Mouth,
        // LivenessTypeEunm.HeadUp, LivenessTypeEunm.HeadDown, LivenessTypeEunm.HeadLeft,
        // LivenessTypeEunm.HeadRight, LivenessTypeEunm.HeadLeftOrRight
        List<LivenessTypeEnum> livenessList = new ArrayList<>();
        livenessList.add(LivenessTypeEnum.Eye);
        config.setLivenessTypeList(livenessList);
        // 设置动作活体是否随机
        config.setLivenessRandom(true);
        // 设置开启提示音
        config.setSound(true);
        // 原图缩放系数
        config.setScale(FaceEnvironment.VALUE_SCALE);
        // 抠图高的设定，为了保证好的抠图效果，我们要求高宽比是4：3，所以会在内部进行计算，只需要传入高即可
        config.setCropHeight(FaceEnvironment.VALUE_CROP_HEIGHT);
        // 加密类型，0：Base64加密，上传时image_sec传false；1：百度加密文件加密，上传时image_sec传true
        config.setSecType(FaceEnvironment.VALUE_SEC_TYPE);
        FaceSDKManager.getInstance().setFaceConfig(config);
        // 初始化资源文件
        FaceSDKResSettings.initializeResId();
    }

    /**
     * 活体人脸
     */
    private void livenessFace(String language) {

    }

    /**
     * 采集人脸
     */
    private void detectFace(String language) {
        Intent intent = new Intent(activity, FaceDetectActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

    }

    @Override
    public void onDetachedFromActivity() {

    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }

    class FaceCallback {

        private Result result;

        public FaceCallback(Result result) {
            this.result = result;
        }

        public void success(String image) {
            Map<String, String> map = new HashMap<>();
            map.put(ArgumentConstant.SUCCESS, "true");
            map.put(ArgumentConstant.IMAGE, image);
            result.success(map);
        }

        public void failed() {
            Map<String, String> map = new HashMap<>();
            map.put(ArgumentConstant.SUCCESS, "false");
            result.success(map);
        }
    }
}