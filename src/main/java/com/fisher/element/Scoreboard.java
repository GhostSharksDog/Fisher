package com.fisher.element;

import com.alibaba.fastjson.JSONObject;
import com.fisher.manager.CoinManager;
import com.fisher.manager.ElementManager;
import com.fisher.manager.GameLoad;

import javax.swing.*;
import java.awt.*;

public class Scoreboard extends ElementObj {
    private final ImageIcon[] icons = new ImageIcon[10];  // 存储 0-9 数字
    private double countDistantRatio = 0.12;  // 数字间距的比例, 相对于元素宽度
    private double ScoreboardRatioHeight = 0.06;
    private double ScoreboardRatioWidth = 0.15;

    public Scoreboard() {}

    @Override
    public void showElement(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.drawImage(this.getIcon().getImage(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), null);

        // 绘制金币数
        int coinCount = CoinManager.getInstance().getCoins();
        this.drawIcons(g2d, coinCount);

        g2d.dispose();
    }

    // 绘制金币数量
    private void drawIcons(Graphics2D g2d, int coinCount) {
        char[] chars = String.valueOf(coinCount).toCharArray();
        int baseX = (int) (this.getX() + this.getWidth() / 3.3);
        for (char c : chars) {
            int x = Character.getNumericValue(c);
            g2d.drawImage(icons[x].getImage(), baseX, (int) (this.getY() + this.getHeight() * 0.18),
                    (int) (this.getWidth() * 0.13), (int) (this.getHeight() * 0.8), null);
            baseX += (int) (this.countDistantRatio * this.getWidth());
        }
    }

    @Override
    public void setSize(Dimension size) {
        this.setWidth((int) (size.width * this.ScoreboardRatioWidth));
        this.setHeight((int) (size.height * this.ScoreboardRatioHeight));
        this.setX((int) ((double) size.width / 2 + this.getWidth() * 1.2));
        this.setY((int) (size.height - this.getHeight() * 1.15));
    }

    @Override
    public void update() {

    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public void onClick() {

    }

    @Override
    public ElementObj createElement(JSONObject jsonObject) {
        ImageIcon icon = GameLoad.findResourceIcon(jsonObject.getString("scoreboard"));
        this.setIcon(icon);
        this.setSize(ElementManager.getManager().getMainPanelSize());

        // 预加载所有数字图标
        for (int i = 0; i < 10; i++) {
            icons[i] = GameLoad.findResourceIcon("/before/componet/num_gold.png",
                    "/before/componet/num_gold.plist", "num_" + i + ".png");
        }

        return this;
    }
}
