package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.jakewharton.rxbinding.widget.RxTextView;

/**
 * Created by diaoqf on 2017/3/27.
 */

public class RemarkEditTextLayout extends LinearLayout {
    private EditText contentEdt;
    private TextView size_info;

    private ContentUpdatelistener contentUpdatelistener;


    public RemarkEditTextLayout(Context context) {
        super(context);
        init(context,null);
    }

    public RemarkEditTextLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.remark_edittext_layout, this, true);
        contentEdt = (EditText) view.findViewById(R.id.content);
        size_info = (TextView) view.findViewById(R.id.size_info);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RemarkEditTextLayout);
        Integer length = a.getInteger(R.styleable.RemarkEditTextLayout_max_length,120);
        contentEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
        contentEdt.setHint(a.getString(R.styleable.RemarkEditTextLayout_content_hint));
        size_info.setText("0/"+length+"字");
        a.recycle();

        RxTextView.textChanges(contentEdt)
                .subscribe(s->{
                    size_info.setText(s.length()+"/"+length+"字");
                    if (contentUpdatelistener != null) {
                        contentUpdatelistener.update(s.length());
                    }
                });

    }

    public void setContentUpdatelistener(ContentUpdatelistener contentUpdatelistener) {
        this.contentUpdatelistener = contentUpdatelistener;
    }

    /**
     * 获取填写内容
     * @return
     */
    public String getText() {
        return contentEdt.getText().toString();
    }

    public void setText(String str) {
        contentEdt.setText(str);
    }

    public interface ContentUpdatelistener {
        void update(int length);
    }

}
