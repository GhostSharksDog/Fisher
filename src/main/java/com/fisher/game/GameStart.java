package com.fisher.game;

import com.fisher.controller.GameListener;
import com.fisher.controller.GameThread;
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
        GameThread mainThread = new GameThread();

        frame.setPanel(mainPanel);
        frame.setKeyListener(gameListener);
        frame.setMainThread(mainThread);
        frame.start();
    }
}
