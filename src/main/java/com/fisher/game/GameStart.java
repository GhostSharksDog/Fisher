package com.fisher.game;

import com.fisher.show.GameJFrame;
import com.fisher.show.GameMainPanel;

public class GameStart {
    /**
     * 程序唯一入口
     */
    public static void main(String[] args) {
        GameJFrame frame = new GameJFrame();
        GameMainPanel mainPanel = new GameMainPanel();

        frame.setPanel(mainPanel);
        frame.start();
    }
}
