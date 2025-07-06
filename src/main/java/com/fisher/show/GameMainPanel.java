package com.fisher.show;

import com.fisher.element.ElementObj;
import com.fisher.element.Play;
import com.fisher.manager.GameLoad;
import com.fisher.manager.ElementManager;
import com.fisher.manager.GameElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    private final int width = 1000;
    private final int height = 618;

    public GameMainPanel() {
        init();
    }

    public void init() {
        EM = ElementManager.getManager();
//        设置面板大小
        setPreferredSize(new Dimension(width,height));
        load();
        if (EM != null) {
            EM.setMainPanelSize(new Dimension(width, height));
        }
    }

    public void load() {
        this.background = GameLoad.getInstance().getPanelBackground();  // 加载背景图片
        listen();  // 注册监听器
    }

    // 注册监听器
    public void listen() {
        // 添加窗口监听器
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = getSize();
                EM.setMainPanelSize(size);
            }
        });
    }

    // 加载大炮
//    public void loadPlay() {
//        // 提前加载大炮（替代 GameThread.load）
//        URL cannonUrl = getClass().getClassLoader().getResource("image/cannon/00.png");
//        ImageIcon icon = new ImageIcon(cannonUrl != null ? cannonUrl.getFile() : null);
//        Play player = new Play(icon);
//        player.setWinSize(this.getSize());  // 提前设置窗口大小
//        EM.addElement(player, GameElement.PLAYER);
//        this.player = player;
//    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Map<GameElement, List<ElementObj>> all = EM.getGameElements();
//		GameElement.values();  // 隐藏方法  返回值是一个数组,数组的顺序就是定义枚举的顺序
        g.drawImage(background.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
        for (GameElement e : GameElement.values()) {
            List<ElementObj> list = all.get(e);
            for (int i = 0; i < list.size(); i++) {
                ElementObj obj = list.get(i);
                System.out.println(obj);
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
//            List<ElementObj> bullets = EM.getElementByKey(GameElement.BULLET);
//
//            Iterator<ElementObj> it = bullets.iterator();
//            while (it.hasNext()) {
//                ElementObj bullet = it.next();
//                if (!bullet.isAlive()) {
//                    it.remove();  // 子弹死亡，从集合中移除
//                } else {
//                    bullet.update();  // 子弹活着，更新位置
//                }
//            }

            this.repaint();
            try {
                Thread.sleep(10); // 100fps
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
