package com.fisher.element;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.alibaba.fastjson.JSONObject;
import com.fisher.manager.ElementManager;
import com.fisher.manager.GameElement;
import com.fisher.manager.GameLoad;

public class ScoreItem extends ElementObj {
    private int startTime; // 开始时间
    private int liveTime = 200; // 存活时间
    private List<ImageIcon> images = new ArrayList<>(); // 图片
    private int currentIconIndex = 0; // 当前图片索引

    /**
     * 绘制分数版
     */
    @Override
    public void showElement(Graphics g) {
        // 绘制分数版
        ImageIcon icon = images.get(currentIconIndex);
        g.drawImage(icon.getImage(), getX(), getY(), icon.getIconWidth(), icon.getIconHeight(), null);
    }

    @Override
    public void setSize(Dimension size) {
    }

    @Override
    public void update() {
        if(ElementManager.getManager().GameThreadTime % 5 == 0){
            currentIconIndex = (currentIconIndex + 1) % images.size();
        }
    }

    @Override
    public boolean isAlive() {
        if(ElementManager.getManager().GameThreadTime - startTime >= liveTime){
            return false;
        }
        return true;
    }

    @Override
    public void onClick() {
    }

    @Override
    public ElementObj createElement(JSONObject jsonObject) {
        // 为this对象设置属性
        // 将jsonObject中的属性设置到this对象中
        /*
         * {
         * x:0,
         * y:0,
         * "bigImage":"/before/score/hundred.png",
         * "bigImageplist":"/before/score/hundred.plist",
         * "images":[
         * "120_1.png",
         * "120_2.png",
         * "120_3.png",
         * "120_4.png",
         * "120_5.png",
         * "120_6.png",
         * "120_7.png",
         * "120_8.png"
         * ]
         * }
         */
        startTime = (int) ElementManager.getManager().GameThreadTime; // 记录开始时间
        int x = jsonObject.getIntValue("x");
        int y = jsonObject.getIntValue("y");
        String bigImage = jsonObject.getString("bigImage");
        String bigImageplist = jsonObject.getString("bigImageplist");
        List<String> images = jsonObject.getJSONArray("images").toJavaList(String.class);
        for (String image : images) {
            this.images.add(GameLoad.findResourceIcon(bigImage, bigImageplist, image));
        }
        setX(x);
        setY(y);
        return this;
    }

}
