package com.fisher.element;

import com.alibaba.fastjson.JSONObject;
import com.fisher.manager.GameElement;
import com.fisher.manager.GameLoad;

import javax.swing.*;
import java.awt.*;

public class ExplosionEffect extends ElementObj {
    private int x, y;
    private long startTime;
    private static final long DURATION = 500; // 显示500ms

    public ExplosionEffect(int x, int y) {
        this.x = x;
        this.y = y;
        this.startTime = System.currentTimeMillis();
        // 加载爆炸图片
        this.setIcon(GameLoad.findResourceIcon("/before/cannon/tenstar.png"));
    }

    public ExplosionEffect() {
    }

    public ExplosionEffect(int x, int y, int width, int height, ImageIcon icon) {
        super(x, y, width, height, icon);
    }

    public ExplosionEffect(ImageIcon icon) {
        super(icon);
    }

    @Override
    public void showElement(Graphics g) {
        System.out.println("绘制爆炸特效: (" + x + ", " + y + ")");
        if (getIcon() != null) {
            System.out.println("特效图像存在，尺寸: " + getIcon().getIconWidth() + "x" + getIcon().getIconHeight());
            g.drawImage(getIcon().getImage(), x, y, getWidth(), getHeight(), null);
        } else {
            System.err.println("特效图像为null!");
        }
    }

    @Override
    public void setSize(Dimension size) {

    }

    @Override
    public void update() {
        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println("特效存活时间: " + elapsed + "ms / " + DURATION + "ms");

        if (elapsed > DURATION) {
            System.out.println("标记特效为死亡");
            this.setAlive(false);
        }
    }

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public ElementObj createElement(JSONObject jsonObject) {
        return null;
    }
}
