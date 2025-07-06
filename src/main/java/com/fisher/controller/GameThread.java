package com.fisher.controller;

import com.fisher.element.ElementObj;
import com.fisher.element.Fish;
import com.fisher.element.FishMap;
import com.fisher.element.Play;
import com.fisher.manager.ElementManager;
import com.fisher.manager.GameElement;
import com.fisher.manager.GameLoad;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 用于控制游戏主线程,用于控制游戏加载，游戏关卡，游戏运行时自动化
 *      游戏判定，游戏地图切换， 资源释放和重新读取
 */
public class GameThread extends Thread{
    private ElementManager EM;
    private volatile boolean running = true;
    private volatile boolean paused = false;
    private int currentLevel = 0;   //  当前地图
    private int currentStatus = 0;     //  当前界面状态  0为初始界面，1为关卡选择界面，2为关卡界面
    private Dimension size;

    public GameThread(){
        EM = ElementManager.getManager();
    }

    @Override
    public void run() {
        while(true){  //true换成变量控制结束
        //游戏开始前     读进度条，加载游戏资源
            gameLoad(currentStatus, currentLevel);
        //游戏进行时     游戏过程中
            gameRun();
        //游戏场景结束   游戏资源回收
            gameOver();

            try {
                sleep(25); //40fps
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 游戏加载
     */
    private void gameLoad(int cStatus, int cLevel) {
        loadPlayer();

        generateFishes(5,1);
    }

    /**
     * 游戏进行
     */
    private void gameRun() {
        long gameTime = 0L;
        while(true){
            /*
              读取各类基类，按model顺序执行功能
             */
            Map<GameElement, List<ElementObj>> all = EM.getGameElements();
            for(GameElement e : GameElement.values()){
                List<ElementObj> list = all.get(e);
                for(int i=0; i<list.size(); i++){
                    ElementObj obj = list.get(i);
                    obj.model(gameTime);
                }
            }
            gameTime++;
            //true改为变量控制结束
            try {
                sleep(10); //100fps
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


    public void loadPlayer() {
        ElementObj player = GameLoad.getInstance().getElement("Play");
        EM.addElement(player,GameElement.PLAYER);
    }


    public void generateFishes(int count, double speed) {
        for (int i = 0; i < count; i++) {
            ElementObj fish = GameLoad.getInstance().getElement("Fish"); // 创建鱼对象
            if (fish != null) {
                EM.addElement(fish, GameElement.FISH); // 添加到元素管理器
            }
        }
    }

    public void setSize(Dimension size) {
        this.size = size;
    }

}
