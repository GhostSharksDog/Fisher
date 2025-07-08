package com.fisher.controller;

import com.fisher.element.Bullet;
import com.fisher.element.ElementObj;
import com.fisher.element.ExplosionEffect;
import com.fisher.element.Fish;
import com.fisher.manager.ElementManager;
import com.fisher.manager.FishClass;
import com.fisher.manager.GameElement;
import com.fisher.manager.GameLoad;

import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

    // 鱼类生成配置
    private final long FISH_GENERATION_INTERVAL = 2000; // 每2秒生成一次鱼（毫秒）
    private long lastFishGenerationTime = 0;
    private final Random random = new Random();


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
        ElementObj goldCoin = GameLoad.getInstance().getElement("GoldItem","{x:200,y:2}");
        EM.addElement(goldCoin, GameElement.PLAYER);
    }

    /**
     * 游戏进行
     */
    private void gameRun() {
        long gameTime = 0L;
        while(true){
            generateFishesContinuously();
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
            checkCollisions();

            //统一删除死亡元素
            removeDeadElements();

            gameTime++;
            EM.GameThreadTime = gameTime; // 更新ElementManager中记录的游戏主线程时间
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


    private void generateFishesContinuously() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFishGenerationTime > FISH_GENERATION_INTERVAL) {
            lastFishGenerationTime = currentTime;

            // 每次生成1-3条鱼
            int fishCount = 1 + random.nextInt(3);
            generateFishes(fishCount);
        }
    }

    public void generateFishes(int count) {
        ColliderManager instance = ColliderManager.getInstance();
        for (int i = 0; i < count; i++) {
            ElementObj fishObj = GameLoad.getInstance().getElement("Fish");
            if (fishObj instanceof Fish) {
                Fish fish = (Fish) fishObj;

                // 根据概率选择鱼的种类
                FishClass fishClass = Fish.getRandomFishClass();

                // 创建新的鱼对象（指定类型）
                Fish newFish = new Fish(fishClass);
                newFish.setIcon(fish.getIcon()); // 保留图像

                EM.addElement(newFish, GameElement.FISH);
                instance.addCollider(newFish);
            }
        }
    }

    private void checkCollisions() {
        List<ElementObj> bullets = EM.getElementByKey(GameElement.BULLET);
        List<ElementObj> fishes = EM.getElementByKey(GameElement.FISH);
        ColliderManager colliderManager = ColliderManager.getInstance();

        // 检测子弹与鱼的碰撞
//      这里不可以使用:的方式循环，因为无法保证线程安全!!!
//        需要修改为:int i=0; i<bullets.size(); i++的方式
        for (int i = 0; i<bullets.size(); i++) {
            ElementObj bulletObj =  bullets.get(i);
            if (!bulletObj.isAlive()) continue;

            Bullet bullet = (Bullet) bulletObj;
            List<ElementObj> collidedFishes = colliderManager.getIntersectColliders(bullet);

            for (ElementObj fishObj : collidedFishes) {
                Fish fish = (Fish) fishObj;
                if (fish.isAlive()) {
                    // 处理碰撞
                    handleCollision(bullet, fish);
                }
            }
        }
    }


    private void handleCollision(Bullet bullet, Fish fish) {
        // 1.子弹消失
        bullet.setAlive(false);

        // 2.播放特效
        createExplosionEffect(fish.getX(), fish.getY());

        // 3.鱼死亡
        fish.setAlive(false);

        // 4.增加分数
        int score = fish.getScore();

//        ScoreManager.addScore(score);

    }

    private void createExplosionEffect(int x, int y) {
        ElementObj effect = GameLoad.getInstance().getElement("ExplosionEffect");
        if (effect instanceof ExplosionEffect) {
            ExplosionEffect explosion = (ExplosionEffect) effect;

            ExplosionEffect newExplosion = new ExplosionEffect(x,y);
            newExplosion.setIcon(explosion.getIcon()); // 保留图像

            EM.addElement(newExplosion, GameElement.EFFECT);
        }
    }

    // 添加移除死亡元素的方法
    private void removeDeadElements() {
        Map<GameElement, List<ElementObj>> all = EM.getGameElements();
        for (GameElement element : all.keySet()) {
            List<ElementObj> list = all.get(element);
            // 使用迭代器安全移除
            Iterator<ElementObj> iterator = list.iterator();
            while (iterator.hasNext()) {
                ElementObj obj = iterator.next();
                if (!obj.isAlive()) {
                    // 如果是碰撞体，从碰撞管理器中移除
                    if (obj instanceof Collider) {
                        ColliderManager.getInstance().removeCollider((Collider) obj);
                    }
                    iterator.remove();
                }
            }
        }
    }
}
