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
        GameJFrame frame = new GameJFrame(); // 创建窗体
        GameMainPanel mainPanel = new GameMainPanel(); // 创建游戏面板
        GameListener gameListener = new GameListener(); // 创建游戏监听器
        GameThread mainThread = new GameThread(); // 创建游戏主线程

        frame.setPanel(mainPanel); // 注入游戏面板
        frame.setKeyListener(gameListener); // 注入游戏监听器
        frame.setMainThread(mainThread); // 注入游戏主线程
        frame.start(); // 启动游戏
    }
}
