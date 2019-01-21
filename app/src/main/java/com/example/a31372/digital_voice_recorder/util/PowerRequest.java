package com.example.a31372.digital_voice_recorder.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.example.a31372.digital_voice_recorder.Activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class PowerRequest {
    //用户是否禁止权限
    private  boolean mShowRequestPermission = true;

    //申请权限  -- 进入主页面时申请权限
    public void init_permission(Context context, Activity activity) {
        if (getSdkVersionSix()) {
            String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO};
            List<String> mPermissionList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(context, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }

            if (mPermissionList.isEmpty()) {// 全部允许
                mShowRequestPermission = true;
            } else {//存在未允许的权限
                String[] permissionsArr = mPermissionList.toArray(new String[mPermissionList.size()]);
                ActivityCompat.requestPermissions(activity, permissionsArr, 101);
            }
        }
    }

    //判断SDK是否在6.0以上
    public boolean getSdkVersionSix() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}
