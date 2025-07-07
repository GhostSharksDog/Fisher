package com.fisher.element;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fisher.manager.ElementManager;
import com.fisher.manager.GameElement;
import com.fisher.manager.GameLoad;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.net.URL;

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

    // 大炮旋转角度（垂直角度）
    private double angle = 0;
    // 开炮时间 ms
    private int fireTime = 200;
    // 上次开炮时间 ms
    private long lastFireTime = 0;
    // 自动开炮线程
    private Thread firingThread;
    // 窗口大小
    private Dimension size;
    // 大炮的大小的比例
    private double widthRatio = 0.04;
    private double HeightRatio = 0.07;
    // 是否自动开炮
    private boolean autoPlay = false;

    public Play() {}

//    public Play(int x, int y, int width, int height, ImageIcon icon) {
//        super(x, y, width, height, icon);
//        this.startAutoFire();
//    }
//
//    public Play(ImageIcon icon) {
//        super(icon);
//        this.startAutoFire();
//    }

    @Override
    public void showElement(Graphics g) {
//      获取panel尺寸
//        System.out.println(ElementManager.getManager().getMainPanelSize());

        if (ElementManager.getManager().isMouseClick()) {
            // 鼠标点击，计算方向并开炮
            int x = ElementManager.getManager().getMousePoint().get(0);
            int y = ElementManager.getManager().getMousePoint().get(1);
            this.pointTo(x, y);
        }

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
//        System.out.println("Play setSize: " + size);
        if (size == null) return;
        this.size = size;
        this.setWidth((int)(size.getWidth() * this.widthRatio));
        this.setHeight((int)(size.getHeight() * this.HeightRatio));
        this.setX((int)(size.getWidth() / 2 - (double) this.getWidth() / 2));
        this.setY((int)(size.getHeight() - this.getHeight()));
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
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastFireTime < this.fireTime) {
            return;
        }
        this.lastFireTime = currentTime;

        // 创建子弹对象
        Bullet bullet = getBullet();

        // 向元素管理器中添加子弹
        ElementManager.getManager().addElement(bullet, GameElement.BULLET);
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
        return bullet;
    }

    // 自动开炮
    public void startAutoFire() {
        this.firingThread = new Thread(() -> {
            while (true) {
                this.fire();
                try {
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


    /**
     * @param jsonObject 数据
     * @return
     */
    @Override
    public ElementObj createElement(JSONObject jsonObject) {
        ImageIcon lIcon = GameLoad.findResourceIcon(jsonObject.getString("canonLeft"));
        ImageIcon rIcon = GameLoad.findResourceIcon(jsonObject.getString("canonRight"));
        ImageIcon cannonIcon = GameLoad.findResourceIcon(jsonObject.getString("cannon"));

        this.setIcon(cannonIcon);
        this.setSize(ElementManager.getManager().getMainPanelSize());

        return this;
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
