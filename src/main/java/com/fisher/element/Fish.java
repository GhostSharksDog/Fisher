package com.fisher.element;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fisher.controller.Collider;
import com.fisher.manager.ElementManager;
import com.fisher.manager.GameLoad;
import com.fisher.manager.FishClass;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Fish extends ElementObj implements Collider {
    private Random random = new Random();

    // 鱼的基本属性
    private int x, y;          // 位置坐标
    private int width, height; // 尺寸
    private Dimension size;    // 屏幕边界
    private double boundaryWidth, boundaryHeight;
    private FishClass fishClass;
    private ImageIcon icon;    // 图片素材

    // 矢量属性
    private double speed;  // 鱼的移动速度,每帧移动的固定距离
    private double direction;  // 当前移动方向
    private int frameCounter = 0;  // 新增帧计数器
    private int moveInterval = 3; // 每3帧移动一次

    // 生存属性
    private boolean isAlive = true;  //鱼的生存状态
    private long createTime;  //鱼的生成时间
    private long liveTime;    //鱼的生存周期
    private Boolean isCatch = false; //鱼是否被捕捉

    // 动画属性
    private List<ImageIcon> animationFrames = new ArrayList<>(); // 存储运动动画帧
    private int currentFrameIndex = 0;        // 当前帧索引
    private int animationSpeed = 5;            // 动画速度（每n帧切换一次）
    private int animationCounter = 0;          // 动画计数器
    private List<ImageIcon> catchFrames = new ArrayList<>();     // 存储被捕捉动画帧
    private int catchFrameIndex = 0;           // 被捕捉动画当前帧
    private int catchFrameCounter = 0;         // 被捕捉动画计数器
    private boolean catchAnimationComplete = false; // 被捕捉动画结束标志
    private int currentLoop = 0;
    private int catchAnimationLoops = 1;       // 动画播放循环次数
    private int catchAnimationSpeed = 5;       // 动画播放间隔

    //积分系统属性
    private int score;

    public Fish() {
        this(FishClass.SMALL);
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

        this.width = fishClass.getMinSize();
        this.height = fishClass.getMinSize();

        this.boundaryWidth = size.getWidth();
        this.boundaryHeight = size.getHeight();
        this.createTime = System.currentTimeMillis();
        this.liveTime = (long)(Math.sqrt(Math.pow(boundaryWidth,2) + Math.pow(boundaryHeight,2)) / speed) * 100;

        this.catchAnimationSpeed = fishClass.getCatchSpeed();
        this.catchAnimationLoops = fishClass.getCatchLoops();


        // 随机位置（确保在边界内）
        int[] siteXY = randomBoundary((int)(boundaryWidth), (int)(boundaryHeight), width, height, random);
        x = siteXY[0];
        y = siteXY[1];

        // 随机初始方向
        direction = calInwardDirection(x,y,(int)(boundaryWidth), (int)(boundaryHeight),random);
    }

    @Override
    public void setSize(Dimension size) {

    }


    @Override
    public void showElement(Graphics g) {
        if (this.getIcon() == null || animationFrames.isEmpty()) {
            return;
        }

        // 转换为 Graphics2D 以支持变换
        Graphics2D g2d = (Graphics2D) g.create();

        // 计算鱼的中心点
        int centerX = this.getX() + this.getWidth() / 2;
        int centerY = this.getY() + this.getHeight() / 2;

        // 保存原始变换
        AffineTransform originalTransform = g2d.getTransform();

        // 创建新变换
        AffineTransform transform = new AffineTransform();

        // 移动到中心点
        transform.translate(centerX, centerY);

        // 计算旋转角度（鱼头初始朝左是180度，所以需要额外旋转180度使鱼头朝向移动方向）
        double rotationAngle = direction + Math.PI;

        // 应用旋转
        transform.rotate(rotationAngle);

        // 移动回原始位置
        transform.translate(-this.getWidth() / 2, -this.getHeight() / 2);

        // 应用变换
        g2d.setTransform(transform);

        // 绘制图像
        g2d.drawImage(this.getIcon().getImage(),
                0, 0,
                this.getWidth(), this.getHeight(), null);

        // 恢复原始变换
        g2d.setTransform(originalTransform);

        // 释放资源
        g2d.dispose();
    }

    @Override
    public ElementObj createElement(JSONObject jsonObject) {

        // 从 fish配置中获取图集信息
        if (jsonObject != null) {
            String bigImage = jsonObject.getString("bigImage");
            String plist = jsonObject.getString("bigImageplist");
            JSONArray normalImages = jsonObject.getJSONArray("imageNormal");
            JSONArray catchImages = jsonObject.getJSONArray("imageCatch");

            // 加载所有动画帧
            if (normalImages != null) {
                for (int i = 0; i < normalImages.size(); i++) {
                    String frameName = normalImages.getString(i);
                    ImageIcon frame = GameLoad.findResourceIcon(bigImage, plist, frameName);
                    animationFrames.add(frame);
                }
                for (int i = 0; i < catchImages.size(); i++) {
                    String frameName = catchImages.getString(i);
                    ImageIcon frame = GameLoad.findResourceIcon(bigImage, plist, frameName);
                    catchFrames.add(frame);
                }

                // 设置初始帧
                if (!animationFrames.isEmpty()) {
                    ImageIcon firstFrame = animationFrames.get(0);
                    setIcon(firstFrame);

                    // 在资源加载后计算实际尺寸
                    int originalWidth = firstFrame.getIconWidth();
                    int originalHeight = firstFrame.getIconHeight();

                    // 计算缩放比例
                    double scale = fishClass.getSizeFactor();

                    // 计算实际尺寸
                    this.width = (int)(originalWidth * scale);
                    this.height = (int)(originalHeight * scale);
                }
            }
        }
        return this;
    }

    /**
     * 更新鱼的位置（每帧调用）
     */
    public void update() {

        updateAnimation();
        move();
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
    public void onClick() {

    }

    public Boolean isCatch(){
        return this.isCatch;
    }

    public void setCatch(Boolean isCatch) {
        this.isCatch = isCatch;
    }

    @Override
    public void move() {
        if (isCatch) {
            return;
        }

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


            // 20%的概率随机改变方向
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

    /**
     * 更新动画方法
     */
    private void updateAnimation() {
        if (!isCatch){
            // 正常游动动画
            if (animationFrames.isEmpty()) {
                System.out.println("Fish.updateAnimation: 动画帧列表为空！");
                return;
            }

            animationCounter++;
            setIcon(animationFrames.get(currentFrameIndex));

            if (animationCounter >= animationSpeed) {
                animationCounter = 0;
                currentFrameIndex = (currentFrameIndex + 1) % animationFrames.size();
                setIcon(animationFrames.get(currentFrameIndex));
            }
        } else {
            // 如果动画已经完成，直接返回
            if (catchAnimationComplete) {
                return;
            }

            if (catchFrames.isEmpty()) {
                System.out.println("Fish.updateAnimation: 被捕捉动画帧列表为空!");
                catchAnimationComplete = true; // 没有动画，直接标记完成
                return;
            }

            catchFrameCounter++;

            // 确保索引在有效范围内
            if (catchFrameIndex < catchFrames.size()) {
                setIcon(catchFrames.get(catchFrameIndex));
            }

            // 使用捕捉动画速度
            if (catchFrameCounter >= catchAnimationSpeed) {
                catchFrameCounter = 0;
                catchFrameIndex++; // 移动到下一帧

                if (catchFrameIndex >= catchFrames.size()) {
                    // 完成一次循环
                    currentLoop++; // 增加循环次数计数器

                    if (currentLoop >= catchAnimationLoops) {
                        // 达到循环次数，动画完成
                        catchAnimationComplete = true;

                    } else {
                        // 重置到第一帧继续播放
                        catchFrameIndex = 0;
                        setIcon(catchFrames.get(catchFrameIndex));
                    }
                } else {
                    // 设置下一帧图片
                    setIcon(catchFrames.get(catchFrameIndex));
                }
            }
        }
    }

    /**
     * 检查捕捉动画是否完成
     */
    public boolean isCatchAnimationComplete() {
        return catchAnimationComplete;
    }

    /**
     * 重置捕捉动画状态
     */
    public void resetCatchAnimation() {
        catchFrameIndex = 0;
        catchFrameCounter = 0;
        catchAnimationComplete = false;
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