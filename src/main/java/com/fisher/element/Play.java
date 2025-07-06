package com.fisher.element;

import com.fisher.manager.ElementManager;
import com.fisher.manager.GameElement;

import javax.swing.*;
import java.awt.*;
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
    private int fireTime = 300;
    // 上次开炮时间 ms
    private long lastFireTime = 0;

    public Play(int x, int y, int width, int height, ImageIcon icon) {
        super(x, y, width, height, icon);
    }

    @Override
    public void showElement(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // 计算中心点
        int cx = this.getX() + this.getWidth() / 2;
        int cy = this.getY() + this.getHeight() / 2;

        // 设置旋转中心并旋转
        g2d.rotate(angle, cx, cy);
//        System.out.println("angle:" + angle);

        // 绘制图像（注意坐标是旋转前的）
        g2d.drawImage(this.getIcon().getImage(),
                this.getX(), this.getY(),
                this.getWidth(), this.getHeight(), null);

        g2d.dispose(); // 释放Graphics2D资源
    }

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

    // 开炮！
    public void fire() {
        long currentTime = System.currentTimeMillis();
        if (System.currentTimeMillis() - currentTime < this.fireTime) {
            return;
        }
        lastFireTime = currentTime;

        // 计算子弹起始位置(炮口位置)
        int bulletX = this.getX() + this.getWidth() / 2;
        int bulletY = this.getY() + this.getHeight() / 2;

        // 加载子弹图片
        URL bulletUrl = getClass().getClassLoader().getResource("image/change/img_3.png");
        ImageIcon bulletIcon = new ImageIcon(bulletUrl != null ? bulletUrl.getFile() : null);

        // 创建子弹对象
        Bullet bullet = new Bullet(bulletX, bulletY, 20, 20, bulletIcon, angle);

        // 添加到元素管理器
        ElementManager.getManager().addElement(bullet, GameElement.BULLET);

    }


    public void keyClick(boolean b, int keycode) {
        if(b){
            switch(keycode){

            }
        }
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
