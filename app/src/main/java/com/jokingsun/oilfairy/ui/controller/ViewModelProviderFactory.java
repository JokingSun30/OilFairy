package com.jokingsun.oilfairy.ui.controller;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.jokingsun.oilfairy.data.remote.ApiHelper;
import com.jokingsun.oilfairy.ui.fun.center.PersonalCenterViewModel;
import com.jokingsun.oilfairy.ui.fun.dashboard.HomeDashboardViewModel;
import com.jokingsun.oilfairy.ui.fun.station.FindGasStationViewModel;
import com.orhanobut.logger.Logger;

/**
 * @author cfd058
 */
public class ViewModelProviderFactory extends ViewModelProvider.NewInstanceFactory {

    @SuppressLint("StaticFieldLeak")
    private static ViewModelProviderFactory instance;

    private final ApiHelper apiHelper;

    private ViewModelProviderFactory(Context context) {
        this.apiHelper = new ApiHelper(context, "http://172.105.228.202/voxy/api/");
    }

    public static ViewModelProviderFactory getInstance(Context context) {
        if (instance == null) {
            instance = new ViewModelProviderFactory(context);
        }

        return instance;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //Controller-MainActivity
        if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(apiHelper);
        }

        //Function - Dashboard
        if (modelClass.isAssignableFrom(HomeDashboardViewModel.class)) {
            return (T) new HomeDashboardViewModel(apiHelper);
        }


        //Function - FindGasStation
        if (modelClass.isAssignableFrom(FindGasStationViewModel.class)) {
            return (T) new FindGasStationViewModel(apiHelper);
        }


        //Function - PersonalCenter
        if (modelClass.isAssignableFrom(PersonalCenterViewModel.class)) {
            return (T) new PersonalCenterViewModel(apiHelper);
        }


        Logger.d("getViewModel Error：" + "Unknown ViewModel class: " + modelClass.getName());

        return null;

    }
}

