package com.cetnaline.findproperty.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.cetnaline.findproperty.api.bean.ExerciseListBo;
import com.cetnaline.findproperty.ui.fragment.ExerciseFragment;
import com.cetnaline.findproperty.widgets.bottomwindow.adapter.ExpandableFragmentPagerAdapter;

import java.util.List;

/**
 * Created by fanxl2 on 2016/8/12.
 */
public class ExerciseAdapter extends ExpandableFragmentPagerAdapter<ExerciseListBo> {

	public ExerciseAdapter(FragmentManager fm, List<ExerciseListBo> items) {
		super(fm, items);
	}

	@Override
	public Fragment getItem(int position) {
		return ExerciseFragment.getInstance(items.get(position));
	}
}
