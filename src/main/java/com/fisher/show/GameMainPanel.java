package com.fisher.show;

import com.fisher.element.ElementObj;
import com.fisher.element.FishMap;
import com.fisher.element.Play;
import com.fisher.manager.ElementManager;
import com.fisher.manager.GameElement;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 游戏主面板
 * 元素的显示
 * 界面的刷新
 * 多线程刷新
 */
public class GameMainPanel extends JPanel implements Runnable {
    // 联动管理器，调用元素
    private ElementManager EM;
    // 背景图片
    private ImageIcon background;

    public GameMainPanel() {
        init();

    }

    public void init() {
        EM = ElementManager.getManager();
//        设置面板大小
        setPreferredSize(new Dimension(1000,618));
        load();
    }

    public void load() {
        URL imgUrl2 = FindImgUrl("image/background/fishlightbg_0.jpg");
        if (imgUrl2 == null) return;
        this.background = new ImageIcon(imgUrl2);
    }

    public URL FindImgUrl(String address) {
        return getClass().getClassLoader().getResource(address);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (this.background != null) {
            g.drawImage(this.background.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
        }
        Map<GameElement, List<ElementObj>> all = EM.getGameElements();
//		GameElement.values();//隐藏方法  返回值是一个数组,数组的顺序就是定义枚举的顺序
        for (GameElement e : GameElement.values()) {
            List<ElementObj> list = all.get(e);
            for (int i = 0; i < list.size(); i++) {
                ElementObj obj = list.get(i);
                obj.showElement(g); //调用每个类自己的show进行显示
            }
        }
    }

    /**
     * 多线程
     */
    @Override
    public void run() {
        while (true) {
            this.repaint();
            try {
                Thread.sleep(50); // 20fps
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
