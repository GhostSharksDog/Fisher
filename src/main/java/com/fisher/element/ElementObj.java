package com.fisher.element;

import javax.swing.*;
import java.awt.*;

/**
 * 所有元素的基类
 */
public class ElementObj {
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

}
