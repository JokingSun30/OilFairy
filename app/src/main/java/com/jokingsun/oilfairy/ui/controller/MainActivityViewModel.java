package com.jokingsun.oilfairy.ui.controller;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jokingsun.oilfairy.base.BaseViewModel;
import com.jokingsun.oilfairy.common.constant.AppConstant;
import com.jokingsun.oilfairy.data.local.livedata.BaseLiveDataModel;
import com.jokingsun.oilfairy.data.local.simple.ApplicationUserSp;
import com.jokingsun.oilfairy.data.remote.ApiHelper;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * @author cfd058
 */
public class MainActivityViewModel extends BaseViewModel<MainActivity, BaseLiveDataModel>
        implements OnSuccessListener<PendingDynamicLinkData>, OnFailureListener {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//    private final DocumentReference supportCardDoc = db.document("ArcadeGame/ArcadeGameListInfo");
//    private final MutableLiveData<List<ReqArcadeGameList.DataBean>> arcadeGameListData = new MutableLiveData<>();

    public MainActivityViewModel(ApiHelper apiHelper) {
        // viewModel 透過 factory 建構後，傳入 ApiHelper Reference
        super(apiHelper);
    }

    @Override
    protected void setWhenNetWorkRework() {

    }

    @Override
    protected void attachRepository(MainActivity navigator) {

    }

    /**
     * -------------About Dynamic Link Response Event (PendingDynamicLinkData) -------------------
     */
    @Override
    public void onFailure(@NonNull Exception e) {
        Logger.i("getDynamicLink:onFailure" + e.getMessage());
    }

    @Override
    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
        // Get deep link from result (may be null if no link is found)
        Logger.i("getDynamicLink:onSuccess");

        if (pendingDynamicLinkData != null) {
            Uri deepLink = pendingDynamicLinkData.getLink();

        }

        if (ApplicationUserSp.isLogin()) {

            if (pendingDynamicLinkData != null) {

                Uri deepLink = pendingDynamicLinkData.getLink();

                Logger.d("Firebase dynamicLinkInfo:" + deepLink.toString() + " host:" + deepLink.getHost());
            }
        }
    }

    /**
     * 取得 Device Token
     */
    public void getDeviceTokenAndLogin() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    ApplicationUserSp.putString(AppConstant.SP_KEY_FCM_TOKEN, token);
                });
        }


}
