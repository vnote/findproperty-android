package com.cetnaline.findproperty.highline;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.entity.event.TipClickEvent;
import com.cetnaline.findproperty.utils.RxBus;

public class ListComponent implements Component {

	@Override
	public View getView(LayoutInflater inflater) {
		LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.layout_list_info, null);
		ll.findViewById(R.id.ic_info_know).setOnClickListener(view -> {
			RxBus.getDefault().send(new TipClickEvent(1));
		});
//		ll.setOnClickListener(view -> {
//			RxBus.getDefault().send(new TipClickEvent(1));
//		});
		return ll;
	}

	@Override
	public int getAnchor() {
		return Component.ANCHOR_TOP;
	}

	@Override
	public int getFitPosition() {
		return Component.FIT_START;
	}

	@Override
	public int getXOffset() {
		return -160;
	}

	@Override
	public int getYOffset() {
		return -10;
	}
}