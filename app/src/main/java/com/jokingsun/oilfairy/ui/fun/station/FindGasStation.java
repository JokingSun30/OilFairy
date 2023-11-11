package com.jokingsun.oilfairy.ui.fun.station;

import android.animation.LayoutTransition;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.jokingsun.oilfairy.BR;
import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.base.BaseFragment;
import com.jokingsun.oilfairy.databinding.CustomInfoContentsBinding;
import com.jokingsun.oilfairy.databinding.FragmentFindGasStationBinding;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class FindGasStation extends BaseFragment<FragmentFindGasStationBinding, FindGasStationViewModel>
        implements OnMapReadyCallback {

    public static final String TAG = "FindGasStation";

    private GoogleMap map;
    private FusedLocationProviderClient locationProviderClient;

    private final LatLng defaultLocation = new LatLng(24.1988535, 120.6418947);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private boolean isDistanceTagShow = false;

    private ArrayList<String> stationResultList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
    }

    @Override
    protected void initView() {
        LayoutTransition lt = new LayoutTransition();
        lt.disableTransitionType(LayoutTransition.DISAPPEARING);
        binding.llDistanceTagMenu.setLayoutTransition(lt);
    }

    @Override
    protected void initial() {
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
        testGetStationResult();
    }

    @Override
    protected void onBackPressed() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();/// getChildFragmentManager();

        SupportMapFragment supportMapFragment;
        supportMapFragment = SupportMapFragment.newInstance();
        fm.beginTransaction().replace(R.id.mapContainer, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);
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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle_night);
        this.map = googleMap;
        map.setMapStyle(style);

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        this.map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(@NonNull Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(@NonNull Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                CustomInfoContentsBinding binding = DataBindingUtil.inflate(getLayoutInflater(),
                        R.layout.custom_info_contents, null, false);
                binding.title.setText(marker.getTitle());
                binding.snippet.setText(marker.getSnippet());

                return binding.getRoot();
            }
        });

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

    }


    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Log.d(TAG, "getDeviceLocation");
                Task<Location> locationResult = locationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this.requireActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });

            } else {
                map.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                map.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (this.getActivity() != null) {

            if (ContextCompat.checkSelfPermission(this.getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;

            } else {
                ActivityCompat.requestPermissions(this.getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }

    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (map == null) {
            Log.d(TAG, "updateLocationUI MAP NULL");
            return;
        }

        Log.d(TAG, "updateLocationUI");
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);

            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    //--------------------------------- 加油站篩選、列表、距離偵測 --------------------------------//

    public void showDistanceTagMenu() {
        isDistanceTagShow = !isDistanceTagShow;
        binding.llDistanceTagMenu.setVisibility(isDistanceTagShow ? View.VISIBLE : View.GONE);
    }

    private void testGetStationResult() {
        for (int i = 0; i < 10; i++) {
            stationResultList.add("");
        }

        StationResultAdapter resultAdapter = new StationResultAdapter(getContext());
        binding.vpStationMenu.setAdapter(resultAdapter);
        resultAdapter.setData(stationResultList);
    }

}
