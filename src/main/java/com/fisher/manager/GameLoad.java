package com.fisher.manager;

import com.alibaba.fastjson.JSONObject;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;
import com.fisher.element.ElementObj;
import com.fisher.element.Fish;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameLoad {
    // 将所有类加载到classMap中
    private static final Map<String, Class<?>> classMap = new HashMap<>();
    // 将使用的ICON对象用map缓存，避免重复加载
    private static final Map<String, ImageIcon> iconMap = new HashMap<>();
    private static final GameLoad dataLoader = new GameLoad();
    // 将使用的大图对象用map缓存，避免重复加载
    private static final Map<String, BufferedImage> bigImgMap = new HashMap<>();

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
        // 将panelBackground加载到imgMap中
        String panelBackground = jsonObject.getString("panelBackground");
        // 存入imgMap
        imgMap.put("panelBackground", new ImageIcon(GameLoad.findResourceUrl(panelBackground)));
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
        // 从配置文件中将alClass字段加载出来
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

    /**
     * 通过key，返回对应的ElementObj对象
     * 
     * @param key data.json的资源字符串
     * eg:Fish.fish1表示使用的资源是data.json中allClass的Fish.fish1字段的数据,
     * 实体类的全类名是allClass.Fish.className,创建对象
     * @return ElementObj对象
     */
    public ElementObj getElement(String key) {
        String[] split = key.split("\\.");
        if (!classMap.containsKey(split[0])) {
            return null;
        }
        try {
            ElementObj obj = (ElementObj) classMap.get(split[0]).newInstance();
            JSONObject allJsonObject = jsonObject.getJSONObject("allClass");
            JSONObject jObject = getJSONObj(split, allJsonObject);
            return obj.createElement(jObject);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * getElement的辅助方法，用于获取层级深度较大的json对象
     * @param split data.json的资源字符串eg:Fish.fish1表示使用的资源是data.json中allClass的Fish.fish1字段
     * @param rJsonObject data.json的json对象
     * @return json对象
     */
    private final JSONObject getJSONObj(String[] split, JSONObject rJsonObject) {
        JSONObject res = rJsonObject;
        for (int i = 0; i < split.length; i++) {
            res = res.getJSONObject(split[i]);
        }
        return res;
    }

    public static void main(String[] args) {
        ElementManager.getManager();
        dataLoader.getElement("Fish.fish1");
    }

    /**
     * 查找资源的url
     *
     * @param address 资源地址
     * @return URL
     */
    public static URL findResourceUrl(String address) {
        return GameLoad.class.getClassLoader().getResource(address);
    }

    /**
     * 通过address，返回ICON对象，
     * 注意：address对应的图片是一张完整的图片，不是多张小图拼接而成的
     * 注意：对象路径需要从/开始，表示从resources开始的路径
     * 
     * @param address resources开始的路径
     * @return IMAGE_ICON
     */
    public static ImageIcon findResourceIcon(String address) {
        if (iconMap.containsKey(address)) {
            return iconMap.get(address);
        }
        try (InputStream resourceAsStream = GameLoad.class.getResourceAsStream(address)) {
            if (resourceAsStream == null) {
                throw new RuntimeException("resource not found in GamaLoad.findResourceIcon");
            }
            BufferedImage read = ImageIO.read(resourceAsStream);
            ImageIcon imageIcon = new ImageIcon(read);
            iconMap.put(address, imageIcon); // 缓存
            return imageIcon;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过bigImgPath，plistPath，smallImgName，返回ICON对象，
     * 注意：bigImgPath对应的图片是多张小图拼接而成的图像
     * 注意：对象路径需要从/开始，表示从resources开始的路径
     * 
     * @param bigImgPath   resources开始的路径
     * @param plistPath    resources开始的路径
     * @param smallImgName 小图名称，plist文件中对应的小图名称
     * @return IMAGE_ICON
     */
    public static ImageIcon findResourceIcon(String bigImgPath, String plistPath, String smallImgName) {
        if (iconMap.containsKey(bigImgPath + smallImgName)) {
            return iconMap.get(bigImgPath + smallImgName);
        }
        // 读取大图的bufferimage
        BufferedImage bigImg = readImage(bigImgPath);
        // 读取plist文件
        try (InputStream resourceAsStream = GameLoad.class.getResourceAsStream(plistPath)) {
            if (resourceAsStream == null) {
                throw new RuntimeException("resource not found in GamaLoad.findResourceIcon");
            }
            NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(resourceAsStream);
            NSDictionary framesDict = (NSDictionary) rootDict.get("frames");
            NSDictionary frameInfo = (NSDictionary) framesDict.get(smallImgName);
            if (frameInfo == null) {
                throw new RuntimeException("smallImgName not found in GamaLoad.findResourceIcon");
            }
            int x = ((NSNumber) frameInfo.get("x")).intValue();
            int y = ((NSNumber) frameInfo.get("y")).intValue();
            int width = ((NSNumber) frameInfo.get("width")).intValue();
            int height = ((NSNumber) frameInfo.get("height")).intValue();
            // 裁剪图片
            BufferedImage smallImg = bigImg.getSubimage(x, y, width, height);
            // 缓存
            ImageIcon imageIcon = new ImageIcon(smallImg);
            iconMap.put(bigImgPath + smallImgName, imageIcon);
            return imageIcon;
        } catch (IOException | PropertyListFormatException | ParseException | ParserConfigurationException
                | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用于加载大图
     * 注意：对象路径需要从/开始，表示从resources开始的路径
     * 
     * @param bigImgPath resources开始的路径
     * @return BufferedImage
     */
    private static BufferedImage readImage(String bigImgPath) {
        if (bigImgMap.containsKey(bigImgPath)) {
            return bigImgMap.get(bigImgPath);
        }
        // 读取图片
        try (InputStream resourceAsStream = GameLoad.class.getResourceAsStream(bigImgPath)) {
            if (resourceAsStream == null) {
                throw new RuntimeException("resource not found in GamaLoad.readImage");
            }
            BufferedImage read = ImageIO.read(resourceAsStream);
            bigImgMap.put(bigImgPath, read); // 缓存
            return read;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
