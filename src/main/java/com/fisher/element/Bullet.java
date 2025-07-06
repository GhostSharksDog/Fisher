package com.fisher.element;

import javax.swing.*;
import java.awt.*;

public class Bullet extends ElementObj {
    // 子弹角度
    private double angle;
    // 子弹速度
    private double speed = 10;
    // 子弹大小比例
    private double bulletSize = 0.02;
    // 记录子弹创建时间和存活时间（毫秒）
    private long createTime;
    private final long lifeTime = 5000; // 子弹存活时间

    public Bullet(int x, int y, int width, int height, ImageIcon icon, double angle) {
        super(x, y, width, height, icon);
        this.angle = angle;
        this.createTime = System.currentTimeMillis();
    }

    public Bullet(ImageIcon icon, double angle) {
        super(icon);
        this.angle = angle;
        this.createTime = System.currentTimeMillis();
    }

    @Override
    public void update() {
        // 根据角度移动子弹
        this.setX((int)(this.getX() + this.speed * Math.sin(angle)));
        this.setY((int)(this.getY() + this.speed * -Math.cos(angle)));

        // 判断子弹是否超出生存周期
        if (System.currentTimeMillis() - this.createTime > this.lifeTime) {
            this.setAlive(false);
        }
    }

    @Override
    public void showElement(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.drawImage(this.getIcon().getImage(),
                this.getX(), this.getY(),
                this.getWidth(), this.getHeight(), null);
        g2d.dispose();
    }

    @Override
    public void setSize(Dimension size) {
        this.setHeight((int) (size.height * this.bulletSize));
        this.setWidth((int) (size.width * this.bulletSize));
    }

    public void setPosition(int x, int y) {
        this.setX(x);
        this.setY(y);
    }
}
