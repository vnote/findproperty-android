package com.cetnaline.findproperty.presenter.impl;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.BuildConfig;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.ApiResponse;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.entity.bean.QQUserInfoEntity;
import com.cetnaline.findproperty.entity.bean.SinaUserInfoBean;
import com.cetnaline.findproperty.entity.bean.UserInfoBean;
import com.cetnaline.findproperty.entity.result.BaseSingleResult;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.UserInfoSettingContract;
import com.cetnaline.findproperty.ui.activity.ScanActivity;
import com.cetnaline.findproperty.ui.activity.UserInfoSettingActivity;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.ExternalCacheDirUtil;
import com.cetnaline.findproperty.utils.ImageUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.PermissionUtils;
import com.cetnaline.findproperty.widgets.MyBottomDialog;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by diaoqf on 2016/8/28.
 */
public class UserInfoSettingPresenter extends BasePresenter<UserInfoSettingContract.View> implements UserInfoSettingContract.Presenter {
    public static final int REQUEST_PERMISSION_CODE = 10;
//    private PopupWindow pop;
    private MyBottomDialog sortWindow;

    private UserInfoSettingActivity activity;

    private Tencent tencent;
    private AuthInfo authInfo;
    private SsoHandler ssoHandler;
    private IWXAPI iwxapi;

    private Uri image_uri;
    private String picturePath;
    private String compressDir;//压缩保存目录

    private final String[] items = new String[]{"选择本地图片", "拍照"};

    private IUiListener iUiListener =new IUiListener() {
        @Override
        public void onComplete(Object o) {
            Gson gson = new Gson();
            QQUserInfoEntity jo = gson.fromJson(o.toString(),QQUserInfoEntity.class);
            int ret = jo.ret;
            if (ret == 0) {
                iView.setQQAccount(jo);
            }
            iView.dismissLoading();
        }
        @Override
        public void onError(UiError uiError) {iView.showMessage("绑定失败");iView.dismissLoading();}
        @Override
        public void onCancel() {iView.showMessage("已取消绑定");iView.dismissLoading();}
    };


    private WeiboAuthListener weiboAuthListener = new WeiboAuthListener() {
        @Override
        public void onComplete(Bundle bundle) {
            Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(bundle);
            if (accessToken.isSessionValid()) {
                ApiRequest.getSinaUserInfo(accessToken.getToken(),accessToken.getUid(),"")
                        .subscribe(new Subscriber<SinaUserInfoBean>() {
                            @Override
                            public void onCompleted() {}
                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                iView.dismissLoading();
                            }
                            @Override
                            public void onNext(SinaUserInfoBean sinaUserInfoBean) {
                                if (sinaUserInfoBean.getError() == null) {
                                    iView.setWBAccount(sinaUserInfoBean);
                                } else {
                                    iView.showMessage("获取微博账户失败");
                                }
                                iView.dismissLoading();
                            }
                        });
            } else {
                iView.dismissLoading();
                iView.showMessage("绑定失败，该账号已注册");
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            iView.dismissLoading();
            iView.showMessage("绑定失败");
        }

        @Override
        public void onCancel() {
            iView.dismissLoading();
            iView.showMessage("用户取消绑定");
        }
    };


