package com.fisher.element;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fisher.controller.Collider;
import com.fisher.manager.ElementManager;
import com.fisher.manager.GameLoad;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Fish extends ElementObj implements Collider {
    public enum Type {
        SMALL(5, 30, 50, 1, 2, 1, 2, 10),
        MEDIUM(10, 50, 100, 1, 3, 1, 3, 12),
        LARGE(25, 100, 150, 0.8, 4, 1, 4, 15);

        private final int score;
        private final int minSize;
        private final int maxSize;
        private final double speed;
        private final int moveInterval;
        private final double sizeFactor; // 新增尺寸大小
        private final int catchLoops; // 捕捉动画循环次数
        private final int catchSpeed; // 捕捉动画速度（帧间隔）

        Type(int score, int minSize, int maxSize, double speed, int moveInterval, double sizeFactor, int catchLoops, int catchSpeed) {
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

    private Random random = new Random();

    // 鱼的基本属性
    private int x, y;          // 位置坐标
    private double preciseX, preciseY;
    private int width, height; // 尺寸
    private Dimension size;    // 屏幕边界
    private double boundaryWidth, boundaryHeight;
    private Type type; // 鱼的种类
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

    }

    @Override
    public void setSize(Dimension size) {

    }

    // 在Fish类的showElement方法中，修改旋转部分：
    @Override
    public void showElement(Graphics g) {
        if (this.getIcon() == null || animationFrames.isEmpty()) return;

        Graphics2D g2d = (Graphics2D) g.create();

        // 使用精确位置计算中心点
        double centerX = preciseX + this.getWidth() / 2.0;
        double centerY = preciseY + this.getHeight() / 2.0;

        // 获取当前帧
        ImageIcon currentIcon = getCurrentIcon();
        Image image = currentIcon.getImage();

        // 计算旋转角度（鱼头初始朝左是180度，所以需要额外旋转180度使鱼头朝向移动方向）
        double rotationAngle = direction + Math.PI;

        // 优化旋转逻辑
        AffineTransform original = g2d.getTransform();
        g2d.translate(centerX, centerY);
        g2d.rotate(rotationAngle); // 使用修正后的旋转角度

        // 从中心绘制
        g2d.drawImage(image,
                -width/2, -height/2,
                width, height, null);

        g2d.setTransform(original);
        g2d.dispose();
    }

    // 添加获取当前帧的方法
    private ImageIcon getCurrentIcon() {
        if (isCatch) {
            if (!catchFrames.isEmpty() && catchFrameIndex < catchFrames.size()) {
                return catchFrames.get(catchFrameIndex);
            }
        } else {
            if (!animationFrames.isEmpty()) {
                return animationFrames.get(currentFrameIndex);
            }
        }
        return getIcon(); // 默认返回基础图标
    }


    @Override
    public ElementObj createElement(JSONObject jsonObject) {
        // 从配置中读取鱼的类型
        String typeStr = jsonObject.getString("type");
        this.type = Type.valueOf(typeStr);

        // 使用枚举中的属性初始化
        this.score = type.getScore();
        this.speed = type.getSpeed();
        this.moveInterval = type.getMoveInterval();
        this.catchAnimationSpeed = type.getCatchSpeed();
        this.catchAnimationLoops = type.getCatchLoops();

        // 初始化位置和方向
        size = ElementManager.getManager().getMainPanelSize();
        this.boundaryWidth = size.getWidth();
        this.boundaryHeight = size.getHeight();
        this.createTime = System.currentTimeMillis();
        this.liveTime = (long)(Math.sqrt(Math.pow(boundaryWidth,2) + Math.pow(boundaryHeight,2)) / speed) * 100;

        // 设置尺寸
        this.width = type.getMinSize();
        this.height = type.getMinSize();

        // 随机位置（确保在边界外）
        int[] siteXY = randomBoundary((int)(boundaryWidth), (int)(boundaryHeight), width, height, random);
        x = siteXY[0];
        y = siteXY[1];
        preciseX = x;
        preciseY = y;

        // 用中心点计算方向
        int fishCenterX = this.x + this.width/2;
        int fishCenterY = this.y + this.height/2;
        direction = calInwardDirection(
                fishCenterX, fishCenterY,
                boundaryWidth/2, boundaryHeight/2,
                random
        );

        // 加载图像资源
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
                double scale = type.getSizeFactor();

                // 计算实际尺寸
                this.width = (int)(originalWidth * scale);
                this.height = (int)(originalHeight * scale);
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
        if (isCatch) return;

        // 使用浮点计算移动增量
        double dx = Math.cos(direction) * speed;
        double dy = Math.sin(direction) * speed;

        // 更新精确位置
        preciseX += dx;
        preciseY += dy;

        // 同步给整数坐标
        x = (int) preciseX;
        y = (int) preciseY;

        // 每帧都移动（取消帧计数器）
        if (random.nextFloat() < 0.05f) {
            // 更小幅度的方向变化（±3度）
            direction += (random.nextDouble() - 0.5) * Math.toRadians(3);
        }
    }

    // 修改边界检查方法
    private void checkBoundary() {
        double centerX = preciseX + width/2.0;
        double centerY = preciseY + height/2.0;

        if (centerX < -100 ||
                centerY < -100 ||
                centerX > boundaryWidth + 100 ||
                centerY > boundaryHeight + 100) {
            isAlive = false;
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
     * @return 指向屏幕内部的方向(弧度)
     */
    // 修改方向计算方法（使用双精度参数）
    private double calInwardDirection(
            double startX, double startY,
            double targetX, double targetY,
            Random random) {

        double dx = targetX - startX;
        double dy = targetY - startY;

        double baseAngle = Math.atan2(dy, dx);

        // 减小随机偏移范围（±15度）
        double randomOffset = (random.nextDouble() - 0.5) * Math.PI / 6;

        return baseAngle + randomOffset;
    }

    // 添加坐标同步方法
    public void syncPosition() {
        if (isCatch) return;

        // 检查是否需要同步
        double diffX = preciseX - x;
        double diffY = preciseY - y;

        // 超过0.5像素才更新（减少无效更新）
        if (Math.abs(diffX) > 0.5 || Math.abs(diffY) > 0.5) {
            x = (int) Math.round(preciseX);
            y = (int) Math.round(preciseY);
        }
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
    public Type getFishType() {
        return type;
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

    public int getCenterX() {
        return (int)(preciseX + width/2);
    }

    public int getCenterY() {
        return (int)(preciseY + height/2);
    }
}