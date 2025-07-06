package com.fisher.manager;

import com.fisher.element.ElementObj;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Function
 * 元素存储器,存储元素和提供方法
 * 给予视图和控制获取数据
 *
 */
public class ElementManager {
    private List<Object> listMap;  //地图列表
    private List<Object> listPlay; //玩家列表
    private List<Object> listPlayFire; //发射渔网列表
    private Dimension mainPanelSize=new Dimension(0,0);

    public Dimension getMainPanelSize() {
        return mainPanelSize;
    }

    public void setMainPanelSize(Dimension mainPanelSize) {
        this.mainPanelSize = mainPanelSize;
    }

    /**
     * String作为key匹配所有元素
     * 枚举类型 当作Map的key用来区分不一样的资源，用于获取资源
     */
    private Map<GameElement, List<ElementObj>> gameElements;

    public Map<GameElement, List<ElementObj>> getGameElements() {
        return gameElements;
    }

    //添加元素
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
    } //私有化实例化方法

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
}
