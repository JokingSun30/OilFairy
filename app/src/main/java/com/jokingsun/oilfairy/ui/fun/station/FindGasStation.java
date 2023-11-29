package com.jokingsun.oilfairy.ui.fun.station;

import static com.jokingsun.oilfairy.common.constant.AppConstant.LOCATION_PERMISSION;

import android.Manifest;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;
import com.jokingsun.oilfairy.BR;
import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.base.BaseFragment;
import com.jokingsun.oilfairy.data.local.OilMarkerItem;
import com.jokingsun.oilfairy.databinding.FragmentFindGasStationBinding;
import com.jokingsun.oilfairy.ui.fun.console.SampleModel;
import com.jokingsun.oilfairy.utils.GeneralUtil;
import com.jokingsun.oilfairy.widget.helper.PermissionCheckHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * 尋找附近加油站 Page
 */
public class FindGasStation extends BaseFragment<FragmentFindGasStationBinding, FindGasStationViewModel>
        implements OnMapReadyCallback{

    public static final String TAG = "FindGasStation";
    private static final float DEFAULT_ZOOM = 15f;
    private GoogleMap myMap;
    private ClusterManager<OilMarkerItem> clusterManager;
    private final ArrayList<OilMarkerItem> everSelectItems = new ArrayList<>();
    private FusedLocationProviderClient locationProviderClient;
    private PermissionCheckHelper checkPermissionHelper;
    private final LatLng defaultLocation = new LatLng(25.0399987, 121.501651);
    private Location lastKnownLocation;
    private boolean isDistanceTagShow = false;
    private boolean alreadyGetUserLocation = false;
    private boolean locationPermissionGranted = false;
    private boolean clickAutoTracingBtn = false;

    @Override
    protected void initView() {
        binding.ivRoadSymbol.setImageResource(R.drawable.ic_road_conditions);
        binding.ivRoadSymbol.clearColorFilter();
    }

    @Override
    protected void initial() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(getBaseActivity());
        checkPermissionHelper = new PermissionCheckHelper(getBaseActivity());
        observeAutoTracingToggle();
        checkMapServiceReady();
        getViewModel().startAutoTracing();
    }

    @Override
    protected void initToolBar() {
        hideToolbar();
    }

    @Override
    protected void initSettingHaveVisible() {
    }

    @Override
    protected void loadPageData() {
        super.loadPageData();
        //getLocationPermission();
        // load gas station by asset resource
    }

    @Override
    protected void onBackPressed() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getViewModel().closeAutoTracing();
    }

    @Override
    public int[] getBindingVariable() {
        return new int[]{BR.findGasStationViewModel, BR.findGasStation};
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_find_gas_station;
    }

    @Override
    public FindGasStationViewModel getViewModel() {
        if (viewModel == null) {
            viewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) getFactory())
                    .get(FindGasStationViewModel.class);
        }
        return viewModel;
    }

    //-------------------------------------- 地圖操作 --------------------------------------------//

    /**
     * 初始化地圖
     */
    private void initMap() {
        FragmentManager fm = getChildFragmentManager();/// getChildFragmentManager();
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();

        fm.beginTransaction().replace(R.id.mapContainer, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);
    }

    /**
     * 地圖準備好回調
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d("Google Map", "Google Map is Ready");
        myMap = googleMap;
        myMap.setTrafficEnabled(false);
        myMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle_night));
        setUpClusterManager();

        //開啟App得時候，如果已經有權限，直接取得用戶位置
        if (!checkPermissionHelper.hasPermission(getBaseActivity(), LOCATION_PERMISSION)) {
            moveCamera(defaultLocation);
        }

        getDeviceLocation();
    }

    /**
     * 取得裝置的位置(一定要通過位置權限，如果沒有權限，則詢問用戶，另在 location icon 也會詢問)
     */
    public void getDeviceLocation() {
        //通過權限
        checkPermissionHelper.setOnResultListener(() -> {
            try {
                    if (ActivityCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    locationPermissionGranted = true;
                    Task<Location> location = locationProviderClient.getLastLocation();
                    location.addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            alreadyGetUserLocation = true;

                            Location currentLocation = task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));

                            Log.d("Google Map", "Location is ：" + currentLocation.getLatitude() + "," +
                                    currentLocation.getLongitude());

                        } else {
                            Log.d("Google Map", "Location is not find");
                        }
                    });

                    myMap.setMyLocationEnabled(true);
                    myMap.getUiSettings().setMyLocationButtonEnabled(false);

            } catch (Exception e) {
                Log.d("Google Map", "Location find exception" + e.getMessage());
            }
        });

        int REQUEST_LOCATION_CODE = 100;
        //取的位置的權限
        checkPermissionHelper.checkPermission(LOCATION_PERMISSION, REQUEST_LOCATION_CODE);
    }

    /**
     * 檢查地圖 Service 是否準備好了
     */
    private void checkMapServiceReady() {
        Log.d("Google Map", "checking google service version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireActivity());

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d("Google Map", "isServiceOK: Google Play Service is working");
            initMap();

        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error o but we can resolve it
            Log.d("Google Map", "an error occur but we can resolve it");
        }

    }

    /**
     * 移動鏡頭
     */
    private void moveCamera(LatLng latLng) {
        Log.d("Google Map", "moveCamera to lat:" + latLng.latitude + ",lng:" + latLng.longitude);
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }

    /**
     * 安裝 Google Map 叢集管理者
     */
    @SuppressLint("PotentialBehaviorOverride")
    private void setUpClusterManager() {
        // Position the map.
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = new ClusterManager<>(getBaseActivity(), myMap);

        clusterManager.setOnClusterItemClickListener(item -> {
            for (OilMarkerItem everItem : everSelectItems) {
                OilMarkerItem copy = gson.fromJson(gson.toJson(everItem), OilMarkerItem.class);
                copy.setSelect(false);
                clusterManager.removeItem(everItem);
                clusterManager.addItem(copy);
            }

            everSelectItems.clear();

            OilMarkerItem copy = gson.fromJson(gson.toJson(item), OilMarkerItem.class);
            copy.setSelect(true);

            clusterManager.removeItem(item);
            clusterManager.addItem(copy);
            everSelectItems.add(copy);

            clusterManager.cluster();
            return true;
        });
        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        CustomClusterRenderer clusterRenderer = new CustomClusterRenderer(getContext(), myMap, clusterManager);
        clusterManager.setRenderer(clusterRenderer);
        myMap.setOnCameraIdleListener(clusterManager);
        myMap.setOnMarkerClickListener(clusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {

        // Set some lat/lng coordinates to start with.
        double lat = 24.08824;
        double lng = 120.537345;

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 1000d;
            lat = lat + offset;
            lng = lng + offset;

            OilMarkerItem offsetItem = new OilMarkerItem(lat, lng, "Title " + i, "Snippet " + i,
                    i % 2 == 0 ? 0 : 1, false);
            clusterManager.addItem(offsetItem);
        }
    }

    public void enableRoadConditions() {
        if (myMap == null) {
            return;
        }

        myMap.setTrafficEnabled(!myMap.isTrafficEnabled());

        binding.ivRoadBg.setImageResource(myMap.isTrafficEnabled() ? R.drawable.shape_select_distance_tag_bg
                : R.drawable.shape_unselect_distance_tag_bg);

        if (myMap.isTrafficEnabled()) {
            binding.ivRoadSymbol.setImageResource(R.drawable.ic_road_conditions_simple);
            binding.ivRoadSymbol.setColorFilter(ContextCompat.getColor(getBaseActivity(), R.color.white));

        } else {
            binding.ivRoadSymbol.setImageResource(R.drawable.ic_road_conditions);
            binding.ivRoadSymbol.clearColorFilter();
        }

    }

    //--------------------------------- 加油站篩選、列表、距離偵測 --------------------------------//

    private void testGetStationResult() {
        StationResultAdapter resultAdapter = new StationResultAdapter(getContext());
        binding.vpStationMenu.setAdapter(resultAdapter);
    }

    private void observeAutoTracingToggle() {
        getViewModel().getToggleScheduleTracing().observe(this, toggle -> {
            if (toggle) {
                if (ActivityCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                Task<Location> locationTask = locationProviderClient.getLastLocation();
                locationTask.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Location receiveLocation = task.getResult();

                        if (lastKnownLocation == null) {
                            lastKnownLocation = receiveLocation;
                        }

                        if (clickAutoTracingBtn) {
                            //啟動自動搜尋附近加油站
                            return;
                        }

                        float[] results = new float[3];

//                        Location.distanceBetween(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(),
//                                receiveLocation.getLatitude(), receiveLocation.getLongitude(), results);


                        lastKnownLocation = receiveLocation;
                    }
                });
            }
        });
    }

    public void reSearchStation(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.ivReSearch,"rotation",
                0f,720f).setDuration(1000);
        animator.start();
        showToast("重新搜尋附近加油站");
    }

}
