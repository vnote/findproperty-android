package com.cetnaline.findproperty.widgets;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;

import io.rong.imkit.RongContext;
import io.rong.imkit.widget.provider.CameraInputProvider;

/**
 * Created by diaoqf on 2017/4/12.
 */

public class MyCameraInputProvider extends CameraInputProvider {
    public MyCameraInputProvider(RongContext context) {
        super(context);
    }

    @Override
    public void onPluginClick(View view) {
        if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) view.getContext(), Manifest.permission.CAMERA)){
                new AlertDialog.Builder(view.getContext())
                        .setTitle("摄像头权限")
                        .setMessage("需要您开启摄像头访问权限")
                        .setPositiveButton("确定", (dialog, which) -> ActivityCompat.requestPermissions((Activity) view.getContext(), new String[]{Manifest.permission.CAMERA}, 1002)).show();
            }
        }else {
            super.onPluginClick(view);
        }
    }
}
