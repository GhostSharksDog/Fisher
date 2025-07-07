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

    }

    @Override
    public void setSize(Dimension size) {

    }

    @Override
    public void update() {

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
