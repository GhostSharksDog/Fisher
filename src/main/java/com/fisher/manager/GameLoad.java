package com.fisher.manager;

import com.alibaba.fastjson.JSONObject;
import com.fisher.element.ElementObj;
import com.fisher.element.FishMap;
import com.fisher.element.Play;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class GameLoad {
    private static final Map<String, Class<?>> map =  new HashMap<>();

    private static final GameLoad dataLoader = new GameLoad();

    public static GameLoad getInstance(){
        return dataLoader;
    }
    /**
     * 全局图片资源
     * eg: panelBackground.jpg etc.
     */
    private final Map<String,ImageIcon> imgMap = new HashMap<>();

    private JSONObject jsonObject; // 配置文件中的json对象

    public void load(){
        loadJson(); // 加载配置文件
//        将panelBackground加载到imgMap中
        String panelBackground = jsonObject.getString("panelBackground");
//        存入imgMap
        imgMap.put("panelBackground",new ImageIcon(GameLoad.FindResourceUrl(panelBackground)));
    }

    private void loadJson(){
        try (InputStream inputStream = GameLoad.class.getClassLoader().getResourceAsStream("data.json")) {
            if(inputStream == null){
                throw new RuntimeException("data.json not found");
            }
            String jsonStr = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            jsonObject = JSONObject.parseObject(jsonStr);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public ImageIcon getPanelBackground(){
        return imgMap.get("panelBackground");
    }

    private GameLoad(){
        init();
    }

    protected void init(){
        load(); // 加载配置
//        设置map
        map.put("ElementObj", ElementObj.class);
        map.put("FishMap", FishMap.class);
        map.put("Play", Play.class);
    }

    public ElementObj getElement(String key) {
        if(map.get(key) == null){
            return null;
        }
        try {
            return (ElementObj) map.get(key).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查找资源的url
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
}
