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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.jokingsun.oilfairy.BR;
import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.base.BaseFragment;
import com.jokingsun.oilfairy.databinding.FragmentConsoleCenterBinding;
import com.jokingsun.oilfairy.widget.helper.PermissionCheckHelper;

public class ConsoleCenter extends BaseFragment<FragmentConsoleCenterBinding, ConsoleCenterViewModel>
        implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private boolean locationPermissionGranted = false;
    private final float DEFAULT_ZOOM = 18f;

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
            getDeviceLocation();
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
}
