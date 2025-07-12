package com.fisher.element;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fisher.manager.GameLoad;

import javax.swing.*;
import java.awt.*;

public class ExplosionEffect extends ElementObj {
    private int x, y;
    private int width, height;
    private long startTime;
    private static final long DURATION = 1000; // 显示1000ms

    private boolean isAlive = true;  //生存状态

    public ExplosionEffect(int x, int y) {
        this.x = x;
        this.y = y;
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
            g.drawImage(getIcon().getImage(), x - (int)(getWidth() * 1.4)/2, y - (int)(getHeight() * 1.4)/2, (int)(getWidth() * 1.4), (int)(getHeight() * 1.4), null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, 50, 50);
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
