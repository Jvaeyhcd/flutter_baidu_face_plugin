package wang.jvaeyhcd.flutter_baidu_face_plugin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.baidu.idl.face.platform.FaceStatusNewEnum;
import com.baidu.idl.face.platform.model.ImageInfo;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wang.jvaeyhcd.flutter_baidu_face_plugin.CollectionSuccessExpActivity;
import wang.jvaeyhcd.flutter_baidu_face_plugin.FaceApplication;
import wang.jvaeyhcd.flutter_baidu_face_plugin.ui.FaceDetectActivity;
import wang.jvaeyhcd.flutter_baidu_face_plugin.ui.utils.IntentUtils;
import wang.jvaeyhcd.flutter_baidu_face_plugin.ui.widget.TimeoutDialog;

public class FaceDetectExpActivity extends FaceDetectActivity implements
        TimeoutDialog.OnTimeoutDialogClickListener {

    private TimeoutDialog mTimeoutDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 添加至销毁列表
        FaceApplication.addDestroyActivity(FaceDetectExpActivity.this,
                "FaceDetectExpActivity");
    }

    @Override
    public void onDetectCompletion(FaceStatusNewEnum status, String message,
                                   HashMap<String, ImageInfo> base64ImageCropMap,
                                   HashMap<String, ImageInfo> base64ImageSrcMap) {
        super.onDetectCompletion(status, message, base64ImageCropMap, base64ImageSrcMap);
        if (status == FaceStatusNewEnum.OK && mIsCompletion) {
            Log.i("人脸图像采集", "人脸图像采集,采集成功");
            IntentUtils.getInstance().setBitmap(mBmpStr);
            Intent intent = new Intent(FaceDetectExpActivity.this,
                    CollectionSuccessExpActivity.class);
            intent.putExtra("destroyType", "FaceDetectExpActivity");
            finish();
            startActivity(intent);
//            Intent intent = new Intent();
//            intent.putExtra("image", mBmpStr);
//            intent.putExtra("cropimage", "test");
//            intent.putExtra("success", true);
//            this.setResult(10013, intent);
//            finish();
        } else if (status == FaceStatusNewEnum.DetectRemindCodeTimeout) {
            if (mViewBg != null) {
                mViewBg.setVisibility(View.VISIBLE);
            }
            showMessageDialog();
            //            this.getIntent().putExtra("success", false);
//            this.setResult(10013, this.getIntent());
//            finish();
        }
    }

    private void showMessageDialog() {
        mTimeoutDialog = new TimeoutDialog(this);
        mTimeoutDialog.setDialogListener(this);
        mTimeoutDialog.setCanceledOnTouchOutside(false);
        mTimeoutDialog.setCancelable(false);
        mTimeoutDialog.show();
        onPause();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onRecollect() {
        if (mTimeoutDialog != null) {
            mTimeoutDialog.dismiss();
        }
        if (mViewBg != null) {
            mViewBg.setVisibility(View.GONE);
        }
        onResume();
    }

    @Override
    public void onReturn() {
        if (mTimeoutDialog != null) {
            mTimeoutDialog.dismiss();
        }
        finish();
    }
}
