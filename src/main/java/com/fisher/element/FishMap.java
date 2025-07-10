package com.fisher.element;

import com.alibaba.fastjson.JSONObject;

import javax.swing.*;
import java.awt.*;

public class FishMap extends ElementObj {

    public FishMap(int x, int y, int width, int height, ImageIcon icon) {
        super(x, y, width, height, icon);
    }

    public FishMap(ImageIcon icon) {
        super(icon);
    }

    @Override
    public void showElement(Graphics g) {
        g.drawImage(this.getIcon().getImage(),
                this.getX(),this.getY(),
                this.getWidth(),this.getHeight(),null);
    }

    @Override
    public void setSize(Dimension size) {
        if (size == null) return;
        this.setHeight(size.height);
        this.setWidth(size.width);
    }

    @Override
    public void update() {

    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public void onClick() {

    }

    @Override
    public ElementObj createElement(JSONObject jsonObject) {
        return this;
    }

}
