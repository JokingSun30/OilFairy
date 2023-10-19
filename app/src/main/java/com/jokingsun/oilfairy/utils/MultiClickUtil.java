package com.jokingsun.oilfairy.utils;

import com.orhanobut.logger.Logger;

/**
 * @author cfd058
 */
public class MultiClickUtil {
    //點擊間隔延遲 1 秒
    private static final int DELAY_TIME = 1000;

    //是否允許點擊
    private static boolean isCanClick = true;

    //上一次點擊時間
    private static long lastClickTime = 0;

    public static void clickButton(OnClickListener listener) {
        //獲取兩次點擊事件的時間差
        long timeDiff = System.currentTimeMillis() - lastClickTime;

        //如果時間差大於設定的間隔時間，則允許用戶點擊
        if (timeDiff > DELAY_TIME) {
            isCanClick = true;
            Logger.d("允許點擊觸發");
        } else {
            isCanClick = false;
            Logger.d("不允許點擊觸發");
        }

        if (isCanClick) {
            lastClickTime = System.currentTimeMillis();
            listener.onClick();
        }
    }

    public interface OnClickListener {
        void onClick();
    }


}
