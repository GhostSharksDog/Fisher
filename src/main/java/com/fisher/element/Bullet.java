package com.fisher.element;

import com.alibaba.fastjson.JSONObject;
import com.fisher.controller.Collider;
import com.fisher.manager.CannonManager;
import com.fisher.manager.ElementManager;
import com.fisher.manager.GameLoad;

import javax.swing.*;
import java.awt.*;

public class Bullet extends ElementObj implements Collider {
    private double angle;  // 子弹角度
    private double speed = 7; // 子弹速度
    private double bulletSize = 0.04;  // 子弹大小比例
    private long createTime;  // 记录子弹创建时间和存活时间（毫秒）
    private final long lifeTime = 3000; // 子弹存活时间（ms）
    private final ImageIcon[] icons = new ImageIcon[CannonManager.getTopCannonLevel()];;  // 炮弹图片

    public Bullet() {}

    @Override
    public void update() {
        this.bulletTTL();;  // 子弹生存周期

        // 根据角度移动子弹
        this.setX((int)(this.getX() + this.speed * Math.sin(angle)));
        this.setY((int)(this.getY() + this.speed * -Math.cos(angle)));

        // 设置子弹图片
        this.setIcon(this.icons[CannonManager.getCannonLevel() - 1]);
    }

    @Override
    public boolean isAlive() {
        return this.alive;
    }

    @Override
    public void onClick() {

    }

    // 子弹生存周期
    public void bulletTTL() {
        // 判断子弹是否超出生存周期
        if (System.currentTimeMillis() - this.createTime > this.lifeTime) {
            this.setAlive(false);
        }

        // 判断子弹是否超出边界
        if (this.getX() < 0 || this.getX() > ElementManager.getManager().getMainPanelSize().width
                || this.getY() < 0 || this.getY() > ElementManager.getManager().getMainPanelSize().height) {
            this.setAlive(false);
                }
    }

    @Override
    public ElementObj createElement(JSONObject jsonObject) {
        ImageIcon Icon = GameLoad.findResourceIcon("/before/cannon/bulletandnet.png",
                "/before/cannon/bulletandnet.plist", "bullet01.png");

//        ImageIcon Icon = GameLoad.findResourceIcon(jsonObject.getString("bullet"));
        this.setIcon(Icon);
        this.setSize(ElementManager.getManager().getMainPanelSize());

        // 预加载所有炮弹图片
        for (int i = 0; i < CannonManager.getTopCannonLevel(); i++) {
            this.icons[i] = GameLoad.findResourceIcon("/before/cannon/bulletandnet.png",
                    "/before/cannon/bulletandnet.plist", "bullet0" + (i + 1) + ".png");
        }

        return this;

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

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setCreateTime(long l) {
        this.createTime = l;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public ElementObj getThis() {
        return this;
    }
}
