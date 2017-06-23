package com.cetnaline.findproperty.highline;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.entity.event.TipClickEvent;
import com.cetnaline.findproperty.utils.RxBus;

public class IconComponent implements Component {

	@Override
	public View getView(LayoutInflater inflater) {
		LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.layout_icon_info, null);
		ll.findViewById(R.id.ic_info_know).setOnClickListener(v -> {
			RxBus.getDefault().send(new TipClickEvent(0));
		});
//		ll.setOnClickListener(view -> {
//
//		});
		return ll;
	}

	@Override
	public int getAnchor() {
		return Component.ANCHOR_RIGHT;
	}

	@Override
	public int getFitPosition() {
		return Component.FIT_END;
	}

	@Override
	public int getXOffset() {
		return 0;
	}

	@Override
	public int getYOffset() {
		return 0;
	}
}