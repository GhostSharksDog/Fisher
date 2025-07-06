package com.fisher.manager;

import com.alibaba.fastjson.JSONObject;
import com.fisher.element.ElementObj;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class GameLoad {
    //    将所有类加载到classMap中
    private static final Map<String, Class<?>> classMap = new HashMap<>();
    //    将使用道德ICON对象用map缓存，避免重复加载
    private static final Map<String, ImageIcon> iconMap = new HashMap<>();
    private static final GameLoad dataLoader = new GameLoad();

    public static GameLoad getInstance() {
        return dataLoader;
    }

    /**
     * 全局图片资源
     * eg: panelBackground.jpg etc.
     */
    private final Map<String, ImageIcon> imgMap = new HashMap<>();

    private JSONObject jsonObject; // 配置文件中的json对象

    public void load() {
        loadJson(); // 加载配置文件
//        将panelBackground加载到imgMap中
        String panelBackground = jsonObject.getString("panelBackground");
//        存入imgMap
        imgMap.put("panelBackground", new ImageIcon(GameLoad.FindResourceUrl(panelBackground)));
    }

    private void loadJson() {
        try (InputStream inputStream = GameLoad.class.getClassLoader().getResourceAsStream("data.json")) {
            if (inputStream == null) {
                throw new RuntimeException("data.json not found");
            }
            String jsonStr = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            jsonObject = JSONObject.parseObject(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ImageIcon getPanelBackground() {
        return imgMap.get("panelBackground");
    }

    private GameLoad() {
        init();
    }

    protected void init() {
        load(); // 加载配置
        if (jsonObject == null) {
            throw new RuntimeException("data.json not get");
        }
//        从配置文件中将alClass字段加载出来
        JSONObject allClass = jsonObject.getJSONObject("allClass");
        for (String key : allClass.keySet()) {
            String s = allClass.getJSONObject(key).getString("className");
            try {
                classMap.put(key, Class.forName(s));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ElementObj getElement(String key) {
        if (classMap.get(key) == null) {
            return null;
        }
        try {
            ElementObj obj = (ElementObj) classMap.get(key).getDeclaredConstructor().newInstance();
            JSONObject elementJson = jsonObject.getJSONObject("allClass").getJSONObject(key);
            return obj.createElement(elementJson);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查找资源的url
     *
     * @param address 资源地址
     * @return URL
     */
    public static URL FindResourceUrl(String address) {
        return GameLoad.class.getClassLoader().getResource(address);
    }

    public static void main(String[] args) {
//        DataLoader.load();
        new GameLoad();
    }


    /**
     * 通过address，返回ICON对象，
     * 注意：对象路径需要从/开始
     *
     * @param address resources开始的路径
     * @return IMAGE_ICON
     */
    public static ImageIcon findResourceIcon(String address) {
        if (classMap.get(address) != null) {
            return iconMap.get(address);
        }
        try (InputStream resourceAsStream = GameLoad.class.getResourceAsStream(address)) {
            if (resourceAsStream == null) {
                throw new RuntimeException("resource not found");
            }
            BufferedImage read = ImageIO.read(resourceAsStream);
            ImageIcon imageIcon = new ImageIcon(read);
            iconMap.put(address, imageIcon); // 缓存
            return imageIcon;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
