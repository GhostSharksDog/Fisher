package com.fisher.manager;

public enum FishClass {
    SMALL(5, 30, 50, 3.0, 2,1, 2, 10),
    MEDIUM(10, 50, 100, 2.0, 3,3, 3, 12),
    LARGE(25, 100, 150, 1.5, 4,5, 4, 15);

    private final int score;
    private final int minSize;
    private final int maxSize;
    private final double speed;
    private final int moveInterval;
    private final double sizeFactor; // 新增尺寸大小
    private final int catchLoops; // 捕捉动画循环次数
    private final int catchSpeed; // 捕捉动画速度（帧间隔）

    FishClass(int score, int minSize, int maxSize, double speed, int moveInterval, double sizeFactor,int catchLoops, int catchSpeed) {
        this.score = score;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.speed = speed;
        this.moveInterval = moveInterval;
        this.sizeFactor = sizeFactor;
        this.catchLoops = catchLoops;
        this.catchSpeed = catchSpeed;
    }

    public double getSizeFactor() {
        return sizeFactor;
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

    public int getCatchLoops() {
        return catchLoops;
    }

    public int getCatchSpeed() {
        return catchSpeed;
    }
}