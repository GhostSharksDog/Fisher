package com.fisher.controller;

import com.fisher.element.ElementObj;

import java.awt.*;

public interface Collider {
    /**
     * 获取Rectangle用于碰撞检测
     * @return Rectangle
     */
    Rectangle getBounds();

    /**
     * 获取实现Collider接口的ElementObj自身
     * @return ElementObj
     */
    ElementObj getThis();
}
