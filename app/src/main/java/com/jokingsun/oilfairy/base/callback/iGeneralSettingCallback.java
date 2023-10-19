package com.jokingsun.oilfairy.base.callback;

import android.app.Activity;
import android.os.IBinder;

public interface iGeneralSettingCallback {

    void showLoading();

    void cancelLoading();

    void showToast(CharSequence content);

    void showToast(CharSequence content, boolean isLong);

    void updateStatusBarColor(Activity activity, int colorResId, boolean useLightText);

    void hideKeyboard();

    void hideKeyboard(IBinder token);
}
