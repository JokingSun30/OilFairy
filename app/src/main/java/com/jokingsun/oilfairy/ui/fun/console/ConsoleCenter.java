package com.jokingsun.oilfairy.ui.fun.console;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.jokingsun.oilfairy.BR;
import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.base.BaseFragment;
import com.jokingsun.oilfairy.base.BaseViewModel;
import com.jokingsun.oilfairy.common.constant.OilCommonSign;
import com.jokingsun.oilfairy.data.remote.model.response.ResOilPriceInfo;
import com.jokingsun.oilfairy.data.remote.model.upload.ReqUploadStoreInfo;
import com.jokingsun.oilfairy.databinding.FragmentConsoleCenterBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsoleCenter extends BaseFragment<FragmentConsoleCenterBinding, ConsoleCenterViewModel> {
    @Override
    protected void initView() {
        isMapServiceReady();
    }

    @Override
    protected void initial() {
        setLazyLoadTime(0);
    }

    @Override
    protected void initToolBar() {
        hideToolbar();
    }

    @Override
    protected void initSettingHaveVisible() {

    }

    @Override
    protected void onBackPressed() {

    }

    @Override
    public int[] getBindingVariable() {
        return new int[]{BR.consoleCenterViewModel, BR.consoleCenter};
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_console_center;
    }

    @Override
    public ConsoleCenterViewModel getViewModel() {
        if (viewModel == null) {
            viewModel = new ViewModelProvider(this, getFactory()).get(ConsoleCenterViewModel.class);
        }
        return viewModel;
    }

    private boolean isMapServiceReady() {
        Log.d("Google Map ", "checking google service version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireActivity());

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d("Google Map ", "isServiceOK: Google Play Service is working");
            return true;

        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error o but we can resolve it
            Log.d("iGoogle Map ", "an error occur but we can resolve it");
            return false;
        }

        return false;
    }
}
