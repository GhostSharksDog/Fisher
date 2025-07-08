package com.fisher.show;

import com.fisher.element.ElementObj;
import com.fisher.manager.GameLoad;
import com.fisher.manager.ElementManager;
import com.fisher.manager.GameElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
                EM.setMainPanelSize(getSize());
            }
        });

        // 添加鼠标监听器
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("mouseClicked: " + e.getX() + "," + e.getY());
                EM.setMousePoint(e.getX(), e.getY());
            }
        });
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Map<GameElement, List<ElementObj>> all = EM.getGameElements();
//        System.out.println(all.get(GameElement.EFFECT));
//		GameElement.values();  // 隐藏方法  返回值是一个数组,数组的顺序就是定义枚举的顺序
        g.drawImage(background.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
        for (GameElement e : GameElement.values()) {
            List<ElementObj> list = all.get(e);
//            if (e.equals(GameElement.EFFECT)) System.out.println("GameMainPanel.paint: EFFECT成功调用");
            for (int i = 0; i < list.size(); i++) {
                ElementObj obj = list.get(i);
                obj.setSize(ElementManager.getManager().getMainPanelSize());  // 设置元素大小，位置
                obj.update();  // 更新元素状态
                obj.showElement(g);  // 调用每个类自己的show进行显示

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
