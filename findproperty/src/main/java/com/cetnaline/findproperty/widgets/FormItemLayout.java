package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.inputmethodservice.InputMethodService;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cetnaline.findproperty.R;

/**
 * 表单项布局
 * Created by diaoqf on 2017/3/27.
 */
public class FormItemLayout extends LinearLayout {

    private TextView title;
    private EditText contentExt;
    private TextView content_ext;
    private ImageView right_img;
    private View touch_view;

    private ImageClickListener imageListener;
    private OnContentClickListener onContentClickListener;
    private OnExtClickListener onExtClickListener;
    private ItemFocusListener onFocusChangeListener;



    public FormItemLayout(Context context) {
        super(context);
        init(context,null);
    }

    public FormItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.form_item_layout, this, true);
        title = (TextView) view.findViewById(R.id.title);
        contentExt = (EditText) view.findViewById(R.id.content);
        content_ext = (TextView) view.findViewById(R.id.content_ext);
        right_img = (ImageView) view.findViewById(R.id.right_img);
        touch_view = view.findViewById(R.id.touch_view);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FormItemLayout);
        title.setText(a.getString(R.styleable.FormItemLayout_title));
        contentExt.setHint(a.getString(R.styleable.FormItemLayout_content_hint));
        contentExt.setEnabled(a.getBoolean(R.styleable.FormItemLayout_input_enable, true));
        if (a.getInt(R.styleable.FormItemLayout_input_type,0) == 1) {
            contentExt.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else if (a.getInt(R.styleable.FormItemLayout_input_type,0) == 1) {
            contentExt.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        int resId = a.getResourceId(R.styleable.FormItemLayout_ext_background,0);
        if (resId != 0) {
            content_ext.setBackgroundDrawable(getResources().getDrawable(resId));
        }

        contentExt.setTextColor(a.getColor(R.styleable.FormItemLayout_input_text_color,getResources().getColor(R.color.normalText)));

        int inputLength = a.getInt(R.styleable.FormItemLayout_input_length, -1);
        if (inputLength > 0) {
            contentExt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(inputLength)});
            //editText.setTransformationMethod(PasswordTransformationMethod.getInstance());密码框
        }

        //是否显示扩展文字（单位、称呼等）
        if (a.getBoolean(R.styleable.FormItemLayout_show_ext, false)) {
            content_ext.setText(a.getString(R.styleable.FormItemLayout_content_ext));
            content_ext.setVisibility(VISIBLE);
            content_ext.setOnClickListener(v->{
                if (onExtClickListener != null) {
                    onExtClickListener.onClick();
                }
            });
        } else {
            content_ext.setVisibility(GONE);
        }

        //右侧箭头的点击事件
        if (a.getBoolean(R.styleable.FormItemLayout_show_right_img, false)) {
            right_img.setOnClickListener(v->{
                if (imageListener != null) {
                    imageListener.onClick();
                }
            });
            right_img.setVisibility(VISIBLE);
        } else {
            right_img.setVisibility(GONE);
        }

        //可点击区域显示（无法直接输入时用于相应点击事件）
        if (a.getBoolean(R.styleable.FormItemLayout_show_touch_view, false)) {
            touch_view.setOnClickListener(v->{
                if (imageListener != null) {
                    imageListener.onClick();
                }
            });
            touch_view.setVisibility(VISIBLE);
        } else {
            touch_view.setVisibility(GONE);
        }

        contentExt.setOnTouchListener((v, event) -> {
            if (onContentClickListener != null) {
                onContentClickListener.onClick();
            }
            return false;
        });

        a.recycle();

        contentExt.setOnFocusChangeListener((v, hasFocus) -> {
            if (onFocusChangeListener != null) {
                onFocusChangeListener.onFocusChange(hasFocus);
            }
        });
    }

    /**
     * 获取输入焦点并强制显示键盘
     */
    public void focusInput() {
        contentExt.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0,0);
    }

    public void setImageListener(ImageClickListener imageListener) {
        this.imageListener = imageListener;
    }

    public void setOnContentClickListener(OnContentClickListener onContentClickListener) {
        this.onContentClickListener = onContentClickListener;
    }

    public void setOnExtClickListener(OnExtClickListener onExtClickListener) {
        this.onExtClickListener = onExtClickListener;
    }

    /**
     * 获取填写内容
     * @return
     */
    public String getText() {
        return contentExt.getText().toString();
    }

    public void setText(String str) {
        contentExt.setText(str);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setExtText(String extText) {
        content_ext.setText(extText);
    }

    public String getExtText() {
        return content_ext.getText().toString();
    }

    public void setHintText(String hintText) {
        contentExt.setHint(hintText);
    }

    public void setImgShow(boolean show) {
        if (show) {
//            right_img.setVisibility(VISIBLE);
            touch_view.setVisibility(VISIBLE);
        } else {
//            right_img.setVisibility(GONE);
            touch_view.setVisibility(GONE);
        }
    }

    public void setInputEnable(boolean enable) {
        contentExt.setEnabled(enable);
    }

    public void setItemFocusListener(ItemFocusListener onFocusChangeListener) {
        this.onFocusChangeListener = onFocusChangeListener;
    }

    public interface ImageClickListener {
        void onClick();
    }

    public interface OnContentClickListener {
        void onClick();
    }

    public interface OnExtClickListener {
        void onClick();
    }

    public interface ItemFocusListener{
        void onFocusChange(boolean focus);
    }
}
