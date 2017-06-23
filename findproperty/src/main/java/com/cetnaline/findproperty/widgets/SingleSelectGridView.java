package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.cetnaline.findproperty.R;

/**
 * Created by diaoqf on 2016/12/9.
 */

public class SingleSelectGridView extends GridView {
    private int lastPosition=-1;

    public SingleSelectGridView(Context context) {
        super(context);
    }

    public SingleSelectGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleSelectGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLastPosition(int lastPosition) {
        if (this.lastPosition != lastPosition) {
            if (this.lastPosition > -1) {
                View view1 = getChildAt(this.lastPosition);
                TextView text1 = (TextView) view1.findViewById(R.id.text);
                text1.setTextColor(getResources().getColor(R.color.drop_text_color));
                text1.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_check_line));
            }
            this.lastPosition = lastPosition;
            View view2 = getChildAt(this.lastPosition);
            TextView text2 = (TextView) view2.findViewById(R.id.text);
            text2.setTextColor(getResources().getColor(R.color.appBaseColor));
            text2.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_check_line_sel));
        }
    }

}
