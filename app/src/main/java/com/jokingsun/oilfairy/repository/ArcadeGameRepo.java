package com.jokingsun.oilfairy.repository;


public class ArcadeGameRepo {
    private volatile static ArcadeGameRepo instance;

    private ArcadeGameRepo() {
    }

    public static ArcadeGameRepo getInstance() {
        if (instance == null) {
            synchronized (ArcadeGameRepo.class) {
                if (instance == null) {
                    instance = new ArcadeGameRepo();
                }
            }
        }
        return instance;
    }
}
