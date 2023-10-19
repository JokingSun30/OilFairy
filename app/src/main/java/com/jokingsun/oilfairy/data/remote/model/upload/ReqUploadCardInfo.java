package com.jokingsun.oilfairy.data.remote.model.upload;

import java.util.List;

public class ReqUploadCardInfo {

    private List<DataBean> gameCardList;

    private int version;

    private int gameCode;


    public List<DataBean> getGameCardList() {
        return gameCardList;
    }

    public void setGameCardList(List<DataBean> gameCardList) {
        this.gameCardList = gameCardList;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getGameCode() {
        return gameCode;
    }

    public void setGameCode(int gameCode) {
        this.gameCode = gameCode;
    }


    public static class DataBean {
        private String cardId;

        private String cardName;

        private String photoUrl;

        private boolean isEnable;

        public String getCardId() {
            return cardId;
        }

        public void setCardId(String cardId) {
            this.cardId = cardId;
        }

        public String getCardName() {
            return cardName;
        }

        public void setCardName(String cardName) {
            this.cardName = cardName;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        public boolean isEnable() {
            return isEnable;
        }

        public void setEnable(boolean enable) {
            isEnable = enable;
        }
    }
}
