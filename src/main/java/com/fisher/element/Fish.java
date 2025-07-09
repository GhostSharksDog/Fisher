package com.fisher.element;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fisher.controller.Collider;
import com.fisher.manager.ElementManager;
import com.fisher.manager.GameLoad;
import com.fisher.manager.FishClass;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Fish extends ElementObj implements Collider {

    // 鱼的基本属性
    private int x, y;          // 位置坐标
    private int width, height; // 尺寸
    private Dimension size;    // 屏幕边界
    private double boundaryWidth, boundaryHeight;
    private int score;         // 鱼类积分
    private FishClass fishClass;

    private double speed;  // 鱼的移动速度,每帧移动的固定距离
    private double direction;  // 当前移动方向

    private ImageIcon icon;

    private boolean isAlive = true;  //鱼的生存状态
    private long createTime;  //鱼的生成时间
    private long liveTime;    //鱼的生存周期

    private int frameCounter = 0;  // 新增帧计数器
    private int moveInterval = 3; // 每3帧移动一次（可调整）

    private Random random = new Random();

    public Fish() {
        // 无参构造函数
        this(getRandomFishClass());
    }

    /**
     * 获取随机鱼种
     */
    public static FishClass getRandomFishClass() {
        Random rand = new Random();
        double randomValue = rand.nextDouble() * 100;

        if (randomValue <= 50) {
            return FishClass.SMALL;
        } else if (randomValue <= 95) {
            return FishClass.MEDIUM;
        } else {
            return FishClass.LARGE;
        }
    }


    /**
     * 鱼的构造函数
     * @param fishClass 鱼的三种等级
     * 鱼
     * 鱼会乱游，设置随机数和设计算法使鱼按照一定规则移动，鱼超出屏幕范围外生命周期结束
     * x,y,width,height
     */
    public Fish(FishClass fishClass) {
        this.fishClass = fishClass;
        size = ElementManager.getManager().getMainPanelSize();

        // 使用枚举中的属性
        this.score = fishClass.getScore();
        this.speed = fishClass.getSpeed();
        this.moveInterval = fishClass.getMoveInterval();

        // 随机尺寸在最小和最大之间
        this.width = fishClass.getMinSize() + random.nextInt(fishClass.getMaxSize() - fishClass.getMinSize());
        this.height = width;

        this.boundaryWidth = size.getWidth();
        this.boundaryHeight = size.getHeight();
        this.createTime = System.currentTimeMillis();
        this.liveTime = (long)(Math.sqrt(Math.pow(boundaryWidth,2) + Math.pow(boundaryHeight,2)) / speed) * 100;

        // 随机位置（确保在边界内）
        int[] siteXY = randomBoundary((int)(boundaryWidth), (int)(boundaryHeight), width, height, random);
        x = siteXY[0];
        y = siteXY[1];

        // 随机初始方向
        direction = calInwardDirection(x,y,(int)(boundaryWidth), (int)(boundaryHeight),random);
    }

    @Override
    public void showElement(Graphics g) {
        if (this.getIcon() != null) {
            g.drawImage(this.getIcon().getImage(),
                    this.getX(), this.getY(),
                    this.getWidth(), this.getHeight(), null);
        }

    }

    @Override
    public void setSize(Dimension size) {

    }

    @Override
    public ElementObj createElement(JSONObject jsonObject) {
        // 尝试获取直接图片路径

        // 从 fish1 配置中获取图集信息
        JSONObject fish1Config = jsonObject.getJSONObject("fish01");
        if (fish1Config != null) {
            String bigImage = fish1Config.getString("bigImage");
            String plist = fish1Config.getString("bigImageplist");
            JSONArray normalImages = fish1Config.getJSONArray("imageNormal");

            if (normalImages != null && normalImages.size() > 0) {
                // 获取第一张正常状态的鱼图片
                String firstNormalImage = normalImages.getString(0);
                this.setIcon(GameLoad.findResourceIcon(bigImage, plist, firstNormalImage));
            }
        }

        // 如果以上都没有
        if (this.getIcon() == null) {
            System.err.println("无法加载鱼图片");
        }

        return this;
    }

    /**
     * 更新鱼的位置（每帧调用）
     */
    public void update() {
        if (System.currentTimeMillis() - createTime > liveTime) {
            isAlive = false; // 标记为需要销毁
            return;
        }
        frameCounter++;
        if (frameCounter >= moveInterval) {
            frameCounter = 0; // 重置计数器
            // 计算移动向量
            int dx = (int) (Math.cos(direction) * speed);
            int dy = (int) (Math.sin(direction) * speed);

            // 更新位置
            x += dx;
            y += dy;

//            System.out.println("element.Fish： 成功位移x: " + x + ", y: " + y);

            // 10%的概率随机改变方向
            if (random.nextFloat() < 0.2f) {
                // 计算最大偏移量（5度转换为弧度）
                double maxOffset = Math.toRadians(5);
                // 生成-5度到+5度之间的随机偏移
                double randomOffset = (random.nextDouble() * 2 - 1) * maxOffset;
                // 应用偏移到当前方向
                direction += randomOffset;
            }
        }
    }

    @Override
    public void setAlive(boolean alive) {
        this.isAlive = alive;
    }

    @Override
    public boolean isAlive() {
        return this.isAlive;
    }

    @Override
    public void move() {
        update();
    }

    /**
     * 生成位于边界外 50 像素范围内的随机坐标
     * @param boundaryWidth 边界宽度
     * @param boundaryHeight 边界高度
     * @param width 对象宽度（避免对象部分出现在边界内）
     * @param height 对象高度（避免对象部分出现在边界内）
     * @param random 随机数生成器
     * @return 包含 x 和 y 坐标的数组 [x, y]
     */
    public static int[] randomBoundary(
            int boundaryWidth, int boundaryHeight,
            int width, int height,
            Random random
    ) {
        int margin = 50; // 边界外 50 像素范围
        int side = random.nextInt(4); // 随机选择边界的一侧：0=上，1=右，2=下，3=左

        int x, y;

        switch (side) {
            case 0: // 上边界外
                x = random.nextInt(boundaryWidth);
                y = -height - random.nextInt(margin);
                break;
            case 1: // 右边界外
                x = boundaryWidth + random.nextInt(margin);
                y = random.nextInt(boundaryHeight);
                break;
            case 2: // 下边界外
                x = random.nextInt(boundaryWidth);
                y = boundaryHeight + random.nextInt(margin);
                break;
            case 3: // 左边界外
                x = -width - random.nextInt(margin);
                y = random.nextInt(boundaryHeight);
                break;
            default: // 默认情况（理论上不会执行）
                x = random.nextInt(boundaryWidth);
                y = random.nextInt(boundaryHeight);
        }

        return new int[]{x, y};
    }

    /**
     * 计算从边界外指向屏幕内部的初始方向
     * @param x 鱼的x坐标
     * @param y 鱼的y坐标
     * @param boundaryWidth 边界宽度
     * @param boundaryHeight 边界高度
     * @return 指向屏幕内部的方向(弧度)
     */
    private double calInwardDirection(int x, int y, int boundaryWidth, int boundaryHeight, Random random) {
        // 计算屏幕中心点
        int centerX = boundaryWidth / 2;
        int centerY = boundaryHeight / 2;

        // 计算指向中心的方向向量
        int dx = centerX - x;
        int dy = centerY - y;

        // 基础方向（指向中心）
        double baseDirection = Math.atan2(dy, dx);

        // 随机偏移量（±60度范围，即 ±π/3 弧度）
        double randomOffset = (random.nextDouble() - 0.5) * Math.PI / 2.5;

        // 返回带偏移的方向
        return baseDirection + randomOffset;
    }

    /**
     * 检查鱼是否应该被销毁
     * @return true如果应该被销毁
     */
    public boolean shouldDestroy() {
        return !isAlive ||
                System.currentTimeMillis() - createTime > liveTime;
    }


    // --- Getter 方法 ---
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Point getPosition() {
        return new Point(x, y);
    }

    // 添加获取鱼等级和积分的方法
    public FishClass getFishClass() {
        return fishClass;
    }

    public int getScore() {
        return score;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public ElementObj getThis() {
        return this;
    }
}
