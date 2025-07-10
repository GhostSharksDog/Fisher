package com.fisher.show;

import com.fisher.manager.GameLoad;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class StartPanel extends JPanel {
    //    StartPanel的尺寸
    private final int width = 1000;
    private final int height = 618;
    private JButton startBtn;
    private JButton exitBtn;
    //    背景图片
    private ImageIcon bgImage;

    public StartPanel(ActionListener startAction, ActionListener exitAction) {
        init(startAction, exitAction);
    }

    public void init(ActionListener startAction, ActionListener exitAction) {
        //        设置背景图片
        bgImage = GameLoad.findResourceIcon("/image/background/start.jpg");
//   设置垂直布局
        this.setLayout(new GridBagLayout());
        //        设置尺寸
        this.setPreferredSize(new Dimension(width, height));
//        创建开始和退出按钮
        createButtons(startAction, exitAction);
    }

    private void createButtons(ActionListener startAction, ActionListener exitAction) {
//        按钮样式配置
        Font buttonFont = new Font("微软雅黑", Font.BOLD, 24);
        Dimension buttonDim = new Dimension(200, 60);
//        创建开始按钮
        startBtn = new JButton("开始游戏");
        startBtn.setFont(buttonFont);
        startBtn.setPreferredSize(buttonDim);
        startBtn.addActionListener(startAction);
//        创建退出按钮
        exitBtn = new JButton("退出游戏");
        exitBtn.setFont(buttonFont);
        exitBtn.setPreferredSize(buttonDim);
        exitBtn.addActionListener(exitAction);
//       添加按钮到面板
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 0, 10, 0);
        add(startBtn, gbc);
        add(exitBtn, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//         绘制背景图片
        if (bgImage != null) {
            g.drawImage(bgImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(19, 90, 149));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
