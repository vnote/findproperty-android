package com.cetnaline.findproperty.presenter.impl;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.load.engine.cache.DiskCache;
import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.BaseApplication;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.AppUpdateBo;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.entity.event.AppLoginEvent;
import com.cetnaline.findproperty.entity.event.DownLoadEvent;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.SettingContract;
import com.cetnaline.findproperty.ui.activity.MainTabActivity;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.JpushUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.RxBus;
import com.kyleduo.switchbutton.SwitchButton;
import com.orhanobut.logger.Logger;

import java.io.File;

import io.rong.eventbus.EventBus;
import io.rong.imlib.RongIMClient;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import zlc.season.rxdownload.RxDownload;

/**
 * Created by diaoqf on 2016/8/28.
 */
public class SettingPresenter extends BasePresenter<SettingContract.View> implements SettingContract.Presenter {
    @Override
    public void getCacheFileSize(File file) {
        Subscription subscription = Observable.just(file)
                .map(new Func1<File, Double>() {
                    @Override
                    public Double call(File file) {
                        return getDirSize(file);
                    }
                }).subscribe(new Subscriber<Double>() {
            @Override
            public void onCompleted() {}
            @Override
            public void onError(Throwable e) {}
            @Override
            public void onNext(Double aDouble) {
                iView.setCacheFileSize(MyUtils.format2(aDouble));
            }
        });
        addSubscribe(subscription);
    }

    @Override
    public void getCacheFile(File dir) {
        iView.setCacheFile(new File(dir, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR));
    }

    @Override
    public void OpenActivity(Context context, Class clz) {
        Intent intent = new Intent(context, clz);
        context.startActivity(intent);
    }

    @Override
    public void logout(Context context) {
        DataHolder.getInstance().clear();
        //清除未读消息数
//            SharedPreferencesUtil.saveInt(AppContents.RONG_CLOUD_UNREAD_COUNT,0);
        //融云下线
//        RongIMClient.getInstance().logout();
        RongIMClient.getInstance().disconnect();
        //JPush 下线
        JpushUtil.logout(context);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
//        mNotificationManager.cancelAll();
        mNotificationManager.cancel(AppContents.RONG_MESSAGE_NOTIFICATION_ID); //清除直聊通知
        mNotificationManager.cancel(AppContents.JPUSH_MESSAGE_NOTIFICATION_ID); //清除极光通知

        //发送
        EventBus.getDefault().post(new AppLoginEvent(false,2));
        iView.setFinished();
    }

