package com.fisher.game;

import com.fisher.controller.GameListener;
import com.fisher.controller.GameThread;
import com.fisher.show.GameJFrame;
import com.fisher.show.GameMainPanel;
import com.fisher.show.StartPanel;


public class GameStart {
    /**
     * 程序唯一入口
     */
    public static void main(String[] args) {
        GameJFrame frame = new GameJFrame(); // 创建窗体
        GameListener gameListener = new GameListener(); // 创建游戏监听器
        GameThread mainThread = new GameThread(); // 创建游戏主线程

        StartPanel startPanel = new StartPanel(e -> {
            // 开始游戏
            frame.addPanel(new GameMainPanel()); // 将游戏主面板注入窗体
            frame.start(); // 启动游戏
        }, e -> {
            // 退出游戏
            System.exit(0);
        });// 创建开始面板

        frame.addPanel(startPanel); // 注入开始面板
        frame.start(); // 启动游戏
        frame.setKeyListener(gameListener); // 注入游戏监听器
        frame.setMainThread(mainThread); // 注入游戏主线程
    }
}
