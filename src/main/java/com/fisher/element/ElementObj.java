package com.fisher.element;

import javax.swing.*;
import java.awt.*;

/**
 * 所有元素的基类
 */
public abstract class ElementObj {
    private int x;
    private int y;
    private int width;
    private int height;
    private ImageIcon icon;


    public ElementObj() {
    }

    public ElementObj(int x, int y, int width, int height, ImageIcon icon) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.icon = icon;
    }

    public abstract void showElement(Graphics g);

    /**
     * 父类定义键盘监听
     */
    public void keyClick(boolean bl,int key){

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

}
