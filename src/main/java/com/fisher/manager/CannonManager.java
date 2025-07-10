package com.fisher.manager;

public class CannonManager {
    private static final int topCannonLevel = 8;
    private static int cannonLevel = 1;
    private static CannonManager instance = new CannonManager();

    private CannonManager() {}

    public static int getTopCannonLevel() {
        return topCannonLevel;
    }

    public static CannonManager getInstance() {
        return instance;
    }

    public static int getCannonLevel() {
        return cannonLevel;
    }

    public void addCannonLevel() {
        if (topCannonLevel > cannonLevel) cannonLevel++;
    }

    public void reduceCannonLevel() {
        if (cannonLevel > 1) cannonLevel--;
    }

}
