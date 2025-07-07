package com.fisher.manager;

public enum FishClass {
    SMALL(1, 30, 50, 3.0, 2),   // 积分，最小尺寸，最大尺寸，速度，移动间隔
    MEDIUM(5, 50, 100, 2.0, 3),
    LARGE(25, 100, 150, 1.5, 4);

    private final int score;
    private final int minSize;
    private final int maxSize;
    private final double speed;
    private final int moveInterval;

    FishClass(int score, int minSize, int maxSize, double speed, int moveInterval) {
        this.score = score;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.speed = speed;
        this.moveInterval = moveInterval;
    }

    public int getScore() {
        return score;
    }

    public int getMinSize() {
        return minSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public double getSpeed() {
        return speed;
    }

    public int getMoveInterval() {
        return moveInterval;
    }
}