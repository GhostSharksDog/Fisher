package com.fisher.element;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fisher.manager.ElementManager;
import com.fisher.manager.GameLoad;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GoldItem extends ElementObj{
    private static final double VELOCITY = 1; // 速度
    private static final int ROTATE_TIME = 5; // 旋转时间
// 金币的iconList
    private final List<ImageIcon> iconList =  new ArrayList<>();
    private int sizeRate = 1; // 金币大小比率
    private int destinationX; // 金币终点x坐标
    private int destinationY; // 金币终点y坐标
    private double x; // 金币当前x坐标
    private double y; // 金币当前y坐标
//    当前显示的icon下标
    private int currentIconIndex = 0;
    @Override
    public void showElement(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
//         绘制金币
        ImageIcon icon = iconList.get(currentIconIndex);
        setWidth(icon.getIconWidth() * sizeRate);
        setHeight(icon.getIconHeight() * sizeRate);
        g2d.drawImage(icon.getImage(), (int)x, (int)y, getWidth(), getHeight(), null);
    }



    @Override
    public void setSize(Dimension size) {
//        更新金币终点位置为屏幕正下方
        destinationX = size.width / 2 - getWidth() / 2;
        destinationY = size.height -  getHeight() / 2;
    }

    @Override
    public void update() {
//        更新金币图片状态
        if(ElementManager.getManager().GameThreadTime % ROTATE_TIME == 0){
            currentIconIndex = (currentIconIndex + 1) % iconList.size();
        }
//         移动金币
//         计算剩余移动距离
        double dis = Math.sqrt(Math.pow(destinationX - x, 2) + Math.pow(destinationY - y, 2));
        System.out.println("dis = " + dis);
        if(dis <= VELOCITY){
            x = destinationX;
            y = destinationY;
//            System.out.println("到达终点");
        }else{
            double rate = VELOCITY / dis;
            x = (1 - rate)*x+rate*destinationX;
            y = (1 - rate)*y+rate*destinationY;
        }
    }

    @Override
    public boolean isAlive() {

        return true;
    }

    @Override
    public ElementObj createElement(JSONObject jsonObject) {
//     处理iconList
        JSONArray j = jsonObject.getJSONArray("goldImages"); // 得到小图图片数组
        String plistPath = jsonObject.getString("goldPlist"); // 得到plist路径
        String bigImgPath = jsonObject.getString("goldImage"); // 得到大图路径
        int x = jsonObject.getIntValue("x"); // 得到x坐标
        int y = jsonObject.getIntValue("y");
//     初始化iconList
        for (int i = 0; i < j.size(); i++) {
            String s = j.getString(i); // 得到小图图片名
            ImageIcon smallIcon = GameLoad.findResourceIcon(bigImgPath, plistPath, s);
            iconList.add(smallIcon); // 添加到iconList
        }
        this.x = x; // 设置x坐标
        this.y = y; // 设置y坐标
//      ________
        return this;
    }
}
