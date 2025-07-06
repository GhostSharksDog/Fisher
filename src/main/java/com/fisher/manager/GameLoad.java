package com.fisher.manager;

import com.fisher.element.ElementObj;
import com.fisher.element.FishMap;
import com.fisher.element.Play;

import javax.swing.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GameLoad {
    // 资源缓存
    private static final Map<Integer, ImageIcon> mapCache = new HashMap<>();
    private static ImageIcon playerIcon = null;

    // 加载指定关卡地图
    public static void loadMap(int level) {
        ElementManager em = ElementManager.getManager();

        // 检查缓存
        ImageIcon mapIcon = mapCache.get(level);
        if (mapIcon == null) {
            // 根据关卡选择不同地图
            String mapPath = getMapPath(level);
            mapIcon = loadImage(mapPath);
            mapCache.put(level, mapIcon);
        }

        // 创建地图元素
        ElementObj map = new FishMap(0, 0, 800, 480, mapIcon);
        em.addElement(map, GameElement.MAP);
    }

    private static String getMapPath(int level) {
        if (level >= 0 && level < 7){
            return  "main/resources/image/background/fishinglightbg_" + level + ".jpg";
        }

        return  "Non exist image";
    }

    // 加载玩家
    public static void loadPlay(String number) {
        if (playerIcon == null) {
            playerIcon = loadImage("image/cannon/" + number + ".png");
        }

        ElementManager em = ElementManager.getManager();
        ElementObj player = new Play(100, 100, 66, 77, playerIcon);
        em.addElement(player, GameElement.PLAYER);
    }


    // 核心加载方法
    private static ImageIcon loadImage(String path) {
        URL imgUrl = GameLoad.class.getClassLoader().getResource(path);
        if (imgUrl == null) {
            System.err.println("资源加载失败: " + path);
            return null;
        }
        return new ImageIcon(imgUrl);
    }
}
