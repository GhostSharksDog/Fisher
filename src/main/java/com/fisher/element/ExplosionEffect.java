package com.fisher.element;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fisher.manager.GameLoad;

import javax.swing.*;
import java.awt.*;

public class ExplosionEffect extends ElementObj {
    private int x, y;
    private int centerX, centerY; // 存储中心点坐标
    private int width, height;
    private long startTime;
    private static final long DURATION = 1000; // 显示1000ms

    private boolean isAlive = true;  //生存状态

    public ExplosionEffect(int centerX, int centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void setSize(Dimension size) {
        // 设置固定尺寸或按比例
        this.setWidth(100);
        this.setHeight(100);
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
        if (getIcon() != null) {
            // 计算左上角坐标，使图像中心在指定位置
            int drawX = centerX - getWidth() / 2;
            int drawY = centerY - getHeight() / 2;

            // 绘制图像（放大1.4倍）
            int scaledWidth = (int)(getWidth() * 1.4);
            int scaledHeight = (int)(getHeight() * 1.4);
            int scaledX = centerX - scaledWidth / 2;
            int scaledY = centerY - scaledHeight / 2;

            g.drawImage(getIcon().getImage(), scaledX, scaledY, scaledWidth, scaledHeight, null);
        } else {
            // 错误处理
            g.setColor(Color.RED);
            g.fillRect(centerX - 25, centerY - 25, 50, 50);
            System.err.println("特效图像为null!");
        }
    }


    @Override
    public void update() {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > DURATION) {
            this.setAlive(false);
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
    public void onClick() {

    }

    @Override
    public ElementObj createElement(JSONObject jsonObject) {
        if (jsonObject != null) {
            String bigImage = jsonObject.getString("bigImage");
            String plist = jsonObject.getString("bigImageplist");
            JSONArray imageNormal = jsonObject.getJSONArray("imageNormal");

            if (imageNormal != null) {
                String effectIcon = imageNormal.getString(5);
                setIcon(GameLoad.findResourceIcon(bigImage, plist, effectIcon));
            }
        }else{
            System.err.println("没有找到图片!");
        }

        return this;
    }

}
