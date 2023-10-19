package com.jokingsun.oilfairy.common.constant;

public enum ArcadeGameEnum {
    /**
     * 定義街機遊戲列舉
     */
    POKEMON(0,"Pokemon Ga-Ole");

    private final int gameCode;
    private final String gameName;

    ArcadeGameEnum(int gameCode, String gameName) {
        this.gameCode = gameCode;
        this.gameName = gameName;
    }

    public int getGameCode() {
        return gameCode;
    }

    public String getGameName() {
        return gameName;
    }
}
