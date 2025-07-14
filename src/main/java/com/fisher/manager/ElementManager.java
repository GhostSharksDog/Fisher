package com.fisher.manager;

import com.fisher.element.ElementObj;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * @Function
 * 元素存储器,存储元素和提供方法
 * 给予视图和控制获取数据
 *
 */
public class ElementManager {
    public String GameMapBgPath; // 地图背景路径
    public long GameThreadTime; // 游戏主线程时间
    private List<Object> listMap;  // 地图列表
    private List<Object> listPlay; // 玩家列表
    private List<Object> listPlayFire; // 发射炮弹列表
    private List<Object> listEffect;   // 爆炸效果列表
    private List<Integer> mousePoint = new ArrayList<>(Arrays.asList(0, 0)); // 初始化默认值
    private boolean isMouseClick;  // 是否点击鼠标
    private Dimension mainPanelSize=new Dimension(10,10);
    
    public Dimension getMainPanelSize() {
        return mainPanelSize;
    }

    public void setMainPanelSize(Dimension mainPanelSize) {
        this.mainPanelSize = mainPanelSize;
    }

    public boolean isMouseClick() {
        return isMouseClick;
    }

    public void setMouseClick(boolean isMouseClick) {
        this.isMouseClick = isMouseClick;
    }

    public synchronized void setMousePoint(int x, int y) {
        SwingUtilities.invokeLater(() -> { // 确保在EDT执行
            mousePoint.clear();
            mousePoint.add(x);
            mousePoint.add(y);
            this.isMouseClick = true;
        });
    }

    public synchronized List<Integer> getMousePoint() {
        return new ArrayList<>(mousePoint); // 返回副本
    }

    /**
     * String作为key匹配所有元素
     * 枚举类型 当作Map的key用来区分不一样的资源，用于获取资源
     */
    private Map<GameElement, List<ElementObj>> gameElements;

    public Map<GameElement, List<ElementObj>> getGameElements() {
        return gameElements;
    }

    // 添加元素
    public void addElement(ElementObj element, GameElement gameElement) {
        gameElements.get(gameElement).add(element);
    }

    //根据key返回list集合取回某元素
    public List<ElementObj> getElementByKey(GameElement element) {
        return gameElements.get(element);
    }

    /**
     * 单例方法获取类对象
     */
    private static ElementManager EM = null; //引用
    //单线程
    public static synchronized ElementManager getManager() {
        if(EM == null) {
            EM = new ElementManager();
        }
        return EM;
    }

    private ElementManager() {
        init();
    }  // 私有化实例化方法

    /**
     * element实例化方法
     */
    public void init() {
        gameElements = new HashMap<GameElement, List<ElementObj>>();
        //将每种元素都放到map中
        for(GameElement e:GameElement.values()) {
            gameElements.put(e,new ArrayList<ElementObj>());
        }

    }

    public void handleElementClick(MouseEvent e) {
        List<ElementObj> left = gameElements.get(GameElement.CannonLeftDecoration);
        List<ElementObj> right = gameElements.get(GameElement.CannonRightDecoration);

        if (!left.isEmpty() || !right.isEmpty()) {
            for (ElementObj obj : left) {
                if (obj.contain(e.getPoint())) {
                    obj.onClick();
                    return;
                }
            }

            for (ElementObj obj : right) {
                if (obj.contain(e.getPoint())) {
                    obj.onClick();
                    return;
                }
            }
        }

        this.setMousePoint(e.getX(), e.getY());

    }
}
