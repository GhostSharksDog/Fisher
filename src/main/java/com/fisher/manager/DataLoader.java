package com.fisher.manager;

import com.fisher.element.ElementObj;
import com.fisher.element.FishMap;
import com.fisher.element.Play;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataLoader {
    private final Map<String, Class<?>> map =  new HashMap<>();



    private static DataLoader dataLoader = null;

    public static void loadImg(){
        try (InputStream iStream = DataLoader.class.getClassLoader().getResourceAsStream("img/bg.png")) {

        }catch (IOException e){
            e.printStackTrace();
        }
    }





    public static synchronized DataLoader getDataLoader() {
        if(dataLoader == null){
            dataLoader = new DataLoader();
        }
        return dataLoader;
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
