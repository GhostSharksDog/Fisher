package com.fisher.element;

import java.awt.Dimension;
import java.awt.Graphics;

import com.alibaba.fastjson.JSONObject;

public class Map extends ElementObj {

    @Override
    public void showElement(Graphics g) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'showElement'");
    }

    @Override
    public void setSize(Dimension size) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setSize'");
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public boolean isAlive() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isAlive'");
    }

    @Override
    public void onClick() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onClick'");
    }

    @Override
    public ElementObj createElement(JSONObject jsonObject) {
        
        return this;
    }
    
}
