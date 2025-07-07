package com.fisher.element;

import com.alibaba.fastjson.JSONObject;

import java.awt.*;

public class Fish extends ElementObj {
    /**
     * 鱼
     * @param g
     * 鱼会乱游，设置随机数和设计算法使鱼按照一定规则移动，鱼超出屏幕范围外生命周期结束
     * x,y,width,height
     */


    @Override
    public void showElement(Graphics g) {

    }

    @Override
    public void setSize(Dimension size) {

    }

    @Override
    public ElementObj createElement(JSONObject jsonObject) {
        return null;
    }
}