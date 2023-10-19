package com.jokingsun.oilfairy.data.remote.model.response;

import java.util.List;

public class ResRemoteConfigInfo {

    /**
     * arcadeGamesVersion : 1
     * diffVersions : [{"storeVersion":1,"cardVersion":1,"newsVersion":1},{"storeVersion":1,"cardVersion":1,"newsVersion":1}]
     */

    private int arcadeGamesVersion;
    /**
     * storeVersion : 1
     * cardVersion : 1
     * newsVersion : 1
     */

    private List<DiffVersionsBean> diffVersions;

    public int getArcadeGamesVersion() {
        return arcadeGamesVersion;
    }

    public void setArcadeGamesVersion(int arcadeGamesVersion) {
        this.arcadeGamesVersion = arcadeGamesVersion;
    }

    public List<DiffVersionsBean> getDiffVersions() {
        return diffVersions;
    }

    public void setDiffVersions(List<DiffVersionsBean> diffVersions) {
        this.diffVersions = diffVersions;
    }

    public static class DiffVersionsBean {
        private int storeVersion;
        private int cardVersion;
        private int newsVersion;

        public int getStoreVersion() {
            return storeVersion;
        }

        public void setStoreVersion(int storeVersion) {
            this.storeVersion = storeVersion;
        }

        public int getCardVersion() {
            return cardVersion;
        }

        public void setCardVersion(int cardVersion) {
            this.cardVersion = cardVersion;
        }

        public int getNewsVersion() {
            return newsVersion;
        }

        public void setNewsVersion(int newsVersion) {
            this.newsVersion = newsVersion;
        }
    }
}
