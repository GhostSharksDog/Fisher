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
        SMALL(5, 30, 50, 0.5, 1, 2, 10),
        MEDIUM(10, 50, 100, 0.3,  1, 3, 12),
        LARGE(25, 100, 150, 0.1,  1, 4, 15);

        private final int score;
        private final int minSize;
        private final int maxSize;
        private final double speed;
        private final double sizeFactor; // 新增尺寸大小
        private final int catchLoops; // 捕捉动画循环次数
        private final int catchSpeed; // 捕捉动画速度（帧间隔）

        Type(int score, int minSize, int maxSize, double speed,double sizeFactor, int catchLoops, int catchSpeed) {
            this.score = score;
            this.minSize = minSize;
            this.maxSize = maxSize;
            this.speed = speed;
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
    public double boundaryWidth;
    public double boundaryHeight;
    private Type type; // 鱼的种类
    private ImageIcon icon;    // 图片素材

    // 矢量属性
    private double speed;  // 鱼的移动速度,每帧移动的固定距离
    private double direction;  // 当前移动方向

    // 生存属性
    private boolean isAlive = true;  //鱼的生存状态
    private long createTime;  //鱼的生成时间
    private long liveTime;    //鱼的生存周期
    private Boolean isCatch = false; //鱼是否被捕捉

    // 动画属性
    public List<ImageIcon> animationFrames = new ArrayList<>(); // 存储运动动画帧
    private int currentFrameIndex = 0;        // 当前帧索引
    private int animationSpeed = 10;            // 动画速度（每n帧切换一次）
    private int animationCounter = 0;          // 动画计数器
    public List<ImageIcon> catchFrames = new ArrayList<>();     // 存储被捕捉动画帧
    private int catchFrameIndex = 0;           // 被捕捉动画当前帧
    private int catchFrameCounter = 0;         // 被捕捉动画计数器
    private boolean catchAnimationComplete = false; // 被捕捉动画结束标志
    private int currentLoop = 0;
    private int catchAnimationLoops = 1;       // 动画播放循环次数
    private int catchAnimationSpeed = 5;       // 动画播放间隔

    //积分系统属性
    private int score;

    public String key;

    private boolean directionFixed = false; // 添加方向锁定标志
    private boolean inSchool = false; // 标记是否在鱼群中
    private int groupId = -1; // 鱼群ID
    private Fish groupLeader; // 鱼群领导者

    public Fish() {

    }

    // 确保边界尺寸正确设置
    @Override
    public void setSize(Dimension size) {
        this.size = size;
        this.boundaryWidth = size.getWidth();
        this.boundaryHeight = size.getHeight();
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
        this.key = jsonObject.getString("key");
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
        schoolBehavior();
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

        // 如果是鱼群成员，完全跟随领导者的方向
        if (groupId != -1 && groupLeader != null && groupLeader.isAlive()) {
            this.direction = groupLeader.getDirection();
        }
        // 如果不是鱼群成员，正常移动
        else if (!directionFixed && random.nextFloat() < 0.05f) {
            // 更小幅度的方向变化（±3度）
            direction += (random.nextDouble() - 0.5) * Math.toRadians(3);
        }

        // 使用浮点计算移动增量
        double dx = Math.cos(direction) * speed;
        double dy = Math.sin(direction) * speed;

        // 更新精确位置
        preciseX += dx;
        preciseY += dy;

        // 同步给整数坐标
        x = (int) preciseX;
        y = (int) preciseY;
    }

    // 添加鱼群行为
    private void schoolBehavior() {
        if (!inSchool) return;

        // 10%的概率跟随鱼群方向
        if (random.nextFloat() < 0.1f) {
            // 这里可以添加复杂的鱼群行为算法
            // 简化为小幅调整方向
            direction += (random.nextDouble() - 0.5) * Math.toRadians(5);
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
    /**
     * 生成位于边界外 50 像素范围内的随机坐标
     */
    public static int[] randomBoundary(
            int boundaryWidth, int boundaryHeight,
            int width, int height,
            Random random) {
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
     */
    public static double calInwardDirection(
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

    // 设置位置
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        this.preciseX = x;
        this.preciseY = y;
    }

    // 设置方向
    public void setDirection(double direction) {
        this.direction = direction;
    }

    // 设置鱼的种类
    public void setFishType(Type type) {
        this.type = type;
        // 更新相关属性
        this.score = type.getScore();
        this.speed = type.getSpeed();
    }

    // 获取方向
    public double getDirection() {
        return direction;
    }

    // 获取鱼的key（用于创建相似鱼）
    public String getKey() {
        // 这里需要根据实际情况返回鱼的key
        // 例如："Fish.fish01", "Fish.fish02" 等
        // 您需要在createElement中保存key
        return this.key;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setScore(int score) {
        this.score = score;
    }

    // 添加方向锁定方法
    public void setDirectionFixed(boolean fixed) {
        this.directionFixed = fixed;
    }

    public void setInSchool(boolean inSchool) {
        this.inSchool = inSchool;
    }

    // 设置鱼群ID
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    // 设置鱼群领导者
    public void setGroupLeader(Fish groupLeader) {
        this.groupLeader = groupLeader;
    }

    // 添加设置边界的方法
    public void setBoundary(Dimension size) {
        this.boundaryWidth = size.getWidth();
        this.boundaryHeight = size.getHeight();
    }

    // 添加获取边界的方法
    public Dimension getBoundary() {
        return new Dimension((int)boundaryWidth, (int)boundaryHeight);
    }
}