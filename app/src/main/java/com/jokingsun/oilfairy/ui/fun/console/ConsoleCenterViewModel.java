package com.jokingsun.oilfairy.ui.fun.console;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.jokingsun.oilfairy.base.BaseViewModel;
import com.jokingsun.oilfairy.common.constant.OilCommonSign;
import com.jokingsun.oilfairy.common.constant.OilQualityEnum;
import com.jokingsun.oilfairy.data.local.livedata.BaseLiveDataModel;
import com.jokingsun.oilfairy.data.remote.ApiHelper;
import com.jokingsun.oilfairy.data.remote.model.response.ResOilDetailInfo;
import com.jokingsun.oilfairy.data.remote.model.response.ResOilPriceInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsoleCenterViewModel extends BaseViewModel<ConsoleCenter, BaseLiveDataModel> {

    public ConsoleCenterViewModel(ApiHelper apiHelper) {
        super(apiHelper);
    }

    @Override
    protected void setWhenNetWorkRework() {

    }

    @Override
    protected void attachRepository(ConsoleCenter navigator) {

    }
}
