package com.fisher.show;

import javax.swing.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Stack;

/**
 * 游戏窗体显示
 * Function
 * 嵌入主面板、启动线程等等
 * 窗体说明 swing awt 窗体大小
 * 1.面板绑定到窗体
 * 2.监听绑定
 * 3.游戏主线程启动
 * 4.显示窗体
 */
public class GameJFrame extends JFrame {
    //窗体默认大小
    public static int GameX = 1000;
    public static int GameY = 618;
    private final Stack<JPanel> panelStack = new Stack<>(); // 面板栈, 用于切换面板
    private JPanel panel = null;

    //监听器
    private KeyListener keyListener = null;
    private MouseMotionListener mouseMotionListener = null;
    private MouseListener mouseListener = null;
    //游戏主进程
    private Thread mainThread = null;

    public GameJFrame() {
        init();
    }

    public void init() {
//        this.setSize(GameX, GameY);
        this.setTitle("捕鱼达人");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置JFrame的默认关闭操作
        this.setLocationRelativeTo(null); // 设置JFrame在屏幕居中
    }

    public void addPanel(JPanel panel) {
//        在JFrame中清除旧的panel
        if(!panelStack.isEmpty()) {
            this.getContentPane().removeAll(); // 清除旧面板
        }
        panelStack.push(panel); // 压栈
        this.panel = panelStack.peek(); // 设置当前面板
        this.add(panel); // 添加面板到窗体
    }

    public boolean removePanel() {
        if (panelStack.size() > 1) {
            panelStack.pop();// 弹栈
            this.getContentPane().removeAll(); // 清除旧面板
            this.panel = panelStack.peek(); // 设置当前面板
//            设置新的面板
            this.add(panel);
            return true;
        }
        return false; // 至少保留一个面板, 不能删除
    }

    /**
     * 窗体布局: 添加控件
     */
    public void addButton() {

    }

    /**
     * 启动方法
     */
    public void start() {
        if (panel != null) {
            pack(); // 自动适配大小
        }
        if (keyListener != null) {
            this.addKeyListener(keyListener);
        }
        /*
         显示界面
          多线程启动
         */
        setLocationRelativeTo(null); // 居中
        this.setVisible(true);

        if (mainThread != null) {
            mainThread.start();  //启动线程
        }
        if (this.panel instanceof Runnable) {
            new Thread((Runnable) this.panel).start();
        }
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
