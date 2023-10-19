package com.jokingsun.oilfairy.data.remote.model.upload;

import java.util.ArrayList;
import java.util.List;

public class ReqUploadStoreInfo {
    private int version;

    private int gameCode;

    private List<EachRegionSummary> regionSummaries;

    public int getGameCode() {
        return gameCode;
    }

    public void setGameCode(int gameCode) {
        this.gameCode = gameCode;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<EachRegionSummary> getRegionSummaries() {
        return regionSummaries;
    }

    public void setRegionSummaries(List<EachRegionSummary> regionSummaries) {
        this.regionSummaries = regionSummaries;
    }

    public static class EachRegionSummary {
        private String regionName;

        private int regionCode;

        private ArrayList<DataBean> gameStores;

        public int getRegionCode() {
            return regionCode;
        }

        public void setRegionCode(int regionCode) {
            this.regionCode = regionCode;
        }

        public String getRegionName() {
            return regionName;
        }

        public void setRegionName(String regionName) {
            this.regionName = regionName;
        }

        public ArrayList<DataBean> getGameStores() {
            return gameStores;
        }

        public void setGameStores(ArrayList<DataBean> gameStores) {
            this.gameStores = gameStores;
        }
    }


    public static class DataBean{

        private String storeAddress;

        private String storeName;

        private double latitude;

        private double longitude;

        private boolean haveSellGoods;

        private boolean haveCompetition;

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public String getStoreAddress() {
            return storeAddress;
        }

        public void setStoreAddress(String storeAddress) {
            this.storeAddress = storeAddress;
        }

        public String getStoreName() {
            return storeName;
        }

        public void setStoreName(String storeName) {
            this.storeName = storeName;
        }


        public boolean isHaveSellGoods() {
            return haveSellGoods;
        }

        public void setHaveSellGoods(boolean haveSellGoods) {
            this.haveSellGoods = haveSellGoods;
        }

        public boolean isHaveCompetition() {
            return haveCompetition;
        }

        public void setHaveCompetition(boolean haveCompetition) {
            this.haveCompetition = haveCompetition;
        }
    }
}
