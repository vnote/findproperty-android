package com.cetnaline.findproperty.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.cetnaline.findproperty.R;


/**
 * 自定义对话框
 * Created by guilin on 16/5/13.
 */
public class CProgressDialog extends Dialog {

    public CProgressDialog(Context context) {
        this(context,true);
    }

    public CProgressDialog(Context context, boolean cancelable) {
        super(context, R.style.DefaultProgressDialog);
        this.setCancelable(cancelable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_progress_dialog);
    }
}
