package com.jokingsun.oilfairy.data.remote.model.upload;

import java.util.List;

public class ReqUploadNewsContest {

    private String gameDomain;

    private int gameCode;

    private List<NewsDataBean> gameNewsList;

    private List<ContestDataBean> gameContestList;

    private int version;

    public String getGameDomain() {
        return gameDomain;
    }

    public void setGameDomain(String gameDomain) {
        this.gameDomain = gameDomain;
    }

    public int getGameCode() {
        return gameCode;
    }

    public void setGameCode(int gameCode) {
        this.gameCode = gameCode;
    }

    public List<NewsDataBean> getGameNewsList() {
        return gameNewsList;
    }

    public void setGameNewsList(List<NewsDataBean> gameNewsList) {
        this.gameNewsList = gameNewsList;
    }

    public List<ContestDataBean> getGameContestList() {
        return gameContestList;
    }

    public void setGameContestList(List<ContestDataBean> gameContestList) {
        this.gameContestList = gameContestList;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }


    public static class NewsDataBean {
        private String newsLink;
        private String newsDate;
        private String newsType;

        private int newsTypeSign;
        private String newsContent;

        public int getNewsTypeSign() {
            return newsTypeSign;
        }

        public void setNewsTypeSign(int newTypeSign) {
            this.newsTypeSign = newTypeSign;
        }

        public String getNewsLink() {
            return newsLink;
        }

        public void setNewsLink(String newsLink) {
            this.newsLink = newsLink;
        }

        public String getNewsDate() {
            return newsDate;
        }

        public void setNewsDate(String newsDate) {
            this.newsDate = newsDate;
        }

        public String getNewsType() {
            return newsType;
        }

        public void setNewsType(String newsType) {
            this.newsType = newsType;
        }

        public String getNewsContent() {
            return newsContent;
        }

        public void setNewsContent(String newsContent) {
            this.newsContent = newsContent;
        }
    }

    public static class ContestDataBean{
        private String contestLink;
        private String contestDate;
        private String storeCompleteName;
        private String storeSimpleName;

        public String getContestLink() {
            return contestLink;
        }

        public void setContestLink(String contestLink) {
            this.contestLink = contestLink;
        }

        public String getContestDate() {
            return contestDate;
        }

        public void setContestDate(String contestDate) {
            this.contestDate = contestDate;
        }

        public String getStoreCompleteName() {
            return storeCompleteName;
        }

        public void setStoreCompleteName(String storeCompleteName) {
            this.storeCompleteName = storeCompleteName;
        }

        public String getStoreSimpleName() {
            return storeSimpleName;
        }

        public void setStoreSimpleName(String storeSimpleName) {
            this.storeSimpleName = storeSimpleName;
        }
    }
}
