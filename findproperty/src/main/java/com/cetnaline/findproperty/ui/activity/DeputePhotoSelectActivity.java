package com.cetnaline.findproperty.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.ImageUploadBean;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.entity.event.UploadEvent;
import com.cetnaline.findproperty.entity.ui.EntrustAttachment;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.utils.ExternalCacheDirUtil;
import com.cetnaline.findproperty.utils.ImageUtil;
import com.cetnaline.findproperty.utils.LocationUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.RxBus;
import com.cetnaline.findproperty.utils.SchedulersCompat;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.cetnaline.findproperty.widgets.RoundProgressBarWidthNumber;
import com.cetnaline.findproperty.widgets.sharedialog.ShareDialog;
import com.jakewharton.rxbinding.view.RxView;
import com.orhanobut.logger.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import me.iwf.photopicker.PhotoPicker;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by diaoqf on 2017/3/31.
 */

public class DeputePhotoSelectActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.img_up_1)
    ImageView img_up_1;
    @BindView(R.id.img_up_2)
    ImageView img_up_2;
    @BindView(R.id.img_up_3)
    ImageView img_up_3;
    @BindView(R.id.img_up_layout_1)
    LinearLayout img_up_layout_1;
    @BindView(R.id.img_up_layout_2)
    LinearLayout img_up_layout_2;
    @BindView(R.id.img_up_layout_3)
    LinearLayout img_up_layout_3;
    @BindView(R.id.submit)
    TextView submit;

    @BindView(R.id.progress_ly1)
    RelativeLayout progress_ly1;
    @BindView(R.id.progress_ly2)
    RelativeLayout progress_ly2;
    @BindView(R.id.progress_ly3)
    RelativeLayout progress_ly3;

    @BindView(R.id.progress1)
    RoundProgressBarWidthNumber progress1;
    @BindView(R.id.progress2)
    RoundProgressBarWidthNumber progress2;
    @BindView(R.id.progress3)
    RoundProgressBarWidthNumber progress3;


    private int currentSelect;  //当前选择的是第几张图片0,1,2

