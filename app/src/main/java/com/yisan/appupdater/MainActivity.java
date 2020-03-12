package com.yisan.appupdater;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.yisan.appupdater.updater.AppUpdater;
import com.yisan.appupdater.updater.bean.AppDownloadBean;
import com.yisan.appupdater.updater.net.INetCallBack;
import com.yisan.appupdater.updater.ui.UpdateVersionShowDialog;
import com.yisan.appupdater.updater.utils.AppUtils;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * app应用应用内升级
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private static final String TAG = "MainActivity";

    /**
     * 请求权限CODE
     */
    private static final int REQUEST_PERMISSION_CODE = 1;
    /**
     * 请求权限
     */
    private String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    /**
     * 版本更新
     */
    private Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnUpdate = findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(this);


        boolean hasPermissions = EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (!hasPermissions) {
            EasyPermissions.requestPermissions(this, "需要读写权限", REQUEST_PERMISSION_CODE, permissions);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //将请求结果传递EasyPermission库处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_update) {

            final String appUpdateUrl = "http://www.yisanvspp.ink/app_update/app_updater_version.json";

            AppUpdater.getInstance().getNetManager().get(appUpdateUrl, new INetCallBack() {
                @Override
                public void success(String response) {

                    //1、解析json
                    AppDownloadBean appDownloadBean = AppDownloadBean.parse(response);
                    if (appDownloadBean == null) {
                        Toast.makeText(MainActivity.this, "版本检测接口返回数据异常", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //2、做版本匹配
                    try {
                        long versionCode = Long.parseLong(appDownloadBean.versionCode);
                        if (versionCode <= AppUtils.getAppVersionCode(MainActivity.this)) {
                            Toast.makeText(MainActivity.this, "已经是最新版本", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "版本号异常", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //3、需要更新、弹窗
                    UpdateVersionShowDialog.show(MainActivity.this, appDownloadBean);

                }

                @Override
                public void failed(Throwable throwable) {
                    Toast.makeText(MainActivity.this, "版本更新接口请求失败", Toast.LENGTH_SHORT).show();
                }
            }, MainActivity.this);

        }

    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        //允许权限
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        //拒绝权限
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消下载请求
        AppUpdater.getInstance().getNetManager().cancel(MainActivity.this);

    }
}
