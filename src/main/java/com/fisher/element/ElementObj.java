package com.fisher.element;

import com.alibaba.fastjson.JSONObject;

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
    protected boolean alive = true;  // 默认存活


    public ElementObj() {
    }

    public ElementObj(int x, int y, int width, int height, ImageIcon icon) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.icon = icon;
    }

    public ElementObj(ImageIcon icon) {
        this.icon = icon;
    }

    public abstract void showElement(Graphics g);

    public abstract void setSize(Dimension size);

    /**
     * 父类定义键盘监听
     */
    public void keyClick(boolean bl,int key){

    }

    public void update() {

    }
    /**
     *定义对象执行方法顺序
     */
    public final void model(long gameTime){
        updateImage(); // 更新图片
        move(); // 移动
    }


    /**
     * 用于获取对象
     * @return
     */
    public abstract ElementObj createElement(JSONObject jsonObject);

    public void updateImage(){}

    protected void move(){}

    // 添加子弹
    public void add(long gameTime){

    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
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

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
