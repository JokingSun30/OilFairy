package com.jokingsun.oilfairy.widget.manager.logevent;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.jokingsun.oilfairy.utils.MultiClickUtil;
import com.jokingsun.oilfairy.widget.manager.logevent.model.ReqDateEventModel;

public class LogEventManager {
    private final FirebaseAnalytics firebaseAnalytics;

    public static LogEventManager getInstance(Context context) {
        return new LogEventManager(context);
    }

    public LogEventManager(Context context) {
        this.firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    //看了約會細節，且按了參加按鈕
    public static final String CONTENT_TYPE_VIEW_DATE_DETAIL_REACTION = "view_date_detail_reaction";
    //看了約會細節，停留了三秒以上
    public static final String CONTENT_TYPE_VIEW_DATE_DETAIL_STAY = "view_date_detail_stay";
    //看了約會列表，且按了參加按鈕
    public static final String CONTENT_TYPE_VIEW_DATE_LIST_REACTION = "view_date_list_reaction";

    /**
     * 紀錄確認報名/猶豫報名事件
     *
     * @param model       涵蓋 約會情境 / id / 費用 / 類型
     * @param confirmJoin 是否為確認? /還是還在猶豫?
     */
    public void logConfirmDateOrNotEvent(ReqDateEventModel model, boolean confirmJoin) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, model.getDateId());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, model.getDateSituation());
        bundle.putString(FirebaseAnalytics.Param.PRICE, model.getDateCharge());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, model.getDateCategory());

        String eventKeyName = confirmJoin ? "confirm_join_date" : "hesitate_join_date";

        Log.d("LogEventManager", "約會事件：" + eventKeyName + "\n" +
                "約會情境：" + model.getDateSituation() + "\n" +
                "約會 ID：" + model.getDateId() + "\n" +
                "約會費用：" + model.getDateCharge() + "\n" +
                "約會類型：" + model.getDateCategory() + "\n");

        MultiClickUtil.clickButton(() -> firebaseAnalytics.logEvent(eventKeyName, bundle));

    }

}
