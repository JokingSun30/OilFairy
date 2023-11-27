package com.jokingsun.oilfairy.data.local;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class OilMarkerItem implements ClusterItem {

    private final LatLng position;
    private final String title;
    private final String snippet;
    private final int categoryCode;
    private boolean isSelect;

    public OilMarkerItem(double lat, double lng, String title, String snippet,
                         int categoryCode, boolean isSelect) {
        position = new LatLng(lat, lng);
        this.title = title;
        this.snippet = snippet;
        this.categoryCode = categoryCode;
        this.isSelect = isSelect;
    }

    public int getCategoryCode() {
        return categoryCode;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return position;
    }

    @Nullable
    @Override
    public String getTitle() {
        return title;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return snippet;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
