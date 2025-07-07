package com.fisher.element;

import com.alibaba.fastjson.JSONObject;
import com.fisher.manager.ElementManager;
import com.fisher.manager.GameLoad;

import javax.swing.*;
import java.awt.*;

public class CannonRightDecoration extends ElementObj {
    private double ratio = 0.05;
    private int cannonX;
    private int cannonY;
    private int cannonWidth;

    public CannonRightDecoration() {}

    @Override
    public void showElement(Graphics g) {
        if (getIcon() == null) return;

        g.drawImage(getIcon().getImage(),
                getX(), getY(),
                getWidth(), getHeight(),
                null);
    }

    @Override
    public void setSize(Dimension size) {
        this.setHeight((int) (ElementManager.getManager().getMainPanelSize().height * this.ratio));
        this.setWidth((int) (ElementManager.getManager().getMainPanelSize().width * this.ratio));

        this.setX(this.cannonX + this.cannonWidth);
        this.setY(ElementManager.getManager().getMainPanelSize().height - this.getHeight());
    }

    public void setCannonPosition(int x, int y, int width) {
        this.cannonX = x;
        this.cannonY = y;
        this.cannonWidth = width;
    }

    @Override
    public void update() {

    }

    @Override
    public boolean isAlive() {
        return true;  // 装饰物始终显示
    }

    @Override
    public ElementObj createElement(JSONObject jsonObject) {
        ImageIcon Icon = GameLoad.findResourceIcon(jsonObject.getString("canonRight"));
        this.setIcon(Icon);
        this.setSize(ElementManager.getManager().getMainPanelSize());
        return this;
    }
}

