package com.yisan.appupdater.updater.ui;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.yisan.appupdater.R;
import com.yisan.appupdater.updater.AppUpdater;
import com.yisan.appupdater.updater.bean.AppDownloadBean;
import com.yisan.appupdater.updater.net.INetDownloadCallBack;
import com.yisan.appupdater.updater.utils.AppUtils;
import com.yisan.appupdater.utils.ScreenUtils;

import java.io.File;

/**
 * @author：wzh
 * @description: 版本更新弹窗、google建议使用DialogFragment处理弹窗业务逻辑
 * @packageName: com.yisan.appupdater.updater.ui
 * @date：2020/3/12 下午 1:55
 */
public class UpdateVersionShowDialog extends DialogFragment {

    private static final String TAG = "UpdateVersionShowDialog";
    private static final String APP_DOWNLOAD_BEAN = "AppDownloadBean";
    private AppDownloadBean bean;
    private TextView tvTitle;
    private TextView tvContent;
    private Button btnUpdate;


    public static void show(FragmentActivity activity, AppDownloadBean bean) {

        Bundle bundle = new Bundle();
        bundle.putSerializable(APP_DOWNLOAD_BEAN, bean);
        UpdateVersionShowDialog dialog = new UpdateVersionShowDialog();
        dialog.setArguments(bundle);
        dialog.show(activity.getSupportFragmentManager(), "updateVersionShowDialog");

    }

    @Override
    public void onStart() {
        super.onStart();

        //必须设置window的参数，不然布局会不显示
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = ScreenUtils.dip2px(getActivity(), 311);
        layoutParams.height = ScreenUtils.dip2px(getActivity(), 230);
        window.setAttributes(layoutParams);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            bean = (AppDownloadBean) bundle.getSerializable(APP_DOWNLOAD_BEAN);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_layout, container, false);
        bindView(view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //dialog不需要标题
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

    }

    private void bindView(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        tvContent = view.findViewById(R.id.tv_content);
        btnUpdate = view.findViewById(R.id.btn_update);
        tvTitle.setText(TextUtils.isEmpty(bean.title) ? "" : bean.title);
        tvContent.setText(TextUtils.isEmpty(bean.content) ? "" : bean.content);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改按钮状态
                btnUpdate.setEnabled(false);
                download();

            }
        });
    }

    private void download() {
        //4、点击下载
        File targetFile = new File(getActivity().getCacheDir(), "target.apk");
        AppUpdater.getInstance().getNetManager().download(bean.url, targetFile,
                new INetDownloadCallBack() {
                    @Override
                    public void success(File apkFile) {
                        //修改按钮状态
                        btnUpdate.setEnabled(true);
                        dismiss();
                        //检查文件的md5值
                        String fileMd5 = AppUtils.getFileMd5(apkFile);
                        if (fileMd5 != null && fileMd5.equals(bean.md5)) {
                            try {
                                //安装的apk
                                AppUtils.installApk(getActivity(), apkFile);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "安装失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "安装文件校验失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void progress(int progress) {
                        //更新进度
                        btnUpdate.setText(progress + "%");
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        //修改状态
                        btnUpdate.setEnabled(true);
                        Toast.makeText(getActivity(), "文件下载失败", Toast.LENGTH_SHORT).show();
                    }
                }, UpdateVersionShowDialog.this);
    }



    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        //取消下载
        AppUpdater.getInstance().getNetManager().cancel(UpdateVersionShowDialog.this);
    }
}
