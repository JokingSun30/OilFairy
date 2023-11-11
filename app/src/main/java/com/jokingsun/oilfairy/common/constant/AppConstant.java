package com.jokingsun.oilfairy.common.constant;

import android.Manifest;

import java.security.Permissions;

public class AppConstant {

    public static final String SP_KEY_FCM_TOKEN = "FCM_TOKEN";
    public static final String SP_KEY_IS_ENABLE_TESTER = "IS_ENABLE_TESTER";

    public static final int USE_ANDROID_DIALOG_DEFAULT_THEME = 0;
    public static final int NO_USE_TOOLBAR_LEFT_NAVIGATION = -1;
    public static final int NO_USE_TOOLBAR_RIGHT_NAVIGATION = -2;
    public static final int TYPE_VIEW_HEADER = 0;
    public static final int TYPE_VIEW_CONTENT = 1;
    public static final int TYPE_VIEW_AD_CODE = 2;
    public static final int TYPE_NO_USE_HEADER = 3;
    public static final String TYPE_DATE_TRADITIONAL_FORMAT = "TRADITIONAL";
    public static final String TYPE_DATE_DASH_FORMAT = "DASH";
    public static final String TYPE_DATE_SLASH_FORMAT = "SLASH";
    public static final int TIME_RULE_MINUTES_OF_ONE_HOUR = 60;
    public static final int TIME_RULE_SECONDS_OF_ONE_MINUTES = 60;
    public static final int TIME_RULE_HOUR_OF_ONE_DAY = 24;
    public static final String ACTION_NET_WORK_REWORK = "NET_WORK_REWORK";
    public static final String SECURITY_NUMBER_FORMAT_TW = "[a-zA-Z][1-2][0-9]{8}";
    public static final int GENERAL_PHONE_RULE_TW_LENGTH = 10;
    public static final int GENERAL_MESSAGE_RULE_TW_LENGTH = 6;

    public static final String SYSTEM_OPEN_APP_COUNTS ="OPEN_APP_COUNTS";
    public static final String SYSTEM_ARCADE_GAME_SIGN ="ARCADE_GAME_SIGN";

    public static final String SYSTEM_REMOTE_VERSION_CONFIG ="REMOTE_VERSION_CONFIG";

    public static final String TEST_SHEET_URL = "https://docs.google.com/spreadsheets/d/e/2PACX-1vT2TY28h2J3tA2cqNFLBK0bJ4bFdcFMC6FVsBhThSmCV9Gz0wFqmfJIu66_Ms2g5bCHxsc6q6Nl_xHo/pubhtml?gid=0&single=true";

    public static final String[] LOCATION_PERMISSION = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

}