    public UserInfoSettingPresenter(UserInfoSettingActivity activity,String[] sex){
        this.activity = activity;
        compressDir = ExternalCacheDirUtil.getImageCompressCacheDir(activity);

        ListView listView = (ListView) LayoutInflater.from(activity).inflate(R.layout.layout_bottom_list, null);
        sortWindow = new MyBottomDialog(activity);
        sortWindow.setContentView(listView);

        listView.setAdapter(new ArrayAdapter<String>(activity,R.layout.item_bottom_dialog,sex));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i < 2) {
                    iView.setSex(sex[i]);
                    Subscription subscription = ApiRequest.updateUserInfo(new HashMap(){
                        {
                            put("UserId", DataHolder.getInstance().getUserInfo().Gender);
                            put("Gender", sex[i]);
                        }
                    }).subscribe(new Action1<BaseSingleResult<UserInfoBean>>() {
                        @Override
                        public void call(BaseSingleResult<UserInfoBean> userInfoBeanBaseSingleResult) {
                            iView.showError("性别保存成功");
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            throwable.printStackTrace();
                            iView.showError("性别保存失败");
                        }
                    });
                    addSubscribe(subscription);
                }
                sortWindow.dismiss();
            }
        });
    }

    @Override
    public void showSexDialog(View parent) {
        if (sortWindow.isShowing()) {
            sortWindow.dismiss();
        } else {
            sortWindow.show();
        }
    }

    @Override
    public void startActivityForResult(Class clz, int flag) {
        activity.startActivityForResult(new Intent(activity,clz),flag);
    }

    @Override
    public void bindWB(boolean flag) {
        if (flag) {
            if (authInfo == null) {
                authInfo = new AuthInfo(activity, BuildConfig.SINA_APP_KEY, BuildConfig.SINA_CALLBACK_URL, BuildConfig.SINA_SCOPE);
            }

            if (ssoHandler == null) {
                ssoHandler = new SsoHandler(activity, authInfo);
            }
            ssoHandler.authorize(weiboAuthListener);
        }
    }

    @Override
    public void bindQQ(boolean flag) {
        if (flag) {
            if (tencent == null) {
                tencent = Tencent.createInstance(BuildConfig.QQ_ID, activity);
            }

            if (!tencent.isSessionValid())
            {
                iView.showLoading();
                tencent.login(activity, "all", null);
            }
        }
    }

    @Override
    public void bindWX(boolean flag) {
        if (flag) {
            if (iwxapi == null) {
                iwxapi = WXAPIFactory.createWXAPI(activity, BuildConfig.APP_ID_WX,true);
            }
            if (!iwxapi.isWXAppInstalled()) {
                iView.showMessage("微信未安装");
            }

            iwxapi.registerApp(BuildConfig.APP_ID_WX);
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = AppContents.WX_BIND_TAG;
            iView.showLoading();
            iwxapi.sendReq(req);
        }
    }

    /**
     * 显示选择对话框
     */
    @Override
    public void showImageDialog() {
        new AlertDialog.Builder(activity)
                .setTitle("修改头像")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                                    Intent selectPhoto = new Intent(Intent.ACTION_GET_CONTENT, null);
                                    selectPhoto.addCategory(Intent.CATEGORY_OPENABLE);
                                    selectPhoto.setType("image/*");
                                    activity.startActivityForResult(selectPhoto, UserInfoSettingActivity.SELECT_IMG);
                                } else {
                                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                    activity.startActivityForResult(intent, UserInfoSettingActivity.SELECT_IMG);
                                }
                                break;
                            case 1:
