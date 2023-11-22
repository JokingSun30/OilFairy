package com.jokingsun.oilfairy.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.common.constant.FileSizeUnitEnum;
import com.jokingsun.oilfairy.data.local.simple.ApplicationUserSp;
import com.orhanobut.logger.Logger;
import com.zyq.easypermission.EasyPermission;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cfd058
 */
public class GeneralUtil {

    /**
     * 將目標 JavaBean 映射到 新的 JavaBean (請注意欄位名稱)
     *
     * @param targetObject 目標 JavaBean
     * @param newClass     新的 JavaBean 型別
     * @return 返回映射後的物件
     */
    public static Object mapObjectToAnother(Object targetObject, Class<?> newClass) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(targetObject), newClass);
    }

    /**
     * 打印 Log 訊息
     *
     * @param tag     標示旗標
     * @param message 打印訊息
     */
    public static void printLog(String tag, String message) {
        Logger.log(Logger.DEBUG, tag, message, null);
    }

    /**
     * 一般格式的警示 Dialog
     *
     * @param title   標題
     * @param message 內容訊息
     * @param context Context
     */
    public static void generalAlertDialog(Context context, String title, String message, DialogInterface.OnClickListener clickListener) {
        AlertDialog.Builder builderLeave = new AlertDialog.Builder(context);
        builderLeave.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.determine), clickListener)
                .create().show();
    }

    /**
     * 一般詢問 Dialog 含確定及取消鈕(自訂按鈕文字)
     *
     * @param context  the context
     * @param title    標題
     * @param message  內容訊息
     * @param positive 確認按鈕描述
     * @param negative 取消按鈕描述
     * @param callback 確認及取消執行方法
     */
    public static void generaAskingDialog(Context context, String title, String message, String positive, String negative, AlertDialogCallback callback) {
        AlertDialog.Builder builderLeave = new AlertDialog.Builder(context);
        builderLeave.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(positive, (dialog, which) -> {
                    callback.onClickPositive();
                    dialog.dismiss();
                })
                .setNegativeButton(negative, (dialog, which) -> {
                    callback.onClickNegative();
                    dialog.dismiss();
                }).create().show();
    }

    /**
     * 取得手機螢幕高度與寬度
     *
     * @param context    the context
     * @param isGetWidth the is get width
     * @return screen width
     */
    public static int getScreenWidth(Activity context, boolean isGetWidth) {
        DisplayMetrics dm = new DisplayMetrics();
        //取得視窗屬性
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        //視窗的寬度
        int screenWidth = dm.widthPixels;
        //視窗高度
        int screenHeight = dm.heightPixels;

        return isGetWidth ? screenWidth : screenHeight;
    }

    /**
     * 取得螢幕寬及高的dp 並存入 SharePreference.
     *
     * @param context the context
     */
    public static void getAndroidScreenProperty(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(dm);
        }
        // 螢幕寬度（畫素）
        int width = dm.widthPixels;
        // 螢幕高度（畫素）
        int height = dm.heightPixels;
        //螢幕密度（0.75 / 1.0 / 1.5）
        float density = dm.density;
        //螢幕密度dpi（120 / 160 / 240）
        int densityDpi = dm.densityDpi;

        //螢幕寬度演算法: 螢幕寬度(dp)
        int screenWidth = (int) (width / density);
        //螢幕寬度演算法: 螢幕高度(dp)
        int screenHeight = (int) (height / density);
        ApplicationUserSp.putInt("SCREEN_WIDTH", screenWidth);
        ApplicationUserSp.putInt("SCREEN_HEIGHT", screenHeight);
