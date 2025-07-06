package com.fisher.show;

import com.fisher.element.ElementObj;
import com.fisher.element.FishMap;
import com.fisher.element.Play;
import com.fisher.manager.ElementManager;
import com.fisher.manager.GameElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
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
    private Image background;
    // 大炮
    private Play player;


    public GameMainPanel() {
        init();
        addMouseListener();
    }

    public void init() {
        EM = ElementManager.getManager();
        loadPlay();  // 加载大炮
        listen();  // 注册监听器
    }

    // 注册监听器
    public void listen() {
        // 添加窗口监听器
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                player.setWinSize(new Dimension(getWidth(), getHeight()));
            }
        });
    }

    // 加载大炮
    public void loadPlay() {
        // 提前加载大炮（替代 GameThread.load）
        URL cannonUrl = getClass().getClassLoader().getResource("image/cannon/00.png");
        ImageIcon icon = new ImageIcon(cannonUrl != null ? cannonUrl.getFile() : null);
        Play player = new Play(icon);
        player.setWinSize(this.getSize());  // 提前设置窗口大小
        EM.addElement(player, GameElement.PLAYER);

        this.player = player;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Map<GameElement, List<ElementObj>> all = EM.getGameElements();
//		GameElement.values();  // 隐藏方法  返回值是一个数组,数组的顺序就是定义枚举的顺序

        // 背景图片
        if (this.background == null) {
            URL bgUrl = getClass().getClassLoader().getResource("image/background/fishlightbg_0.jpg");
            background = new ImageIcon(bgUrl != null ? bgUrl.getFile() : null).getImage();
        }
        g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), null);

        for (GameElement e : GameElement.values()) {
            List<ElementObj> list = all.get(e);
            for (ElementObj obj : list) {
//                System.out.println("show img");
                obj.showElement(g);  // 调用每个类自己的show进行显示
            }
        }
    }

    // 鼠标监听
    private void addMouseListener() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 当鼠标点击时，让大炮指向点击位置
                player.pointTo(e.getX(), e.getY());
            }
        });
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