//                                if (PermissionUtils.isCameraCanUse(activity)){
//                                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 10);
//                                } else {
//                                    MyUtils.showAlertDialog(activity,"没有摄像权限,请在系统设置中打开");
//
//                                }

                                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                                    if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)){
                                        new AlertDialog.Builder(activity)
                                                .setTitle("权限申请")
                                                .setMessage("需要您开启摄像头访问权限")
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        iView.requestCameraPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CODE);
                                                    }
                                                }).show();
                                    }else {
                                        iView.requestCameraPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CODE);
                                    }
                                }else {
                                    String state = Environment.getExternalStorageState();
                                    if (state.equals(Environment.MEDIA_MOUNTED)) {
                                        try {
                                            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                                            activity.startActivityForResult(getImageByCamera, UserInfoSettingActivity.SELECT_PHOTO);
                                        } catch (Exception e) {
                                            MyUtils.showAlertDialog(activity,"没有摄像权限,请在系统设置中打开");
                                        }
                                    } else {
                                        iView.showMessage("没有储存卡");
                                    }
                                }



                                break;
                            default:
                                break;
                        }
                    }
                })
                .create()
                .show();
    }

    @Override
    public void sendAccountBind(String name,String value){
        Subscription subscription = ApiRequest.updateUserInfo(new HashMap(){
            {
                put(name,value);
                put("IsBind","1");
            }
        }).subscribe(new Action1<BaseSingleResult<UserInfoBean>>() {
            @Override
            public void call(BaseSingleResult<UserInfoBean> bean) {
                if (bean.ResultNo != -1) {
                    if ("WeiXinAccount".equals(name)) {
                        iView.updateWXState(bean.Result.WeiXinAccount);
                    }
                    if ("SinaAccount".equals(name)) {
                        iView.updateWBState(bean.Result.SinaAccount);
                    }
                    if ("QQAccount".equals(name)) {
                        iView.updateQQState(bean.Result.QQAccount);
                    }
                } else {
                    iView.showMessage("绑定失败，该账号已注册");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                iView.showMessage("网络连接失败，请检查网络");
            }
        });
        addSubscribe(subscription);
    }


    @Override
    public void qqCallback(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, iUiListener);
        Tencent.handleResultData(data, iUiListener);
    }

    @Override
    public void wbCallback(int requestCode, int resultCode, Intent data) {
        ssoHandler.authorizeCallBack(requestCode, resultCode, data);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void normalCallback(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case UserInfoSettingActivity.SELECT_PHONE:
                iView.setPhone(data.getStringExtra("phone"));
                break;
            case UserInfoSettingActivity.SET_NICK_NAME:
                iView.setName(data.getStringExtra("name"));
                break;
            case UserInfoSettingActivity.SELECT_IMG:
                image_uri = data.getData();
                if(DocumentsContract.isDocumentUri(activity, image_uri)) {
                    String wholeID = DocumentsContract.getDocumentId(image_uri);
                    String id = wholeID.split(":")[1];
                    String[] column = {MediaStore.Images.Media.DATA};
                    String sel = MediaStore.Images.Media._ID + "=?";
                    Cursor cursor1 = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
                            sel, new String[]{id}, null);
                    int columnIndex1 = cursor1.getColumnIndex(column[0]);
                    if (cursor1.moveToFirst()) {
                        picturePath = cursor1.getString(columnIndex1);
                    }
                }else {
                    if (image_uri.toString().startsWith("file")) {
                        picturePath = image_uri.toString().replace("file://", "");
                    } else {
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        ContentResolver cr = activity.getContentResolver();
                        Cursor cursor2 = cr.query(image_uri,
                                filePathColumn, null, null, null);
                        if (cursor2 != null) {
                            cursor2.moveToFirst();
                            int columnIndex2 = cursor2.getColumnIndex(filePathColumn[0]);
                            picturePath = cursor2.getString(columnIndex2);
                            cursor2.close();
                        }
                    }
                }
                loadImage();
                break;
            case UserInfoSettingActivity.SELECT_PHOTO:
                Bundle extras = data.getExtras();
                Bitmap myBitmap = (Bitmap) extras.get("data");
                image_uri  = Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), myBitmap, null,null));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                String photoData = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                uploadImage(photoData);
                break;
        }
    }

    private void uploadImage(String img){
        iView.showLoading(true);
        Subscription subscription = ApiRequest.updateUserImg(img)
                .subscribe(new Subscriber<ApiResponse<UserInfoBean>>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        iView.showLoading(false);
                        iView.showError("头像上传失败");
                    }

                    @Override
                    public void onNext(ApiResponse<UserInfoBean>integerBaseSingleResult) {
                        iView.setImage(image_uri.toString());
                        iView.showLoading(false);
                        iView.showError("头像上传成功");
                    }
                });
        addSubscribe(subscription);
    }

    private void loadImage() {
        Subscription subscription = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    String s = ImageUtil.compress(compressDir, picturePath, 200);
                    if (s == null) {
                        iView.showMessage("请选择正确的图片");
                        return;
                    }
                    subscriber.onNext(Bitmap2StrByBase64(s));
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((s)->{
                    Logger.i("buobao to save path:"+s);
                    uploadImage(s);
                });
        addSubscribe(subscription);
    }

    /**
     * 图片转化为byte
     */
    private String Bitmap2StrByBase64(String path) {
        byte[] data = null;
        FileInputStream input = null;
        try {
            input = new FileInputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();
            output.close();
            input.close();
        } catch (FileNotFoundException ex1) {
            ex1.printStackTrace();
        } catch (IOException ex1) {
            ex1.printStackTrace();
        }
        return Base64.encodeToString(data, Base64.DEFAULT);
    }
}
