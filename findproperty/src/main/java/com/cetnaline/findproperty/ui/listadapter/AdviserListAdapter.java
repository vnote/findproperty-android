package com.cetnaline.findproperty.ui.listadapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.db.entity.GScope;
import com.cetnaline.findproperty.entity.bean.StaffListBean;
import com.cetnaline.findproperty.ui.activity.AdviserListActivity;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.glide.GlideLoad;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by diaoqf on 2016/7/28.
 */
public class AdviserListAdapter extends RecyclerView.Adapter<AdviserListAdapter.ViewHolder> {

    private List<StaffListBean> adviserList;
    /**
     * item 布局
     */
    private int rowLayout;
    private AdviserListActivity mAct;

    public AdviserListAdapter(List<StaffListBean> adviserList, int rowLayout, AdviserListActivity act) {
        this.adviserList = adviserList;
        this.rowLayout = rowLayout;
        this.mAct = act;
    }

    public List<StaffListBean> getAdviserList() {
        return adviserList;
    }

    /**
     * 清除数据
     */
    public void clear() {
        adviserList.clear();
        this.notifyDataSetChanged();
    }

    /**
     * 添加数据
     * @param list
     */
    public void add(List<StaffListBean> list) {
        if (list != null && list.size() > 0) {
            this.adviserList.addAll(list);
            this.notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final StaffListBean adviser = adviserList.get(position);
        holder.name.setText(adviser.CnName);
        holder.content.setText(adviser.StoreName);
        mAct.getCompositeSubscription().add(Observable.just(adviser.GscopeID)
                .map((s)-> {
                    String str = "";
                    if (s == null || "".equals(s)) {
                        return "";
                    }
                    GScope sub = DbUtil.getGScopeById(Integer.parseInt(s));
                    if (sub != null) {
                        GScope par = DbUtil.getGScopeById(sub.getParentId());
                        str = "[" + par.getGScopeName() + " " + sub.getGScopeName()+"]";
                    }
                    return str;
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s->{
                    if (!"".equals(s)) {
                        holder.content.setText(holder.content.getText().toString() + " " + s);
                    }
                }));
        GlideLoad.initRound(mAct)
                .load(adviser.StaffImg)
                .placeholder(io.rong.imkit.R.drawable.rc_default_portrait)
                .error(io.rong.imkit.R.drawable.rc_default_portrait)
                .fitCenter()
                .into(holder.image);
//        holder.image.setImageDrawable();

        if (mAct.isSelectAdviser()) {
            holder.connectImage.setVisibility(View.GONE);
            holder.phoneImage.setVisibility(View.GONE);
        } else {
            holder.connectImage.setOnClickListener((v) -> {
//                String targetId = "s_021_" + adviser.StaffNo.toLowerCase();
//                String title = adviser.CnName;
//                RongIM.getInstance().startConversation(mAct, Conversation.ConversationType.PRIVATE, targetId, title);  //s_021_aa75795 s_021_aa76088 s_021_aa76027
                MyUtils.toStaffTalk(mAct,adviser.StaffNo,adviser.CnName,MyUtils.TALK_FROM_ADVISER_LIST,"","","","","");
            });

            //拨打电话
            holder.phoneImage.setOnClickListener((v) -> {
                MyUtils.toCall400(mAct, adviser.MobileBy400, adviser.CnName);
            });
        }
    }

    @Override
    public int getItemCount() {
        return adviserList == null ? 0 : adviserList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView content;
        public ImageView image;
        public ImageView connectImage;
        public ImageView phoneImage;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.adviser_name);
            content = (TextView) itemView.findViewById(R.id.adviser_content);
            image = (ImageView) itemView.findViewById(R.id.adviser_img);
            connectImage = (ImageView) itemView.findViewById(R.id.adviser_connect);
            phoneImage = (ImageView) itemView.findViewById(R.id.adviser_phone);
        }

    }
}
