package com.fisher.game;

import com.fisher.controller.GameListener;
import com.fisher.show.GameJFrame;
import com.fisher.show.GameMainPanel;

import java.awt.*;

public class GameStart {
    /**
     * 程序唯一入口
     */
    public static void main(String[] args) {
        GameJFrame frame = new GameJFrame();
        GameMainPanel mainPanel = new GameMainPanel();
        GameListener gameListener = new GameListener();

        frame.setPanel(mainPanel);
        frame.setKeyListener(gameListener);
        frame.start();
    }
}
