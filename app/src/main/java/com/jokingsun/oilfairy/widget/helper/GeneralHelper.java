package com.jokingsun.oilfairy.widget.helper;

import android.content.Context;

import com.kaopiz.kprogresshud.KProgressHUD;

/**
 * @author Joshua
 */
public class GeneralHelper {
    private Context mContext;
    private KProgressHUD loading;

    public GeneralHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void showLoading() {
        if (loading == null) {
            loading = KProgressHUD.create(mContext)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0);
        }
        loading.show();
    }

    public void cancelLoading() {
        if (loading != null) {
            loading.dismiss();
        }
    }
}
