package com.fisher.show;

import javax.swing.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * 游戏窗体显示
 * @Function
 * 嵌入主面板、启动线程等等
 * @窗体说明 swing awt 窗体大小
 * 1.面板绑定到窗体
 * 2.监听绑定
 * 3.游戏主线程启动
 * 4.显示窗体
 */
public class GameJFrame extends JFrame {
    //窗体默认大小
    public static int GameX = 1600;
    public static int GameY = 1200;

    private JPanel panel=null;
    //监听器
    private KeyListener keyListener=null;
    private MouseMotionListener mouseMotionListener=null;
    private MouseListener mouseListener=null;
    //游戏主进程
    private Thread mainThread=null;

    public GameJFrame() {
        init();
    }

    public void init(){
        this.setSize(GameX, GameY);
        this.setTitle("捕鱼达人");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
    /**
     * 窗体布局: 添加控件
     */
    public void addButton(){

    }

    /**
     * 启动方法
     */
    public void start(){
        if(panel!=null){
            this.add(panel);
        }
        if(keyListener!=null){
            this.addKeyListener(keyListener);
        }
        if(mainThread!=null){
            mainThread.start();
        }

        this.setVisible(true);
    }




    public void setPanel(JPanel panel) {
        this.panel = panel;
    }

    public void setKeyListener(KeyListener keyListener) {
        this.keyListener = keyListener;
    }

    public void setMouseMotionListener(MouseMotionListener mouseMotionListener) {
        this.mouseMotionListener = mouseMotionListener;
    }

    public void setMouseListener(MouseListener mouseListener) {
        this.mouseListener = mouseListener;
    }

    public void setMainThread(Thread mainThread) {
        this.mainThread = mainThread;
    }
}
