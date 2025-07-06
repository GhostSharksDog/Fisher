package com.fisher.controller;

import com.fisher.element.ElementObj;
import com.fisher.element.FishMap;
import com.fisher.element.Play;
import com.fisher.manager.ElementManager;
import com.fisher.manager.GameElement;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

/**
 * 用于控制游戏主线程,用于控制游戏加载，游戏关卡，游戏运行时自动化
 * 游戏判定，游戏地图切换， 资源释放和重新读取
 */
public class GameThread extends Thread {
    private ElementManager EM;
    private Dimension size;

    public GameThread() {
        EM = ElementManager.getManager();
    }

    @Override
    public void run() {
        while (true) {  //true换成变量控制结束
            //游戏开始前     读进度条，加载游戏资源
            gameLoad();
            //游戏进行时     游戏过程中
            gameRun();
            //游戏场景结束   游戏资源回收
            gameOver();

            try {
                sleep(50); //20fps
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 游戏加载
     */
    private void gameLoad() {
        load();
    }

    /**
     * 游戏进行
     */
    private void gameRun() {
        while (true) {

            //true改为变量控制结束
            try {
                sleep(10); // 20fps
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 场景切换
     */
    private void gameOver() {
    }

    public void load() {
//        // 大炮
//        URL cannonUrl = FindImgUrl("image/cannon/00.png");
//        ElementObj cannon = new Play(new ImageIcon(cannonUrl));
//        cannon.setSize(this.size);
//        EM.addElement(cannon, GameElement.PLAYER);
//
//        URL bgUrl = FindImgUrl("image/background/fishlightbg_0.jpg");
//        ElementObj bg = new FishMap(new ImageIcon(bgUrl));
//        bg.setSize(this.size);
//        EM.addElement(bg, GameElement.MAP);
    }

    public URL FindImgUrl(String address) {
        return getClass().getClassLoader().getResource(address);
    }

    public void setSize(Dimension size) {
        this.size = size;
    }
}
