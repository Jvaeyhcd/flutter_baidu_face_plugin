package wang.jvaeyhcd.flutter_baidu_face_plugin;

import android.content.Intent;
import android.os.Bundle;
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
import wang.jvaeyhcd.flutter_baidu_face_plugin.ui.FaceLivenessActivity;
import wang.jvaeyhcd.flutter_baidu_face_plugin.ui.utils.IntentUtils;
import wang.jvaeyhcd.flutter_baidu_face_plugin.ui.widget.TimeoutDialog;

public class FaceLivenessExpActivity extends FaceLivenessActivity implements
        TimeoutDialog.OnTimeoutDialogClickListener {

    private TimeoutDialog mTimeoutDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 添加至销毁列表
        FaceApplication.addDestroyActivity(FaceLivenessExpActivity.this,
                "FaceLivenessExpActivity");
    }

    @Override
    public void onLivenessCompletion(FaceStatusNewEnum status, String message,
                                     HashMap<String, ImageInfo> base64ImageCropMap,
                                     HashMap<String, ImageInfo> base64ImageSrcMap, int currentLivenessCount) {
        super.onLivenessCompletion(status, message, base64ImageCropMap, base64ImageSrcMap, currentLivenessCount);
        if (status == FaceStatusNewEnum.OK && mIsCompletion) {
            // showMessageDialog("活体检测", "检测成功");
            IntentUtils.getInstance().setBitmap(mBmpStr);
            Intent intent = new Intent(FaceLivenessExpActivity.this,
                    CollectionSuccessExpActivity.class);
            intent.putExtra("destroyType", "FaceLivenessExpActivity");
            finish();
            startActivity(intent);
        } else if (status == FaceStatusNewEnum.DetectRemindCodeTimeout) {
            if (mViewBg != null) {
                mViewBg.setVisibility(View.VISIBLE);
            }
            showMessageDialog();
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
