package com.cetnaline.findproperty.widgets.sharedialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.BuildConfig;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.entity.event.ShareEvent;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.RxBus;
import com.cetnaline.findproperty.utils.WeiXinUtil;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by diaoqf on 2016/8/9.
 */
public class ShareDialog {
    private final Context context;
    private View view;
    private RecyclerView rv_share;
    protected BottomSheetDialog bottomSheetDialog;

    //分先前回调  流量活动的坑爹需求
    private ShareProListener shareProListener;

    private String[] titles = new String[]{"QQ", "朋友圈" ,"微信","复制链接"};
    private int[] images =
            new int[]{R.drawable.qq_app,
            R.drawable.wx_app,
            R.drawable.wb_app,
            R.drawable.url_app};

    protected Tencent tencent;
    private Map<String, String> shareParam;

    public void setShareProListener(ShareProListener shareProListener) {
        this.shareProListener = shareProListener;
    }

    public ShareDialog(Activity context, Map<String, String> shareParam, ShareProListener listener) {
        this(context,shareParam);
        shareProListener = listener;
    }

    public ShareDialog(Activity context, Map<String, String> shareParam) {
        this.context = context;
        this.shareParam = shareParam;
        //QQ分享
        tencent = Tencent.createInstance(BuildConfig.QQ_ID, context.getApplicationContext());

        view = LayoutInflater.from(context).inflate(R.layout.layout_share, null);
        view.findViewById(R.id.cancel).setOnClickListener(v->dismiss());
        rv_share = (RecyclerView) view.findViewById(R.id.rv_share);

        rv_share.setLayoutManager(new GridLayoutManager(context, 4));

        List<ShareItem> datas = new ArrayList<>();
        for (int i=0; i<titles.length; i++){
            datas.add(new ShareItem(titles[i], images[i]));
        }

        ShareAdapter shareAdapter = new ShareAdapter(context, R.layout.item_dialog_share, datas);
        shareAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
                if (shareProListener !=null) {
                    shareProListener.proper(position);
                }
                switch (position) {
                    case 0://QQ好友
                        final Bundle params = new Bundle();

                        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareParam.get("title")+"");
                        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  shareParam.get("summary") == null ? "":shareParam.get("summary")+"");
                        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  shareParam.get("url")+"");

                        if (shareParam.get("imageUrl")!=null){
                            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareParam.get("imageUrl")+"");
                        }else {
                            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, NetContents.DEFAULT_SHARE_IMAGE);
                        }
                        tencent.shareToQQ(context, params, new IUiListener() {
                            @Override
                            public void onComplete(Object o) {
                                RxBus.getDefault().send(new ShareEvent(ShareEvent.EVENT_TYPE_SUCCESS));
                            }

                            @Override
                            public void onError(UiError uiError) {
                                RxBus.getDefault().send(new ShareEvent(ShareEvent.EVENT_TYPE_FAIL));
                            }

                            @Override
                            public void onCancel() {
                                RxBus.getDefault().send(new ShareEvent(ShareEvent.EVENT_TYPE_CANCLE));
                            }
                        });

                        break;
                    case 1://微信
                        shareWX(false);
                        break;
                    case 2://朋友圈
                        shareWX(true);
                        break;
                    case 3:
                        if (shareParam.get("url") != null) {
                            MyUtils.copyText2Clip(context,shareParam.get("url"));
                            Toast.makeText(context,"链接已复制",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context,"没有可复制的链接",Toast.LENGTH_SHORT).show();
                        }
                        dismiss();
                        break;
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
                return false;
            }
        });

        rv_share.setAdapter(shareAdapter);
    }

    public void show() {
        if (bottomSheetDialog == null) {
            bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetDialog.setContentView(view);
        }
        bottomSheetDialog.show();
    }

    public void dismiss(){
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
    }

    private void shareWX(boolean shareFriend){
        final IWXAPI iwxapi = WXAPIFactory.createWXAPI(context, BuildConfig.APP_ID_WX);
        if (!iwxapi.isWXAppInstalled()) {
            Toast.makeText(context,"微信未安装",Toast.LENGTH_SHORT).show();
            return;
        }
        iwxapi.registerApp(BuildConfig.APP_ID_WX);

        if (shareParam.get("imageUrl") != null) {
            Glide.with(context)
                    .load(shareParam.get("imageUrl"))
                    .override(150, 150)
                    .error(R.mipmap.ic_share)
                    .centerCrop()
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            wxShare(resource, shareFriend, iwxapi);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            wxShare(errorDrawable, shareFriend, iwxapi);
                        }

                    });
        }else {
            Glide.with(context)
                    .load(NetContents.DEFAULT_SHARE_IMAGE)
                    .override(150, 150)
                    .centerCrop()
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            wxShare(resource, shareFriend, iwxapi);
                        }
                    });
        }
    }

    private void wxShare(Drawable resource, boolean shareFriend, IWXAPI iwxapi) {
        Bitmap bitmap = Bitmap.createBitmap(resource.getIntrinsicWidth(),
				resource.getMinimumHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        resource.setBounds(0, 0, resource.getIntrinsicWidth(), resource.getIntrinsicHeight());
        resource.draw(canvas);
        WXWebpageObject webpage = new WXWebpageObject();
        WXMediaMessage msg = new WXMediaMessage(webpage);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        webpage.webpageUrl = shareParam.get("url");
        msg.title = shareParam.get("title");
        msg.description = shareParam.get("summary") == null ? " ":shareParam.get("summary")+"";
        msg.thumbData = WeiXinUtil.bmpToByteArray(bitmap, true);
        req.message = msg;

        req.scene =  shareFriend ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;  // : SendMessageToWX.Req.WXSceneSession;
        req.transaction = AppContents.WX_SHARE_FINISH;
        iwxapi.sendReq(req);
    }

    public class ShareItem{
        private String title;
        private int image;

        public ShareItem(String title, int image) {
            this.title = title;
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getImage() {
            return image;
        }

        public void setImage(int image) {
            this.image = image;
        }
    }


    public interface ShareProListener {
        void proper(int position);
    }
}
