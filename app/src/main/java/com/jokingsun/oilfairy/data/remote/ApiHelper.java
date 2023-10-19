package com.jokingsun.oilfairy.data.remote;

import android.content.Context;

/**
 * @author cfd058
 */
public class ApiHelper extends BaseApiTool<ApiServices> {

    public ApiHelper(Context context, String baseUrl) {
        super(context, ApiServices.class);
        createDomain(baseUrl);
    }

//    /**
//     * 1-1 驗證登入
//     *
//     * @param callback 驗證登入後 response
//     */
//    public void accountLogin(String phone, String countryCode, String action, String verifyCode,
//                             SimpleCallback<ResVerifyLogin> callback) {
//
//        String deviceToken = ApplicationUserSp.getString(AppConstant.SP_KEY_FCM_TOKEN);
//        String sessionToken = ApplicationUserSp.getToken();
//        String androidId = ApplicationUserSp.getString(AppConstant.ANDROID_DEVICE_PRIVACY_ID);
//
//        Logger.i("Account Login : " + "Device Token : " +
//                deviceToken + "\tSession Token : " + sessionToken +
//                "\tAndroidId : " + androidId +
//                "\tPhone : " + phone + "\tCountryCode : " + countryCode + "\tAction :" + action +
//                "\tVerify Code :" + verifyCode);
//
//        runCall(getServices().accountVerifyLogin(deviceToken, sessionToken, countryCode
//                , androidId, phone, action, verifyCode, AppConstant.OS_TYPE_ANDROID), callback);
//    }

}