//    private Uri image_uri; //当前选择图片的uri

    private CompositeSubscription mCompositeSubscription;

    //选择的图片路径
    private String[] picturePaths = new String[]{"","",""};

    private EntrustAttachment[] files;

    private List<String> delFilePaths;

    @Override
    protected int getContentViewId() {
        return R.layout.act_photo_select;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        files = new EntrustAttachment[3];
        List<EntrustAttachment> lis = (List<EntrustAttachment>) getIntent().getSerializableExtra("pics");
        if (lis != null) {
//            files = (EntrustAttachment[]) lis.toArray();
            for (int i=0;i<lis.size();i++) {
                files[i] = lis.get(i);
            }
            loadPics();
        }
        delFilePaths = new ArrayList<>();
        img_up_layout_1.setOnClickListener(this);
        img_up_layout_2.setOnClickListener(this);
        img_up_layout_3.setOnClickListener(this);
        img_up_1.setOnClickListener(this);
        img_up_2.setOnClickListener(this);
        img_up_3.setOnClickListener(this);

        mCompositeSubscription = new CompositeSubscription();

        mCompositeSubscription.add(
                RxBus.getDefault().toObservable(UploadEvent.class)
                        .sample(150,TimeUnit.MILLISECONDS)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(uploadEvent -> {
                    Logger.i("buobao:"+uploadEvent.getPercent());
                    switch (uploadEvent.getTag()) {
                        case UploadEvent.DEPUTE_UPLOAD_1:
                            progress1.setProgress(uploadEvent.getPercent());
                            break;
                        case UploadEvent.DEPUTE_UPLOAD_2:
                            progress2.setProgress(uploadEvent.getPercent());
                            break;
                        case UploadEvent.DEPUTE_UPLOAD_3:
                            progress3.setProgress(uploadEvent.getPercent());
                            break;
                    }
                }, throwable -> throwable.printStackTrace()));


        RxView.clicks(submit).throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {
                    //提交数据
                    Intent intent = new Intent();
                    intent.putExtra("pics", (Serializable) Arrays.asList(files));
                    setResult(102,intent);
                    finish();
                });
    }

    /**
     * 加载已选择的图片
     */
    private void loadPics() {
        if (files[0] != null) {
            img_up_layout_1.setVisibility(View.GONE);
            Glide.with(this).load(files[0].getPath())
                    .into(img_up_1);
        }
        if (files[1] != null) {
            img_up_layout_2.setVisibility(View.GONE);
            Glide.with(this).load(files[1].getPath())
                    .into(img_up_2);
        }
        if (files[2] != null) {
            img_up_layout_3.setVisibility(View.GONE);
            Glide.with(this).load(files[2].getPath())
                    .into(img_up_3);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (showShare){
        getMenuInflater().inflate(R.menu.photo_select_option, menu);
//        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.photo_option){
            files[0] = files[1] = files[2] = null;
            img_up_layout_1.setVisibility(View.VISIBLE);
            img_up_layout_2.setVisibility(View.VISIBLE);
            img_up_layout_3.setVisibility(View.VISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    protected void initToolbar() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener((v) -> {
            onBackPressed();
        });
        //设置标题
        toolbar.setTitle("上传证件照片");
    }

    public void onDestroy() {
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                picturePaths[currentSelect] = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS).get(0);
//                Logger.i("buobao:"+picturePaths[currentSelect]);

                ImageView target;
                String tag = "";
                switch (currentSelect) {
                    case 0:target = img_up_1;progress_ly1.setVisibility(View.VISIBLE);tag = UploadEvent.DEPUTE_UPLOAD_1;break;
                    case 1:target = img_up_2;progress_ly2.setVisibility(View.VISIBLE);tag = UploadEvent.DEPUTE_UPLOAD_2;break;
                    case 2:target = img_up_3;progress_ly3.setVisibility(View.VISIBLE);tag = UploadEvent.DEPUTE_UPLOAD_3;break;
                    default:target = img_up_1;
                }

                //上传图片  MyUtils.deleteFiles(path);
                String finalTag = tag;
                mCompositeSubscription.add(Observable.just(
                        ImageUtil.compress(ExternalCacheDirUtil.getImageCompressCacheDir(DeputePhotoSelectActivity.this),
                                picturePaths[currentSelect], 1000))
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(s -> {
                            if (s == null) {
                                toast("请选择正确的图片");
                                return;
                            }
                            mCompositeSubscription.add(ApiRequest.uploadImage(s, finalTag).subscribe(new Subscriber<ImageUploadBean>() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                    Logger.i("上传失败:"+currentSelect);
                                    toast("上传失败");

                                    progress_ly1.setVisibility(View.GONE);
                                    progress_ly2.setVisibility(View.GONE);
                                    progress_ly3.setVisibility(View.GONE);
                                }

                                @Override
                                public void onNext(ImageUploadBean imageUploadBean) {
                                    if (imageUploadBean != null) {
                                        EntrustAttachment attachment = new EntrustAttachment();
                                        switch (currentSelect) {
                                            case 0:attachment.setType("身份证正面");break;
                                            case 1:attachment.setType("身份证反面");break;
                                            case 2:attachment.setType("产证");break;
                                        }
                                        String name = imageUploadBean.getName();
                                        attachment.setExt(name.substring(name.lastIndexOf(".")+1));
                                        attachment.setFileName(name.substring(0,name.lastIndexOf(".")));
                                        attachment.setSize(imageUploadBean.getSize()+"");
                                        attachment.setContent(imageUploadBean.getId());
                                        attachment.setPath(picturePaths[currentSelect]);
                                        files[currentSelect] = attachment;

                                        Glide.with(DeputePhotoSelectActivity.this).load(s)
                                                .into(target);
                                        switch (currentSelect) {
                                            case 0:img_up_layout_1.setVisibility(View.GONE);progress_ly1.setVisibility(View.GONE);break;
                                            case 1:img_up_layout_2.setVisibility(View.GONE);progress_ly2.setVisibility(View.GONE);break;
                                            case 2:img_up_layout_3.setVisibility(View.GONE);progress_ly3.setVisibility(View.GONE);break;
                                        }
                                    }
                                }
                            }));
                        },throwable ->{
                            throwable.printStackTrace();
                            Logger.i("图片压缩失败");
                        }));



//                mCompositeSubscription.add(ApiRequest.uploadImage(picturePaths[currentSelect])
//                        .subscribe(imageUploadBean -> {
//                            if (imageUploadBean != null) {
//                                EntrustAttachment attachment = new EntrustAttachment();
//                                switch (currentSelect) {
//                                    case 0:attachment.setType("身份证正面");break;
//                                    case 1:attachment.setType("身份证反面");break;
//                                    case 2:attachment.setType("产证");break;
//                                }
//                                String name = imageUploadBean.getName();
//                                attachment.setExt(name.substring(name.lastIndexOf(".")+1));
//                                attachment.setFileName(name.substring(0,name.lastIndexOf(".")));
//                                attachment.setSize(imageUploadBean.getSize()+"");
//                                attachment.setContent(imageUploadBean.getId());
//                                files.add(currentSelect,attachment);
//                            }
//                        },throwable -> {
//                            throwable.printStackTrace();
//                            Logger.i("上传失败:"+currentSelect);
//                        }));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_up_1:
            case R.id.img_up_layout_1:
                currentSelect = 0;
                break;
            case R.id.img_up_2:
            case R.id.img_up_layout_2:
                currentSelect = 1;
                break;
            case R.id.img_up_3:
            case R.id.img_up_layout_3:
                currentSelect = 2;
                break;
        }

        //请求权限
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            PhotoPicker.builder()
                    .setPhotoCount(1)
                    .setShowCamera(true)
                    .setShowGif(true)
                    .setPreviewEnabled(false)
                    .start(this, PhotoPicker.REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setShowGif(true)
                        .setPreviewEnabled(false)
                        .start(this, PhotoPicker.REQUEST_CODE);
            } else {
                toast("未获得文件读取，请设置“允许”后尝试");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void finish() {
        MyUtils.deleteFiles(ExternalCacheDirUtil.getImageCompressCacheDir(DeputePhotoSelectActivity.this));
        super.finish();
    }

    @Override
    protected String getTalkingDataPageName() {
        return "委托照片选择";
    }
}
