package com.cetnaline.findproperty.ui.adapter;

import android.util.SparseIntArray;
import android.view.View;
import android.widget.TextView;

import com.camnter.easyrecyclerview.adapter.EasyRecyclerViewAdapter;
import com.camnter.easyrecyclerview.holder.EasyRecyclerViewHolder;
import com.camnter.easyrecyclerviewsidebar.helper.EasyRecyclerSectionIndexer;
import com.camnter.easyrecyclerviewsidebar.sections.EasySection;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.db.entity.DropBo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by diaoqf on 2017/4/19.
 */

public class SchoolListAdapter  extends EasyRecyclerViewAdapter
        implements EasyRecyclerSectionIndexer<EasySection> {

    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    private List<EasySection> easySections;

    private onItemClick onItemClick;

    @Override
    public int[] getItemLayouts() {
        return new int[] { R.layout.item_school };
    }

    @Override
    public void onBindRecycleViewHolder(EasyRecyclerViewHolder viewHolder, int position) {
        DropBo dropBo = this.getItem(position);
        if (dropBo == null) return;
        TextView item_tv_text = viewHolder.findViewById(R.id.item_tv_text);
        item_tv_text.setText(dropBo.getText());
        TextView section_header_tv = viewHolder.findViewById(R.id.section_header_tv);
        setHeaderLogic(dropBo, section_header_tv, viewHolder, position);

        if (onItemClick != null) {
            item_tv_text.setOnClickListener(v->{
                onItemClick.onClick(position);
            });
        }
    }

    @Override
    public int getRecycleViewItemType(int position) {
        return 0;
    }

    @Override
    public List<EasySection> getSections() {
        this.resetSectionCache();

        int itemCount = getItemCount();
        if (itemCount < 1) return this.easySections;

        easySections.clear();
        positionOfSection.clear();

        String letter;
        for (int i = 0; i < itemCount; i++) {
            DropBo dropBo = this.getItem(i);
//            Logger.i("buobao:"+dropBo.getHeaderStr()+dropBo.getText());
            letter = dropBo.getHeaderStr().substring(0,1).toUpperCase();
            if (easySections.size() > 0) {
                if (!easySections.get(easySections.size()-1).letter.equalsIgnoreCase(letter)) {
                    easySections.add(new EasySection(letter));
                    positionOfSection.put(easySections.size()-1, i);
                }
            } else {
                easySections.add(new EasySection(letter));
                positionOfSection.put(0, i);
            }
            sectionOfPosition.put(i, easySections.size()-1);
        }
        return this.easySections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return positionOfSection.get(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        return sectionOfPosition.get(position);
    }

    public void setHeaderLogic(DropBo dropBo, TextView headerTv, EasyRecyclerViewHolder viewHolder, int position) {
        String letter = dropBo.getHeaderStr().substring(0,1);
        if (position == 0) {
            this.setHeader(true, headerTv, letter);
        } else {
            DropBo pre = this.getItem(position - 1);
            if (!letter.equalsIgnoreCase(pre.getHeaderStr().substring(0,1))) {
                this.setHeader(true, headerTv, letter);
            } else {
                this.setHeader(false, headerTv, null);
            }
        }
    }

    public void setHeader(boolean visible, TextView headerTv, String header) {
        if (visible) {
            headerTv.setText(header.toUpperCase());
            headerTv.setVisibility(View.VISIBLE);
        } else {
            headerTv.setVisibility(View.GONE);
        }
    }

    private void resetSectionCache() {
        if (this.easySections == null) this.easySections = new ArrayList<>();
        if (this.easySections.size() > 0) this.easySections.clear();
        if (sectionOfPosition == null) this.sectionOfPosition = new SparseIntArray();
        if (this.sectionOfPosition.size() > 0) this.sectionOfPosition.clear();
        if (this.positionOfSection == null) this.positionOfSection = new SparseIntArray();
        if (this.positionOfSection.size() > 0) this.positionOfSection.clear();
    }

    public void setOnItemClick(SchoolListAdapter.onItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public interface onItemClick {
        void onClick(int position);
    }
}
