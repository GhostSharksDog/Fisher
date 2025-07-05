package com.fisher.element;

import javax.swing.*;
import java.awt.*;

public class Bullet extends ElementObj {
    // 子弹角度
    private double angle;
    // 子弹速度
    private int speed = 10;

    public Bullet(int x, int y, int width, int height, ImageIcon icon, double angle) {
        super(x, y, width, height, icon);
        this.angle = angle;
    }

    @Override
    public void showElement(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.drawImage(this.getIcon().getImage(),
                this.getX(), this.getY(),
                this.getWidth(), this.getHeight(), null);
        g2d.dispose();
    }

}
