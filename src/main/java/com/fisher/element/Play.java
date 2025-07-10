package com.fisher.element;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fisher.controller.Collidercontroller;
import com.fisher.manager.*;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class Play extends ElementObj {
    /**
     * Play玩家属性
     * 效果： 玩家控制鼠标，大炮跟随鼠标转向，定时发射炮弹，捕鱼积分阶段性增长，大炮和炮弹种类会随等级变化
     * 玩家积分
     * 大炮等级
     * 炮弹类型
     * 发射间隔
     * 旋转角度
     * 大炮位置 x，y
     * 大炮图片宽高 width，height
     * 大炮图片素材 imgMap
     */
    private Map<String,ImageIcon> imgMap;
    private double angle = 0;  // 大炮旋转角度（垂直角度）
    private int fireTime = 100;  // 开炮时间 ms
    private long lastFireTime = 0;  // 上次开炮时间 ms
    private Thread firingThread;  // 开炮线程
    private Dimension size;  // 窗口大小
    // 大炮的大小的比例
    private double widthRatio = 0.08;
    private double HeightRatio = 0.16;
    // 大炮装饰比例
    private CannonRightDecoration rightDecoration;
    private CannonLeftDecoration leftDecoration;
    // 存储配置
    private static JSONArray jsonArray;

    public Play() {}

    @Override
    public void showElement(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // 计算中心点
        int cx = this.getX() + this.getWidth() / 2;
        int cy = this.getY() + this.getHeight() / 2;

        // 设置旋转中心并旋转
        g2d.rotate(angle, cx, cy);
        // 绘制图像（注意坐标是旋转前的）
        g2d.drawImage(this.getIcon().getImage(),
                this.getX(), this.getY(),
                this.getWidth(), this.getHeight(), null);
        g2d.dispose(); // 释放Graphics2D资源
    }

    @Override
    public void setSize(Dimension size) {
        if (size == null) return;
        this.size = size;
        this.setWidth((int)(size.getWidth() * this.widthRatio));
        this.setHeight((int)(size.getHeight() * this.HeightRatio));
        this.setX((int)(size.getWidth() / 2 - (double) this.getWidth() / 2));
        this.setY((int)(size.getHeight() - this.getHeight() * 0.85));

        if (this.leftDecoration!= null && this.rightDecoration!= null) {
            this.leftDecoration.setCannonPosition(this.getX(), this.getY(), this.getWidth());
            this.rightDecoration.setCannonPosition(this.getX(), this.getY(), this.getWidth());
        }
    }

    // 计算鼠标指向的方向并开炮
    public void pointTo(int x, int y) {
        // 计算大炮中心点
        int cannonX = this.getX() + this.getWidth() / 2;
        int cannonY = this.getY() + this.getHeight() / 2;

        // 计算目标点相对于大炮中心的偏移量
        double dx = x - cannonX;
        double dy = cannonY - y;

        // 计算角度
        this.angle = Math.atan2(dx, dy);

        // 开炮
        this.fire();
    }

    // 开炮！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
    public void fire() {
        Collidercontroller collidercontroller = new Collidercontroller();
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastFireTime < this.fireTime) {
            return;
        }
        this.lastFireTime = currentTime;

        // 减少金币
        if (CoinManager.getInstance().getCoins() < CannonManager.getCannonLevel()) return;  // 金币不够，无法发射炮弹
        CoinManager.getInstance().reduceCoins(CannonManager.getCannonLevel());

        // 创建子弹对象
        Bullet bullet = getBullet();

        // 向元素管理器中添加子弹
        ElementManager.getManager().addElement(bullet, GameElement.BULLET);
        collidercontroller.addCollider(bullet);

        startFire(bullet);
    }

    // 获得子弹对象
    private Bullet getBullet() {
        Bullet bullet = (Bullet) GameLoad.getInstance().getElement("Bullet");
        bullet.setSize(this.size);

        // 计算子弹起始位置 (炮口位置)
        int cannonCenterX = this.getX() + this.getWidth() / 2 - bullet.getWidth() / 2;
        int cannonCenterY = this.getY() + this.getHeight() / 2 - bullet.getHeight() / 2;
        double barrelLength = this.getHeight() * 0.6; // 炮管长度取炮身高度百分比
        int bulletX = (int) (cannonCenterX + barrelLength * Math.sin(this.angle));
        int bulletY = (int) (cannonCenterY - barrelLength * Math.cos(this.angle));

        bullet.setPosition(bulletX, bulletY);
        bullet.setAngle(this.angle);
        bullet.setCreateTime(System.currentTimeMillis());

        return bullet;
    }

    // 开炮
    public void startFire(Bullet bullet) {
        this.firingThread = new Thread(() -> {
            while (true) {
                try {
                    bullet.update();
                    Thread.sleep(this.fireTime);;
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        this.firingThread.start();
    }

    public void keyClick(boolean b, int keycode) {
        if(b){
            switch(keycode){

            }
        }
    }

    @Override
    public void update() {
        if (ElementManager.getManager().isMouseClick()) {
            // 鼠标点击，计算方向并开炮
            int x = ElementManager.getManager().getMousePoint().get(0);
            int y = ElementManager.getManager().getMousePoint().get(1);
            this.pointTo(x, y);
            ElementManager.getManager().setMouseClick(false);
        }

        // 更新炮图标
        if (jsonArray != null) {
            this.setIcon(GameLoad.findResourceIcon(jsonArray.getString(CannonManager.getCannonLevel() - 1)));
        }

    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public void onClick() {

    }

    /**
     * @param jsonObject 数据
     * @return
     */
    // 创建大炮
    @Override
    public ElementObj createElement(JSONObject jsonObject) {
        jsonArray = jsonObject.getJSONArray("cannon");

        // 瓦达西的炮
        ImageIcon cannonIcon = GameLoad.findResourceIcon(jsonArray.getString(CannonManager.getCannonLevel() - 1));
        this.setIcon(cannonIcon);
        this.setSize(ElementManager.getManager().getMainPanelSize());

        // 瓦达西的炮：你们都是我的翅膀
        this.createDecoration(jsonObject);

        return this;
    }

    public void createDecoration(JSONObject jsonObject) {
        this.leftDecoration = (CannonLeftDecoration) GameLoad.getInstance().getElement("CannonLeftDecoration");
        this.rightDecoration = (CannonRightDecoration) GameLoad.getInstance().getElement("CannonRightDecoration");

        this.leftDecoration.createElement(jsonObject);
        this.rightDecoration.createElement(jsonObject);

        this.leftDecoration.setCannonPosition(this.getX(), this.getY(), this.getWidth());
        this.rightDecoration.setCannonPosition(this.getX(), this.getY(), this.getWidth());

        this.leftDecoration.setSize(ElementManager.getManager().getMainPanelSize());
        this.rightDecoration.setSize(ElementManager.getManager().getMainPanelSize());

        ElementManager.getManager().addElement(this.leftDecoration, GameElement.CannonLeftDecoration);
        ElementManager.getManager().addElement(this.rightDecoration, GameElement.CannonRightDecoration);

    }

    @Override
    public void updateImage() {

    }

    @Override
    public void move() {

    }

    @Override
    public void add(long gameTime) {

    }
}