    @Override
    public void exchangeLanguage(Context context) {
        BaseApplication mApp = (BaseApplication) context.getApplicationContext();
        if (mApp.getLanguage() == AppContents.LANGUAGE_CHINESE) {
            mApp.setLanguage(AppContents.LANGUAGE_ENGLISH);
        } else {
            mApp.setLanguage(AppContents.LANGUAGE_CHINESE);
        }

        //重新启用app已更新语言配置生效
        Intent intent = new Intent();
        intent.setClass(context, MainTabActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    private NotificationCompat.Builder builder;
    private NotificationManagerCompat notificationManagerCompat;

    @Override
    public void getAppVerstion(Context context, LayoutInflater inflater) {
        Subscription subscription = ApiRequest.getAppVersion()
                .subscribe((appUpdateBo)->{
                    View linearlayout;
                    if (appUpdateBo.getAndroidVerCode()> MyUtils.getAppVersionCode(context)) {
                        linearlayout = inflater.inflate(R.layout.dialog_alert, null);
                    } else {
                        linearlayout = inflater.inflate(R.layout.dialog_alert_single, null);
                    }
                    TextView title = (TextView) linearlayout.findViewById(R.id.title);
                    Dialog dialog = new AlertDialog.Builder(context).setView(linearlayout).create();
                    dialog.requestWindowFeature(1);
                    dialog.show();
                    dialog.getWindow().setWindowAnimations(R.style.AlertDialogTheme_App);
                    android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
                    p.width = MyUtils.dip2px(context,300);
                    dialog.getWindow().setAttributes(p);

                    TextView submit = (TextView) linearlayout.findViewById(R.id.submit);
                    if (appUpdateBo.getAndroidVerCode()> MyUtils.getAppVersionCode(context)) {
                        submit.setOnClickListener((view) -> {
                            String appName = "上海中原_" + appUpdateBo.getAndroidVerCode() + ".apk";
                            iView.showError("文件已开始下载，将自动安装");
                            downloadApp(appName,appUpdateBo);
                            dialog.dismiss();
                        });
                        submit.setText("立即更新");
                        TextView cancel = (TextView) linearlayout.findViewById(R.id.cancel);
                        cancel.setText("暂不更新");
                        cancel.setOnClickListener((view)->dialog.dismiss());
                        title.setText("发现新版本v"+appUpdateBo.getAndroidVerCode()+",是否更新?");
                    } else {
                        submit.setOnClickListener((view) -> {
                            dialog.dismiss();
                        });
                        title.setText("当前是最新版本");
                    }


                }, throwable -> {
                    Logger.e(throwable.getMessage());
                });
        addSubscribe(subscription);
    }

    private void downloadApp(String appName, AppUpdateBo appUpdateBo) {
        final long[] time = {0};
        RxDownload.getInstance()
                .context(BaseApplication.getContext())
                .maxRetryCount(3)
                .autoInstall(true)
                .download(appUpdateBo.getAndroidUrl(), appName, null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(downloadStatus -> {
                    if (System.currentTimeMillis() - time[0] > 1000) {
                        time[0] = System.currentTimeMillis();
                        if (builder == null) {
                            notificationManagerCompat = NotificationManagerCompat.from(BaseApplication.getContext());
                            builder = new NotificationCompat.Builder(BaseApplication.getContext());
                            builder.setContentTitle("上海中原APP更新")
                                    .setContentText("正在下载文件，请稍后...")
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setAutoCancel(false)
                                    .setWhen(System.currentTimeMillis());
                        }
                        builder.setProgress((int) downloadStatus.getTotalSize(), (int) downloadStatus.getDownloadSize(), false);
                        notificationManagerCompat.notify(123, builder.build());
                    }
                    if (downloadStatus.getDownloadSize() == downloadStatus.getTotalSize()) {
                        RxBus.getDefault().send(new DownLoadEvent((int) downloadStatus.getDownloadSize(), (int) downloadStatus.getTotalSize()));
                    }
                }, throwable -> {
                    new AlertDialog.Builder(BaseApplication.getContext())
                            .setCancelable(false)
                            .setMessage("连接服务器失败,是否重试?")
                            .setPositiveButton("确定", (dialog1, which) -> {
                                downloadApp(appName, appUpdateBo);
                                dialog1.dismiss();
                            })
                            .setNegativeButton("取消", (dialog12, which) -> {
                                dialog12.dismiss();
                            }).show();
                    throwable.printStackTrace();
                }, () -> notificationManagerCompat.cancel(123));
    }

    @Override
    public void openPushDialog(Context context,LayoutInflater inflater) {
        View linearlayout = inflater.inflate(R.layout.dialog_push_setting, null);
        SwitchButton sc1 = (SwitchButton) linearlayout.findViewById(R.id.sc_all);
        SwitchButton sc2 = (SwitchButton) linearlayout.findViewById(R.id.sc_sound);
        SwitchButton sc3 = (SwitchButton) linearlayout.findViewById(R.id.sc_vibrate);
        sc1.setChecked(!DataHolder.getInstance().isCloseNotice());
        sc2.setChecked(!DataHolder.getInstance().isCloseSound());
        sc3.setChecked(!DataHolder.getInstance().isCloseVibrate());
        sc1.setOnClickListener(vv->{
            if (!sc1.isChecked()) {
                sc2.setChecked(false);
                sc3.setChecked(false);
            } else {
                sc2.setChecked(true);
                sc3.setChecked(true);
            }
        });
        sc2.setOnClickListener(vv->{
            if (sc2.isChecked()) {
                sc1.setChecked(true);
            }
        });
        sc3.setOnClickListener(vv->{
            if (sc3.isChecked()) {
                sc1.setChecked(true);
            }
        });

        Dialog dialog = new AlertDialog.Builder(context).setView(linearlayout).create(); //对比
        dialog.requestWindowFeature(1);
        dialog.show();
        dialog.getWindow().setWindowAnimations(R.style.AlertDialogTheme_App);
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
        p.width = MyUtils.dip2px(context,300);
        dialog.getWindow().setAttributes(p);

        linearlayout.findViewById(R.id.submit).setOnClickListener((view) -> {
            DataHolder.getInstance().setCloseNotice(!sc1.isChecked());
            DataHolder.getInstance().setCloseSound(!sc2.isChecked());
            DataHolder.getInstance().setCloseVibrate(!sc3.isChecked());
            iView.setPushSettingText();
            //极光推送消息设置更新
            JpushUtil.setJpushNotificationBehavior(context);
            iView.showError("设置保存成功");
            dialog.dismiss();
        });
        linearlayout.findViewById(R.id.cancel).setOnClickListener((view)->dialog.dismiss());
    }

    /**
     * 打开提示框
     * @param context
     * @param inflater
     */
    @Override
    public void openAlert(Context context, LayoutInflater inflater, File file) {
        View linearlayout = inflater.inflate(R.layout.dialog_alert, null);
        TextView title = (TextView) linearlayout.findViewById(R.id.title);
        title.setText("确定删除缓存数据吗?");

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        dialog.setContentView(linearlayout);
        dialog.show();
//        Dialog dialog = new AlertDialog.Builder(context).setView(linearlayout).create();
//        dialog.requestWindowFeature(1);
//        dialog.show();

        dialog.getWindow().setWindowAnimations(R.style.AlertDialogTheme_App);
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
        p.width = MyUtils.dip2px(context, 300);
        dialog.getWindow().setAttributes(p);
        linearlayout.findViewById(R.id.submit).setOnClickListener((view) -> {
            if (deleteDir(file)) {
                iView.setCacheFileSize(0);
                iView.showError("清除缓存成功");
            } else {
                iView.showError("清除缓存失败");
            }
            dialog.dismiss();

        });
        linearlayout.findViewById(R.id.cancel).setOnClickListener((view) -> dialog.dismiss());
    }

    @Override
    public String getPushState() {
        String state = "";
        if (DataHolder.getInstance().isCloseNotice()) {
            return "关";
        } else {
            if (!DataHolder.getInstance().isCloseSound()) {
                state = "声音";
            }
            if (!DataHolder.getInstance().isCloseVibrate()) {
                if (state.equals("")) {
                    state = "震动";
                } else {
                    state = "开";
                }
            }
            if (!state.equals("")) {
                return state;
            }
        }
        return "不提示";
    }

    /**
     *
     * 计算文件大小
     * @param file
     * @return
     */
    private double getDirSize(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                double size = 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {
                double size = (double) file.length() / 1024 / 1024;
                return size;
            }
        } else {
            Logger.i("文件或者文件夹不存在，请检查路径是否正确！");
            return 0.0;
        }
    }

    /**
     * 删除文件
     * @param dir
     * @return
     */
    private boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
