package com.jokingsun.oilfairy.ui.fun.station;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.data.local.OilMarkerItem;

public class CustomClusterRenderer extends DefaultClusterRenderer<OilMarkerItem> {

    private final Context context;


    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<OilMarkerItem> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(OilMarkerItem item, MarkerOptions markerOptions) {
        int height = item.isSelect() ? 130 : 100;
        int width = item.isSelect() ? 130 : 100;

        Bitmap b = BitmapFactory.decodeResource(context.getResources(), getStationBand(item.getCategoryCode()));
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        // 设置 Marker 的图标
        markerOptions.icon(smallMarkerIcon);
        super.onBeforeClusterItemRendered(item, markerOptions);
    }


    private int getStationBand(int categoryCode) {
        switch (categoryCode) {
            case 0:
                return R.drawable.ic_cpc_marker;
            case 1:
            default:
                return R.drawable.ic_fpg_marker;
        }
    }
}

