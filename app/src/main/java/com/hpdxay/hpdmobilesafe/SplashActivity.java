package com.hpdxay.hpdmobilesafe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hpdxay.hpdmobilesafe.utils.MyLog;
import com.hpdxay.hpdmobilesafe.utils.StreamTools;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class SplashActivity extends AppCompatActivity {


    private static final int ENTER_HOME = 0;
    private static final int SHOW_UPDATE_DIALOG = 1;
    private static final int URL_EEROR = 2;
    private static final int NET_EEROR = 3;
    private static final int JSON_EEROR = 4;
    private RelativeLayout rlSplashContainer;
    private TextView tvSplashVersion;
    private ProgressBar pbSplashVersion;
    private SharedPreferences sp;
    private TextView tvSplashUpdate;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case ENTER_HOME:
                    enterHome();
                    Toast.makeText(SplashActivity.this, "不用更新", Toast.LENGTH_SHORT).show();
                    break;
                case SHOW_UPDATE_DIALOG:
                    showUpdateDialog();
                    Toast.makeText(SplashActivity.this, "要更新", Toast.LENGTH_SHORT).show();
                    break;
                case URL_EEROR:
                    enterHome();
                    Toast.makeText(SplashActivity.this, "URL错误", Toast.LENGTH_SHORT).show();
                    break;
                case NET_EEROR:
                    enterHome();
                    Toast.makeText(SplashActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    break;
                case JSON_EEROR:
                    enterHome();
                    Toast.makeText(SplashActivity.this, "JSON错误", Toast.LENGTH_SHORT).show();
                    break;
            }


        }
    };

    private void enterHome() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void showUpdateDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("更新提醒");
        builder.setMessage("有新版本，提供更全面的功能");
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                enterHome();
            }
        });
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mobilesafe2.0.apk";
                    FinalHttp finalHttp = new FinalHttp();
                    finalHttp.download("http://192.168.56.1:8080/mobilesafe2.0.apk", path, new AjaxCallBack<File>() {
                        @Override
                        public void onLoading(long count, long current) {
                            super.onLoading(count, current);
                            int number = (int) (current * 100 / count);
                            tvSplashUpdate.setText("更新进度：" + number + "%");
                        }

                        @Override
                        public void onStart() {
                            super.onStart();
                            tvSplashUpdate.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onSuccess(File file) {
                            super.onSuccess(file);
                            //下载成功后安装
                            installAPK(file);
                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            super.onFailure(t, errorNo, strMsg);
                            t.printStackTrace();
                            Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                enterHome();

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    //安装apk
    private void installAPK(File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sp = getSharedPreferences("config", MODE_PRIVATE);
        boolean update = sp.getBoolean("update", false);

        tvSplashVersion = (TextView) findViewById(R.id.tv_splash_version);
        tvSplashVersion.setText("版本号：" + getVersionName());
        rlSplashContainer = (RelativeLayout) findViewById(R.id.rl_splash_container);
        tvSplashUpdate = (TextView) findViewById(R.id.tv_splash_update);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
        alphaAnimation.setDuration(2000);
        rlSplashContainer.setAnimation(alphaAnimation);

        //检查升级
        if (update) {
            checkUpadate();
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    enterHome();
                }
            }, 2000);
        }

    }

    private void checkUpadate() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Message message = handler.obtainMessage();
                long startTime = System.currentTimeMillis();
                try {
                    //一般的，更新版本的这个string,放在config.xml中
                    URL url = new URL(getString(R.string.serverurl));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    int code = conn.getResponseCode();
                    MyLog.d("splash", code + "");
                    if (code == 200) {
                        //联网成功
                        InputStream is = conn.getInputStream();
                        //把转成String
                        String result = StreamTools.readFromStream(is);
                        JSONObject object = new JSONObject(result);
                        String version = object.getString("version");
                        MyLog.d("splash", version);
                        String description = object.getString("description");
                        MyLog.d("splash", description);
                        String apkurl = object.getString("apkurl");
                        MyLog.d("splash", apkurl);
                        //校验是否有新版本
                        if (getVersionName().equals(version)) {
                            //版本一致，不用更新，进入主页面
                            message.what = ENTER_HOME;
                        } else {
                            //版本不一致。需要更新，弹出一个对话框提示更新
                            message.what = SHOW_UPDATE_DIALOG;
                        }
                    }
                } catch (MalformedURLException e) {
                    message.what = URL_EEROR;
                    e.printStackTrace();
                } catch (IOException e) {
                    message.what = NET_EEROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    message.what = JSON_EEROR;
                    e.printStackTrace();
                } finally {
                    long endTime = System.currentTimeMillis();
                    long time = endTime - startTime;
                    if (time < 2000) {
                        try {
                            Thread.sleep(2000 - time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    //得到引用程序的版本名称
    private String getVersionName() {
        //用来管理手机的APK
        PackageManager manager = getPackageManager();
        try {
            //得到APK的功能清单文件
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            String versionName = info.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}
