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

    public GameMainPanel() {
        init();
    }

    public void init() {
        EM = ElementManager.getManager();

    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Map<GameElement, List<ElementObj>> all = EM.getGameElements();
//		GameElement.values();//隐藏方法  返回值是一个数组,数组的顺序就是定义枚举的顺序
        for (GameElement e : GameElement.values()) {
            List<ElementObj> list = all.get(e);
            for (int i = 0; i < list.size(); i++) {
                System.out.println("show img");
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
                Thread.sleep(10); // 100fps
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
