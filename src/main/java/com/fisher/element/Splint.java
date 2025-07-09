package com.fisher.element;

import com.alibaba.fastjson.JSONObject;
import com.fisher.manager.ElementManager;
import com.fisher.manager.GameLoad;

import javax.swing.*;
import java.awt.*;

public class Splint extends ElementObj {
    private double SplintSize = 0.097;

    public Splint() {}

    @Override
    public void showElement(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.drawImage(this.getIcon().getImage(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), null);
        g2d.dispose();
    }

    @Override
    public void setSize(Dimension size) {
        this.setHeight((int) (size.height * this.SplintSize));
        this.setWidth((int) (size.width * this.SplintSize));
        this.setX(size.width / 2 - this.getWidth() / 2);
        this.setY(size.height - this.getHeight());
    }

    @Override
    public void update() {

    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public ElementObj createElement(JSONObject jsonObject) {
        ImageIcon icon = GameLoad.findResourceIcon(jsonObject.getString("splint"));
        this.setIcon(icon);
        this.setSize(ElementManager.getManager().getMainPanelSize());
        return this;
    }
}