//        Logger.e(screenWidth + "======" + screenHeight);
    }

    /**
     * 設定 Activity 對應的頂部狀態列的顏色及 text color
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void updateStatusBarColor(Activity activity, int colorResId, boolean useDarkText) {
        Window window = activity.getWindow();
        //status bar text color
        //status bar文字變黑色
        int systemUiVisibilityFlags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        if (!useDarkText) {
            //status bar文字變白色
            systemUiVisibilityFlags = window.getDecorView().getSystemUiVisibility();
            systemUiVisibilityFlags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        window.getDecorView().setSystemUiVisibility(systemUiVisibilityFlags);
        //status bar background color
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, colorResId));
    }

    /**
     * 透過條件設置過濾 List ，並返回過濾後 List
     *
     * @param <T>  list資料的型別
     * @param list 原始list資料
     * @param hook 過濾條件
     * @return 過濾後 List
     */
    public static <T> List<T> listFilter(List<T> list, ListUtilsHook<T> hook) {
        ArrayList<T> r = new ArrayList<T>();
        for (T t : list) {
            if (hook.filter(t)) {
                r.add(t);
            }
        }
        r.trimToSize(); //去掉多餘的預留空間
        return r;
    }

    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static int dp2px(float dpValue, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;

        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 剪貼簿 - 複製目標字串
     */
    public static void clipStringToAnyWhere(Context context, String target, String hint) {
        if (context != null) {
            ClipData clipData;
            ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipData = ClipData.newPlainText("text", target);
            if (manager != null) {
                manager.setPrimaryClip(clipData);
                Toast.makeText(context, hint, Toast.LENGTH_LONG).show();
            }
        }

    }

    /**
     * 文字直接分享至選定的平台
     */
    public static void shareTextContent(Context context, String shareContent) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, shareContent);
        context.startActivity(Intent.createChooser(intent, "分享到"));
    }

    public static void sendMailToAnyone(Context context, String recipient, String subject, String des) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        String[] tos = {recipient};

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, tos);
        intent.putExtra(Intent.EXTRA_TEXT, des);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        context.startActivity(Intent.createChooser(intent, "給我們一些寶貴的回饋吧！"));
    }

    /**
     * 透過網址連結，顯示指定網頁
     */
    public static void linkToWebsite(Context context, Uri linkUri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(linkUri);
        context.startActivity(intent);
    }

    /**
     * 啟用 App 震動一次
     */
    public static void enableAppVibrateOneShot(Context context) {
        if (EasyPermission.build().hasPermission(context, Manifest.permission.VIBRATE)) {
            Vibrator vb = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //第一個參數是毫秒數，第二個參數是震動強度，值在 1 - 255 之間。
                VibrationEffect vibrationEffect = VibrationEffect.createOneShot(300, 50);
                vb.vibrate(vibrationEffect);
            } else {
                vb.vibrate(300);
            }

        }
    }

    /**
     * 判斷是否在指定 View 範圍內 (碰觸事件)
     *
     * @return true :在範圍內
     */
    public static boolean isTouchPointInView(View view, int x, int y) {
        if (view == null) {
            return false;
        }

        int[] location = new int[2];
        view.getLocationOnScreen(location);

        int left = location[0];
        int top = location[1];

        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();

        if (y >= top && y <= bottom && x >= left && x <= right) {
            Logger.d("移動 : 在範圍內");
            return true;
        } else {
            Logger.d("移動 : 已超出範圍");
            return false;
        }

    }

    /**
     * 取得 View 中心點 X 軸的 位置
     *
     * @param view
     * @return
     */
    public static int getCenterXofViewOnScreen(View view) {
        int centerX = view.getWidth() / 2;
        Logger.i("getCenterXofViewOnScreen - "
                + "width : " + view.getWidth()
                + "\tcenter : " + centerX
                + "\tLeft : " + view.getLeft()
                + "\tCenterXofViewOnScreen : " + (view.getLeft() + centerX));
        return view.getLeft() + centerX;
    }

    /**
     * 取得 View 中心點 Y 軸的 位置
     *
     * @param view
     * @return
     */
    public static int getCenterYofViewOnScreen(View view) {
        int centerY = view.getHeight() / 2;
        return view.getTop() + centerY;
    }

    /**
     * 獲取檔案大小
     *
     * @param targetFilePath 目標分析檔案
     * @param unitEnum       分析格式單位
     * @return
     */
    public static int getFileSize(String targetFilePath, FileSizeUnitEnum unitEnum) {

        if (unitEnum == null) {
            return 0;
        }

        if (targetFilePath != null && !targetFilePath.isEmpty()) {
            File file = new File(targetFilePath);
            long fileSize = file.length() / (unitEnum.getFormula());

            return (int) fileSize;
        }

        return 0;
    }

    public static String getJsonFromAsset(Context context, String fileName) {
        String jsonString = "";

        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonString = new String(buffer, StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        return jsonString;
    }


    /**
     * The interface Alert dialog callback.
     */
    public interface AlertDialogCallback {
        /**
         * On click positive.
         */
        void onClickPositive();

        /**
         * On click negative.
         */
        void onClickNegative();
    }

    /**
     * The interface List utils hook.
     *
     * @param <T> the type parameter
     */
    public interface ListUtilsHook<T> {
        /**
         * Filter boolean.
         *
         * @param t the t
         * @return the boolean
         */
        boolean filter(T t);
    }

    /**
     * 測試 App OOM
     */
    public static void generateOOM() throws Exception {
        int iteratorValue = 20;
        System.out.println("\n=================> OOM test started..\n");
        for (int outerIterator = 1; outerIterator < 20; outerIterator++) {
            System.out.println("Iteration " + outerIterator + " Free Mem: " + Runtime.getRuntime().freeMemory());
            int loop1 = 2;
            int[] memoryFillIntVar = new int[iteratorValue];
            // feel memoryFillIntVar array in loop..
            do {
                memoryFillIntVar[loop1] = 0;
                loop1--;
            } while (loop1 > 0);
            iteratorValue = iteratorValue * 5;
            System.out.println("\nRequired Memory for next loop: " + iteratorValue);
            Thread.sleep(1000);
        }
    }
}
