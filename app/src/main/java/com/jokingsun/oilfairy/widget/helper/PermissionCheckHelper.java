package com.jokingsun.oilfairy.widget.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.zyq.easypermission.EasyPermission;
import com.zyq.easypermission.EasyPermissionResult;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author cfd058
 */
public class PermissionCheckHelper {

    private final AppCompatActivity activity;
    private String requestDialogTitle = "App 需要相關權限";
    private String requestDialogContent = "請至「油價小精靈」設定中心手動授權";
    private OnResultListener onResultListener;
    private int recentRequestCode = 0;

    public PermissionCheckHelper(AppCompatActivity activity) {
        this.activity = activity;
    }

    /**
     * 是否擁有相關權限
     */
    public boolean hasPermission(Context context , String[] permissions){
        return EasyPermission.build().hasPermission(context, permissions);
    }

    /**
     * 檢查權限
     *
     * @param permissions 目標檢查權限
     * @param requestCode 檢查需求碼
     */
    public void checkPermission(String[] permissions, int requestCode) {
        this.recentRequestCode = requestCode;
        if (hasPermission(activity,permissions)) {
            //已以通過所需權限
            if (onResultListener != null && recentRequestCode == requestCode) {
                onResultListener.onAccessPermission();
            }

        } else {
            //開始進入權限訊問流程
            EasyPermission easyPermission = EasyPermission.build()
                    .mRequestCode(requestCode)
                    .mContext(activity)
                    .mPerms(permissions)
                    .mResult(new EasyPermissionResult() {
                        @Override
                        public void onPermissionsAccess(int requestCode) {
                            super.onPermissionsAccess(requestCode);
                            //通過後 執行區塊
                            if (onResultListener != null && recentRequestCode == requestCode) {
                                onResultListener.onAccessPermission();
                            }
                        }

                        @Override
                        public void onPermissionsDismiss(int requestCode, @NonNull @NotNull List<String> permissions) {
                            super.onPermissionsDismiss(requestCode, permissions);
                            //權限遭到拒絕
                        }

                        @Override
                        public boolean onDismissAsk(int requestCode, @NonNull @NotNull List<String> permissions) {
                            //權限遭到拒絕 ，且不再提示
                            showRequestDialog();
                            return true;

                        }
                    });

            easyPermission.requestPermission();
        }
    }

    /**
     * 權限遭到拒絕 ，且不再詢問的情況下，提示使用者跳轉至控制中心手動授權
     */
    public void showRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(requestDialogTitle);
        builder.setMessage(requestDialogContent);

        builder.setPositiveButton("確定", (dialog, which) -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            activity.startActivityForResult(intent, EasyPermission.APP_SETTINGS_RC);
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    /**
     * 設置權限標題提示 (跳轉至控制中心)
     *
     * @param requestDialogTitle 標題
     */
    public void setRequestDialogTitle(String requestDialogTitle) {
        this.requestDialogTitle = requestDialogTitle;
    }

    public void setRequestDialogContent(String requestDialogContent) {
        this.requestDialogContent = requestDialogContent;
    }

    public interface OnResultListener {
        /**
         * 成功通過權限
         *
         */
        void onAccessPermission();
    }

    public void setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
    }
}
