package com.fisher.manager;

import com.fisher.element.ElementObj;
import com.fisher.element.FishMap;
import com.fisher.element.Play;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DataLoader {
    private static final Map<String, Class<?>> map =  new HashMap<>();

    public static void load{
//        加载配置文件
        try (InputStream inputStream = DataLoader.class.getClassLoader().getResourceAsStream("data.json")) {
            if(inputStream == null){
                throw new RuntimeException("data.json not found");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
//            使用fastjson解析json
//            生成json字符串
            String jsonStr = reader.readLine();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public static ImageIcon getPanelBackground(){
        return new ImageIcon(Objects.requireNonNull(DataLoader.class.getClassLoader().getResource("panelBackground.jpg")));
    }



    private DataLoader(){
        init();
    }

    protected void init(){
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
}
