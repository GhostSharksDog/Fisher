package com.fisher.controller;

import com.alibaba.fastjson.JSONObject;
import com.fisher.element.Bullet;
import com.fisher.element.ElementObj;
import com.fisher.element.ExplosionEffect;
import com.fisher.element.Fish;
import com.fisher.manager.*;

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
        loadSplint();
        loadPlayer();
        loadScoreBoard();
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
            List<ElementObj> fishes = EM.getElementByKey(GameElement.FISH);
            for (ElementObj fish : fishes) {
                if (fish instanceof Fish) {
                    ((Fish) fish).syncPosition();
                }
            }
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
        EM.addElement(player, GameElement.PLAYER);
    }

    public void loadSplint() {
        ElementObj splint = GameLoad.getInstance().getElement("Splint");
        EM.addElement(splint, GameElement.SPLINT);
    }

    public void loadScoreBoard() {
        ElementObj scoreBoard = GameLoad.getInstance().getElement("Scoreboard");
        EM.addElement(scoreBoard, GameElement.SCOREBOARD);
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

    /**
     * fish02红色小鱼
     *03绿色小鱼
     *04墨鱼
     *05小丑鱼
     *06黄色小鱼
     *07蓝黄色小鱼
     *08海龟
     *09灯笼鱼
     *10魔鬼鱼
     *13锤头鲨
     *14水母
     *15绿箭
     *17黄青蛙
     */
    public void generateFishes(int count) {
        String[] fishTypes = {
                "Fish.fish01", "Fish.fish02", "Fish.fish03",
                "Fish.fish04", "Fish.fish05", "Fish.fish06",
                "Fish.fish07", "Fish.fish08", "Fish.fish09",
                "Fish.fish10", "Fish.fish13", "Fish.fish14",
                "Fish.fish15", "Fish.fish17"
        };

        for (int i = 0; i < count; i++) {
            String fishKey = fishTypes[random.nextInt(fishTypes.length)];
            ElementObj fishObj = GameLoad.getInstance().getElement(fishKey);

            if (fishObj instanceof Fish) {
                Fish baseFish = (Fish) fishObj;
                // 确保基础鱼有key
                if (baseFish.getKey() == null) {
                    baseFish.key = fishKey; // 显式设置key
                }
                // 确保基础鱼有正确的边界设置
                Dimension size = ElementManager.getManager().getMainPanelSize();
                baseFish.setSize(size);

                // 确保基础鱼有正确的位置
                int[] siteXY = Fish.randomBoundary(
                        (int) size.getWidth(),
                        (int) size.getHeight(),
                        baseFish.getWidth(),
                        baseFish.getHeight(),
                        random
                );
                baseFish.setPosition(siteXY[0], siteXY[1]);

                // 确保基础鱼有正确的方向
                int fishCenterX = siteXY[0] + baseFish.getWidth()/2;
                int fishCenterY = siteXY[1] + baseFish.getHeight()/2;
                double direction = Fish.calInwardDirection(
                        fishCenterX, fishCenterY,
                        size.getWidth()/2, size.getHeight()/2,
                        random
                );
                baseFish.setDirection(direction);

                // 设置基础鱼为鱼群领导者
                baseFish.setGroupId(baseFish.hashCode());
                baseFish.setGroupLeader(baseFish);

                // 使用FishGenerator生成鱼群
                List<Fish> fishGroup = FishGenerator.generateFishGroup(baseFish);

                for (Fish fish : fishGroup) {
                    // 确保每条鱼都有正确的边界设置
                    fish.setSize(size);

                    // 添加鱼类到管理器
                    EM.addElement(fish, GameElement.FISH);
                    // 添加碰撞器
                    Collidercontroller.getInstance().addCollider(fish);
                }
            }
        }
    }

    private void checkCollisions() {
        List<ElementObj> bullets = EM.getElementByKey(GameElement.BULLET);
        List<ElementObj> fishes = EM.getElementByKey(GameElement.FISH);
        Collidercontroller collidercontroller = Collidercontroller.getInstance();

        // 检测子弹与鱼的碰撞
//        这里不可以使用:的方式循环，因为无法保证线程安全!!!
//        需要修改为:int i=0; i<bullets.size(); i++的方式
        for (int i = 0; i<bullets.size(); i++) {
            ElementObj bulletObj =  bullets.get(i);
            if (!bulletObj.isAlive()) continue;

            Bullet bullet = (Bullet) bulletObj;
            List<ElementObj> collidedFishes = collidercontroller.getIntersectColliders(bullet);

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
        Collidercontroller.getInstance().removeCollider(fish);

        // 2.播放特效
        createExplosionEffect(fish.getCenterX(), fish.getCenterY());

        // 3.标记鱼为被捕捉状态
        fish.setCatch(true);
        fish.resetCatchAnimation();

        // 4.增加分数
        CoinManager instance = CoinManager.getInstance();
        instance.addCoins(fish.getScore());

        createGoldItems(fish, fish.getScore());

        // 5. 创建分数显示项
        createScoreItem(fish.getCenterX(), fish.getCenterY(), fish.getScore());
    }

    private void createScoreItem(int centerX, int centerY, int score) {
        JSONObject position = new JSONObject();
        position.put("x", centerX);
        position.put("y", centerY);

        // 根据分数值选择对应的资源路径
        String scoreCategory;
        String scoreValue;

        // 100分以上的鱼使用hundred资源
        if (score >= 100) {
            scoreCategory = "hundred";
            scoreValue = String.valueOf(score);
        }
        // 10-90分的鱼使用highPoint资源
        else {
            scoreCategory = "highPoint";

            // 特殊倍数值处理
            if (score == 10 || score == 20 || score == 30 || score == 35) {
                scoreValue = "x" + score;
            }
            // 普通分数值处理
            else {
                // 向下取整到最近的10的倍数（40、50...90）
                int roundedScore = (score / 10) * 10;
                // 确保在40-90范围内
                scoreValue = String.valueOf(Math.max(40, Math.min(90, roundedScore)));
            }
        }

        // 构建资源key
        String scoreKey = "ScoreItem." + scoreCategory + "." + scoreValue;
        ElementObj scoreItem = GameLoad.getInstance().getElement(scoreKey, position.toJSONString());

        if (scoreItem != null) {
            EM.addElement(scoreItem, GameElement.SCOREITEM);
        } else {
            // 回退机制：使用默认的100分资源
            String fallbackKey = "ScoreItem.hundred.100";
            ElementObj fallbackItem = GameLoad.getInstance().getElement(fallbackKey, position.toJSONString());
            if (fallbackItem != null) {
                EM.addElement(fallbackItem, GameElement.SCOREITEM);
            }
        }
    }


    private void createGoldItems(Fish fish, int score) {
        // 根据鱼的分数创建多个金币
        for (int i = 0; i < Math.min(score, 10); i++) { // 最多生成10个金币
            // 创建金币对象
            ElementObj gold = GameLoad.getInstance().getElement("GoldItem");
            if (gold != null) {
                // 设置金币初始位置为鱼的位置
                JSONObject position = new JSONObject();
                position.put("x", fish.getX() + fish.getWidth()/2);
                position.put("y", fish.getY() + fish.getHeight()/2);

                // 使用运行时数据创建金币
                gold = GameLoad.getInstance().getElement("GoldItem", position.toJSONString());
                ElementManager.getManager().addElement(gold, GameElement.GOLD);
            }
        }
    }

    private void createExplosionEffect(int x, int y) {
        ElementObj effect = GameLoad.getInstance().getElement("ExplosionEffect.effect");
        if (effect instanceof ExplosionEffect) {
            ExplosionEffect explosion = (ExplosionEffect) effect;

            // 使用中心点创建爆炸效果
            ExplosionEffect newExplosion = new ExplosionEffect(x, y);
            newExplosion.setIcon(explosion.getIcon()); // 保留图像
            newExplosion.setWidth(explosion.getWidth());
            newExplosion.setHeight(explosion.getHeight());

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
                // 对于鱼，需要检查捕捉动画是否完成
                if (obj instanceof Fish) {
                    Fish fish = (Fish) obj;
                    if (fish.isCatch() && fish.isCatchAnimationComplete()) {
                        fish.setAlive(false); // 动画完成后标记为死亡
                    }
                }

                if (!obj.isAlive()) {
                    // 如果是碰撞体，从碰撞管理器中移除
                    if (obj instanceof Collider) {
                        Collidercontroller.getInstance().removeCollider((Collider) obj);
                    }
                    iterator.remove();
                }
            }
        }
    }
}
