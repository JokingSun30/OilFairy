package com.jokingsun.oilfairy.ui.fun.console;

import static com.jokingsun.oilfairy.common.constant.AppConstant.LOCATION_PERMISSION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.L;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.auth.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.jokingsun.oilfairy.BR;
import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.base.BaseFragment;
import com.jokingsun.oilfairy.data.local.OilMarkerItem;
import com.jokingsun.oilfairy.databinding.FragmentConsoleCenterBinding;
import com.jokingsun.oilfairy.utils.GeneralUtil;
import com.jokingsun.oilfairy.widget.helper.PermissionCheckHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConsoleCenter extends BaseFragment<FragmentConsoleCenterBinding, ConsoleCenterViewModel> {

    @Override
    protected void initView() {
    }

    @Override
    protected void initial() {
    }

    @Override
    protected void initToolBar() {
        hideToolbar();
    }

    @Override
    public void onStart() {
        super.onStart();
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

}
