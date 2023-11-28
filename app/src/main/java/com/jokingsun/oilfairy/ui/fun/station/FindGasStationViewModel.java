package com.jokingsun.oilfairy.ui.fun.station;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.jokingsun.oilfairy.base.BaseViewModel;
import com.jokingsun.oilfairy.data.local.livedata.BaseLiveDataModel;
import com.jokingsun.oilfairy.data.remote.ApiHelper;
import com.jokingsun.oilfairy.ui.fun.console.SampleModel;
import com.jokingsun.oilfairy.utils.GeneralUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FindGasStationViewModel extends BaseViewModel<FindGasStation, BaseLiveDataModel> {

    private ScheduledExecutorService scheduler;
    public MutableLiveData<Boolean> toggleScheduleTracing = new MutableLiveData<>();

    public FindGasStationViewModel(ApiHelper apiHelper) {
        super(apiHelper);
        scheduler = Executors.newScheduledThreadPool(1);
    }

    @Override
    protected void setWhenNetWorkRework() {

    }

    @Override
    protected void attachRepository(FindGasStation navigator) {

    }

    public void startAutoTracing() {
        if (scheduler == null) {
            scheduler = Executors.newScheduledThreadPool(1);
        }

        scheduler.scheduleWithFixedDelay(() -> toggleScheduleTracing.postValue(true), 1,10, TimeUnit.SECONDS);

    }

    public void closeAutoTracing() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    private void readResGasStation(Context context) {
        try {
            String jsonFileString = GeneralUtil.getJsonFromAsset(context, "sample.json");

            JSONArray jsonArray = new JSONArray(jsonFileString);

            ArrayList<SampleModel.DataBean> dataBeans = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                String code = jsonArray.getJSONObject(i).getString("ErrorCode");
                String des = jsonArray.getJSONObject(i).getString("Des");

                SampleModel.DataBean dataBean = new SampleModel.DataBean();
                dataBean.setErrorCode(code);
                dataBean.setDes(des);
                dataBeans.add(dataBean);
            }

            SampleModel sampleModel = new SampleModel();
            sampleModel.setData(dataBeans);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public MutableLiveData<Boolean> getToggleScheduleTracing() {
        return toggleScheduleTracing;
    }
}
