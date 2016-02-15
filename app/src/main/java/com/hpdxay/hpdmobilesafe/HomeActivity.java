package com.hpdxay.hpdmobilesafe;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.hpdxay.hpdmobilesafe.adapters.HomeGVAdapter;
import com.hpdxay.hpdmobilesafe.utils.MD5Utils;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private GridView gvHomeContainer;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        gvHomeContainer = (GridView) findViewById(R.id.gv_home_container);

        sp = getSharedPreferences("config", MODE_PRIVATE);

        HomeGVAdapter adapter = new HomeGVAdapter(this);
        gvHomeContainer.setAdapter(adapter);
        gvHomeContainer.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                showLoginDialog();
                break;
            case 8:
                Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void showLoginDialog() {

        String password = sp.getString("password", null);
        if (TextUtils.isEmpty(password)) {
            showSetPasswordDialog();
        } else {
            showEnterDialog();
        }

    }

    private EditText etDialogPassword;
    private EditText etDialogPasswordConfirm;
    private Button btDialogCancel;
    private Button btDialogOk;
    private AlertDialog dialog;

    private void showSetPasswordDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_set_password, null, false);
        etDialogPassword = (EditText) view.findViewById(R.id.et_dialog_password);
        etDialogPasswordConfirm = (EditText) view.findViewById(R.id.et_dialog_password_confirm);
        btDialogCancel = (Button) view.findViewById(R.id.bt_dialog_cancel);
        btDialogOk = (Button) view.findViewById(R.id.bt_dialog_ok);
        final SharedPreferences.Editor edit = sp.edit();
        btDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etDialogPassword.getText().toString().trim();
                String confirm = etDialogPasswordConfirm.getText().toString().trim();
                if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm)) {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirm)) {
                    Toast.makeText(HomeActivity.this, "密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                edit.putString("password", MD5Utils.md5Password(password));
                edit.apply();
                enterLoseFindAcivity();
                dialog.dismiss();
            }
        });
        btDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private void enterLoseFindAcivity() {
        Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
        startActivity(intent);
    }

    private void showEnterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_input_password, null, false);
        etDialogPassword = (EditText) view.findViewById(R.id.et_dialog_password);
        btDialogCancel = (Button) view.findViewById(R.id.bt_dialog_cancel);
        btDialogOk = (Button) view.findViewById(R.id.bt_dialog_ok);
        final String spPassword = sp.getString("password", null);
        btDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etDialogPassword.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!MD5Utils.md5Password(password).equals(spPassword)) {
                    Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                enterLoseFindAcivity();
                dialog.dismiss();
            }
        });
        btDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }
}
