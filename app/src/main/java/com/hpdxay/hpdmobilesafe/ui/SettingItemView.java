package com.hpdxay.hpdmobilesafe.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hpdxay.hpdmobilesafe.R;

/**
 * Created by hpd on 2016/2/1.
 */
public class SettingItemView extends RelativeLayout {

    private TextView tvTitle;
    private TextView tvDesc;
    private CheckBox box;
    private String title;
    private String descOn;
    private String descOff;


    public void init() {

        View view = LayoutInflater.from(this.getContext()).inflate(R.layout.setting_item_view, this, false);
        tvTitle = (TextView) view.findViewById(R.id.tv_setting_item_title);
        tvDesc = (TextView) view.findViewById(R.id.tv_setting_item_desc);
        box = (CheckBox) view.findViewById(R.id.tv_setting_item_box);
        addView(view);
    }

    public SettingItemView(Context context) {
        super(context);
        init();
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);
            title = array.getString(R.styleable.SettingItemView_item_title);
            descOn = array.getString(R.styleable.SettingItemView_desc_on);
            descOff = array.getString(R.styleable.SettingItemView_desc_off);
            tvTitle.setText(title);
            array.recycle();
        }


    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public boolean isChecked() {
        return box.isChecked();
    }

    public void setCheck(boolean check) {
        if (check) {
            tvDesc.setText(descOn);
        } else {
            tvDesc.setText(descOff);
        }
        box.setChecked(check);
    }

}
