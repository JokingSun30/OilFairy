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

public class ConsoleCenter extends BaseFragment<FragmentConsoleCenterBinding, ConsoleCenterViewModel>
        implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    private GoogleMap googleMap;
    private boolean locationPermissionGranted = false;
    private final float DEFAULT_ZOOM = 18f;
    private LatLng recordCameraLatLng;
    private ClusterManager clusterManager;

    @Override
    protected void initView() {
    }

    @Override
    protected void initial() {
        try {
            String jsonFileString = GeneralUtil.getJsonFromAsset(getBaseActivity(), "sample.json");

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

            Log.d("最終測試結果：", gson.toJson(sampleModel));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void initToolBar() {
        hideToolbar();
    }

    @Override
    public void onStart() {
        super.onStart();
        checkMapServiceReady();
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

    private void checkMapServiceReady() {
        Log.d("Google Map", "checking google service version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireActivity());

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d("Google Map", "isServiceOK: Google Play Service is working");
            getLocationPermission();

        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error o but we can resolve it
            Log.d("Google Map", "an error occur but we can resolve it");
        }

    }


    private void getLocationPermission() {
        PermissionCheckHelper helper = new PermissionCheckHelper(getBaseActivity());
        //通過權限
        helper.setOnResultListener(new PermissionCheckHelper.OnResultListener() {
            @Override
            public void onAccessPermission() {
                locationPermissionGranted = true;
                initMap();
            }
        });

        int REQUEST_LOCATION_CODE = 100;
        helper.checkPermission(LOCATION_PERMISSION, REQUEST_LOCATION_CODE);
    }

    private void initMap() {
        FragmentManager fm = getChildFragmentManager();/// getChildFragmentManager();
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();

        fm.beginTransaction().replace(R.id.mapContainer, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        Log.d("Google Map", "Google Map is Ready");

        if (locationPermissionGranted) {
            this.googleMap.setOnCameraIdleListener(this);
            getDeviceLocation();
            setUpCluster();
        }
    }

    public void getDeviceLocation() {
        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(getBaseActivity());

        try {
            if (locationPermissionGranted) {
                if (ActivityCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                Task<Location> location = locationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Google Map", "Location is onComplete");
                        Location currentLocation = task.getResult();
                        moveCamera(new LatLng(currentLocation.getLatitude(),
                                currentLocation.getLongitude()), DEFAULT_ZOOM);

                        Log.d("Google Map", "Location is ：" + currentLocation.getLatitude() + "," +
                                currentLocation.getLongitude());

                    } else {
                        Log.d("Google Map", "Location is not find");
                    }
                });

                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            }

        } catch (Exception e) {
            Log.d("Google Map", "Location find exception" + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d("Google Map", "moveCamera to lat:" + latLng.latitude + ",lng:" + latLng.longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @Override
    public void onCameraIdle() {
        LatLng newLatLng = googleMap.getCameraPosition().target;
        Log.d("Google Map", "onCameraIdle ：lat:" + newLatLng.latitude + "," +
                "lng:" + newLatLng.longitude);

//        if (recordCameraLatLng == null) {
//            recordCameraLatLng = newLatLng;
//
//        } else {

//            long startTime = System.currentTimeMillis();
//            Log.d("執行時間：", "開始：" + startTime);
//            int count = 0;
//
//            float[] results = new float[3];
//            Location.distanceBetween(recordCameraLatLng.latitude, recordCameraLatLng.longitude,
//                    newLatLng.latitude, newLatLng.longitude, results);
//            count++;
//            Log.d("Google Map", "cameraCenter real change：" + results[0] / 1000 + "km");
//
//            MarkerOptions markerOptions = new MarkerOptions()
//                    .position(recordCameraLatLng)
//                    .title("")
//                    .icon(BitmapDescriptorFactory
//                            .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
//
//            //設置地圖圖標
//            if (googleMap != null) {
//                googleMap.addMarker(markerOptions);
//            }
//
//        }

    }

    private void setUpCluster() {
        // Position the map.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(24.08824, 120.537345), 18f));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = new ClusterManager<OilMarkerItem>(getBaseActivity(), googleMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {

        // Set some lat/lng coordinates to start with.
        double lat = 24.08824;
        double lng = 120.537345;

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 60; i++) {
            double offset = i / 1000d;
            lat = lat + offset;
            lng = lng + offset;
            OilMarkerItem offsetItem = new OilMarkerItem(lat, lng, "Title " + i, "Snippet " + i);
            clusterManager.addItem(offsetItem);
        }
    }
}
