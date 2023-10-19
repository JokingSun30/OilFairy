package com.jokingsun.oilfairy.widget.helper;

import com.orhanobut.logger.Logger;

/**
 * @author cfd058
 */
public class MultiClickHelper {

    /**
     * 點擊間隔延遲
     */
    private final int delayTime;

    /**
     * 上一次點擊時間
     */
    private long lastClickTime = 0;

    public MultiClickHelper(int delayTime) {
        this.delayTime = delayTime;
    }

    public void clickButton(OnClickListener onClickListener) {
        //獲取兩次點擊事件的時間差
        long timeDiff = System.currentTimeMillis() - lastClickTime;

        //如果時間差大於設定的間隔時間，則允許用戶點擊
        if (timeDiff > delayTime) {
            lastClickTime = System.currentTimeMillis();

            if (onClickListener != null) {
                onClickListener.onClick();
            }

            Logger.d("允許觸發監聽事件");

        } else {
            Logger.d("不允許觸發監聽事件，時間過短");
        }
    }

    public interface OnClickListener {
        void onClick();
    }
}
