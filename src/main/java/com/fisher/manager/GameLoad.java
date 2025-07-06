// GameLoad.java - 资源加载器
package com.fisher.manager;

import com.fisher.element.ElementObj;
import com.fisher.element.FishMap;
import com.fisher.element.Play;
import com.fisher.manager.GameElement;

import javax.swing.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GameLoad {
    private static final Map<Integer, ImageIcon> mapCache = new HashMap<>();
    private static final Map<String, ImageIcon> playerCache = new HashMap<>();

    public static void loadMap(int level) {
        ElementManager em = ElementManager.getManager();

        ImageIcon mapIcon = mapCache.computeIfAbsent(level, k ->
                loadImage(getMapPath(level))
        );

        if (mapIcon != null) {
            ElementObj map = new FishMap(0, 0, 800, 480, mapIcon);
            em.addElement(map, GameElement.MAP);
        }
    }

    private static String getMapPath(int level) {
        return (level >= 0 && level < 7) ?
                "image/background/fishinglightbg_" + level + ".jpg" :
                "image/background/start.jpg";
    }

    public static void loadPlay(String cannonType) {
        ElementManager em = ElementManager.getManager();

        ImageIcon playerIcon = playerCache.computeIfAbsent(cannonType, k ->
                loadImage("image/cannon/" + cannonType + ".png")
        );

        if (playerIcon != null) {
            ElementObj player = new Play(100, 100, 66, 77, playerIcon);
            em.addElement(player, GameElement.PLAYER);
        }
    }

    public static ImageIcon loadImage(String path) {
        URL imgUrl = GameLoad.class.getClassLoader().getResource(path);
        if (imgUrl == null) {
            System.err.println("资源加载失败: " + path);
            return null;
        }
        return new ImageIcon(imgUrl);
    }
}