package com.fisher.show;

import com.fisher.manager.ElementManager;

import javax.swing.*;
import java.awt.*;

/**
 * 游戏主面板
 * 元素的显示
 * 界面的刷新
 */
public class GameMainPanel extends JPanel {
    //联动管理器，调用元素
    private ElementManager EM;

    public GameMainPanel(){
        init();
    }

    public void init(){
        EM = ElementManager.getManager();

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);


    }
}
