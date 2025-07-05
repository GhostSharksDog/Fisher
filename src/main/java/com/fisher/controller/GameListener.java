package com.fisher.controller;
import com.fisher.element.ElementObj;
import com.fisher.manager.ElementManager;
import com.fisher.manager.GameElement;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameListener implements KeyListener {
    private ElementManager em = ElementManager.getManager();

    /**
     * 用于记录按下的键，松开后删除set中数据，使按键
     */
    private Set<Integer> set = new HashSet<Integer>();

    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * 按下
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
        //获取玩家元素
        List<ElementObj> play = em.getElementByKey(GameElement.PLAYER);
        for(ElementObj obj : play){
            obj.keyClick();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
