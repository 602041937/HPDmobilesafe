package com.hpdxay.hpdmobilesafe;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.hpdxay.hpdmobilesafe.ui.SettingItemView;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private SettingItemView sivSettingUpdate;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        
        sivSettingUpdate = (SettingItemView) findViewById(R.id.siv_setting_update);
        sivSettingUpdate.setOnClickListener(this);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        boolean update = sp.getBoolean("update", false);
        if (update) {
            sivSettingUpdate.setCheck(true);
        } else {
            sivSettingUpdate.setCheck(false);
        }


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        SharedPreferences.Editor edit = sp.edit();
        switch (id) {
            case R.id.siv_setting_update:

                boolean checked = sivSettingUpdate.isChecked();
                if (checked) {
                    sivSettingUpdate.setCheck(false);
                    edit.putBoolean("update", false);
                } else {
                    sivSettingUpdate.setCheck(true);
                    edit.putBoolean("update", true);
                }
                break;
        }
        edit.apply();
    }
}
