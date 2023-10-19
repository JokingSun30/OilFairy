package com.jokingsun.oilfairy.data.remote.model.upload;

import java.util.List;

public class ReqArcadeGameList {

    private int version;

    private List<DataBean> gameList;

    public List<DataBean> getGameList() {
        return gameList;
    }

    public int getVersion() {
        return version;
    }
    public void setGameList(List<DataBean> gameList) {
        this.gameList = gameList;
    }

    public static class DataBean {
        private String gameCardPath;
        private int gameCode;
        private String gameLogoUrl;
        private String gameName;
        private String gameNewsPath;
        private String gameStorePath;

        public String getGameCardPath() {
            return gameCardPath;
        }

        public void setGameCardPath(String gameCardPath) {
            this.gameCardPath = gameCardPath;
        }

        public int getGameCode() {
            return gameCode;
        }

        public void setGameCode(int gameCode) {
            this.gameCode = gameCode;
        }

        public String getGameLogoUrl() {
            return gameLogoUrl;
        }

        public void setGameLogoUrl(String gameLogoUrl) {
            this.gameLogoUrl = gameLogoUrl;
        }

        public String getGameName() {
            return gameName;
        }

        public void setGameName(String gameName) {
            this.gameName = gameName;
        }

        public String getGameNewsPath() {
            return gameNewsPath;
        }

        public void setGameNewsPath(String gameNewsPath) {
            this.gameNewsPath = gameNewsPath;
        }

        public String getGameStorePath() {
            return gameStorePath;
        }

        public void setGameStorePath(String gameStorePath) {
            this.gameStorePath = gameStorePath;
        }
    }

}
