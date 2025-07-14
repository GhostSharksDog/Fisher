package com.fisher.show;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import com.fisher.manager.ElementManager;
import com.fisher.manager.GameLoad;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class StartPanel extends JPanel {
    private final JPanel mapsPanel;
    private final ActionListener startAction;
    private final ActionListener exitAction;
    private final List<String> allMaps = new ArrayList<>();
    private int startIndex = 0; // 起始索引
    private int endIndex = 2;   // 结束索引
    private static final int VISIBLE_MAPS = 3; // 固定显示3个地图

    public StartPanel(ActionListener startAction, ActionListener exitAction) {
        this.startAction = startAction;
        this.exitAction = exitAction;
        setLayout(new BorderLayout(10, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setOpaque(false);
        setPreferredSize(new Dimension(1000, 618));

        // 初始化所有地图路径
        initializeMaps();

        // 1. 标题区域
        JLabel titleLabel = new JLabel("捕鱼达人", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 48));
        titleLabel.setForeground(new Color(255, 215, 0)); // 金色标题
        titleLabel.setOpaque(false);
        titleLabel.setBorder(new EmptyBorder(10, 0, 30, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 2. 地图选择区域
        mapsPanel = new JPanel();
        mapsPanel.setLayout(new BoxLayout(mapsPanel, BoxLayout.X_AXIS)); // 改为水平布局
        mapsPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // 水平居中
        mapsPanel.setOpaque(false);
        add(createMapSelector(), BorderLayout.CENTER);

        // 3. 按钮区域
        add(createButtonPanel(), BorderLayout.SOUTH);

        // 初始显示前三张地图（索引0、1、2）
        showCurrentMaps();
    }

    private void initializeMaps() {
        // 添加所有地图资源路径
        allMaps.add("/before/bg/fishlightbg_0.jpg");
        allMaps.add("/before/bg/fishlightbg_1.jpg");
        allMaps.add("/before/bg/fishlightbg_2.jpg");
        allMaps.add("/before/bg/fishlightbg_3.jpg");
        allMaps.add("/before/bg/fishlightbg_4.jpg");
        allMaps.add("/before/bg/fishlightbg_5.jpg");
        allMaps.add("/before/bg/fishlightbg_6.jpg");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ImageIcon bgIcon = GameLoad.findResourceIcon("/before/bg/start.jpg");
        if (bgIcon != null) {
            g.drawImage(bgIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }

    // 创建带箭头的地图选择器
    private JPanel createMapSelector() {
        JPanel container = new JPanel(new GridBagLayout()); // 改用GridBagLayout
        container.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weighty = 1;

        // 左侧箭头
        gbc.gridx = 0;
        gbc.gridy = 0;
        container.add(createImageArrowButton("/before/icon/left_arrow.png", -1), gbc);

        // 地图区域
        gbc.gridx = 1;
        gbc.weightx = 1; // 允许水平扩展
        container.add(mapsPanel, gbc);

        // 右侧箭头
        gbc.gridx = 2;
        gbc.weightx = 0;
        container.add(createImageArrowButton("/before/icon/right_arrow.png", 1), gbc);

        return container;
    }

    // 地图按钮样式
    private JButton createMapButton(String mapName, int index) {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(240, 160)); // 增大尺寸
        ImageIcon icon = GameLoad.findResourceIcon(mapName);
        if (icon != null) {
            // 缩放图片以适应按钮
            Image img = icon.getImage().getScaledInstance(240, 160, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));
        }
        btn.setContentAreaFilled(false);

        // ========== 关键修改：统一所有地图的边框样式 ==========
        // 仅保留黄色边框，移除红色内边框
        btn.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 4)); // 黄色边框

        btn.addActionListener(e -> {
            System.out.println("(com.fisher.show.StartPanel.createMapButton.117)已选择地图: " + mapName);
//            设置gamePanel的背景图片
            ElementManager.getManager().GameMapBgPath = mapName;
        });
        return btn;
    }

    // 创建图片箭头按钮 - 使用PNG图片
    private JButton createImageArrowButton(String iconPath, final int direction) {
        JButton btn = new JButton();
        // 设置尺寸约束
        btn.setMinimumSize(new Dimension(70, 160));
        btn.setPreferredSize(new Dimension(80, 160));
        btn.setMaximumSize(new Dimension(90, 160));

        // === 关键修复：彻底禁用所有默认样式 ===
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);    // 新增：禁用边框绘制
        btn.setFocusPainted(false);      // 新增：禁用焦点框
        btn.setBorder(BorderFactory.createEmptyBorder()); // 确保无边框

        // 加载并缩放图片
        ImageIcon icon = GameLoad.findResourceIcon(iconPath);
        if (icon != null) {
            // 使用高质量缩放减少白边
            int width = 60;
            int height = 60;
            if (icon.getIconWidth() > icon.getIconHeight()) {
                height = (int) ((float) icon.getIconHeight() / icon.getIconWidth() * width);
            } else {
                width = (int) ((float) icon.getIconWidth() / icon.getIconHeight() * height);
            }

            // 使用SCALE_AREA_AVERAGING提高缩放质量
            Image scaledIcon = icon.getImage().getScaledInstance(
                    width, height, Image.SCALE_AREA_AVERAGING // 修改缩放算法
            );
            btn.setIcon(new ImageIcon(scaledIcon));
        } else {
            btn.setText(direction < 0 ? "<" : ">");
            btn.setFont(new Font("Arial", Font.BOLD, 36));
            btn.setForeground(Color.WHITE);
        }

        // 优化悬停效果：使用缓存避免重复缩放
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            private final ImageIcon originalIcon = btn.getIcon() != null ?
                    (ImageIcon) btn.getIcon() : null;

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (originalIcon != null) {
                    Image img = originalIcon.getImage();
                    int newWidth = (int)(img.getWidth(null) * 1.1);
                    int newHeight = (int)(img.getHeight(null) * 1.1);
                    Image scaled = img.getScaledInstance(
                            newWidth, newHeight, Image.SCALE_SMOOTH
                    );
                    btn.setIcon(new ImageIcon(scaled));
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (originalIcon != null) {
                    btn.setIcon(originalIcon); // 直接恢复缓存图标
                }
            }
        });

        // 点击事件处理（保持不变）
        btn.addActionListener(e -> {
            if (direction < 0 && startIndex > 0) {
                startIndex--;
                endIndex--;
            } else if (direction > 0 && endIndex < allMaps.size() - 1) {
                startIndex++;
                endIndex++;
            }
            showCurrentMaps();
        });

        return btn;
    }

    // 显示当前地图组
    private void showCurrentMaps() {
        mapsPanel.removeAll();

        // 确保索引有效
        startIndex = Math.max(0, startIndex);
        endIndex = Math.min(allMaps.size() - 1, endIndex);

        // 确保显示3个地图
        if (endIndex - startIndex + 1 < VISIBLE_MAPS) {
            if (startIndex == 0) {
                endIndex = VISIBLE_MAPS - 1;
            } else {
                startIndex = endIndex - VISIBLE_MAPS + 1;
            }
        }

        // 添加当前组的地图（水平排列）
        for (int i = startIndex; i <= endIndex; i++) {
            int position = i - startIndex;
            mapsPanel.add(createMapButton(allMaps.get(i), position));
            // 在按钮间添加间距（除最后一个外）
            if (i < endIndex) {
                mapsPanel.add(Box.createRigidArea(new Dimension(40, 0)));
            }
        }

        mapsPanel.revalidate();
        mapsPanel.repaint();
    }

    // 按钮区域优化
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(new EmptyBorder(30, 0, 30, 0));

        // 添加弹性空间使按钮居中
        panel.add(Box.createHorizontalGlue());

        JButton startBtn = createStyledButton("开始游戏", new Color(0, 180, 0)); // 鲜艳绿色
        startBtn.addActionListener(startAction);
        panel.add(startBtn);

        // 添加间距
        panel.add(Box.createRigidArea(new Dimension(60, 0)));

        JButton exitBtn = createStyledButton("退出游戏", new Color(220, 50, 50)); // 鲜艳红色
        exitBtn.addActionListener(exitAction);
        panel.add(exitBtn);

        // 添加弹性空间使按钮居中
        panel.add(Box.createHorizontalGlue());

        return panel;
    }

    // 按钮样式优化
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("微软雅黑", Font.BOLD, 30));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);

        // 设置按钮尺寸
        btn.setPreferredSize(new Dimension(260, 80));
        btn.setMaximumSize(new Dimension(260, 80));

        // 3D效果边框
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 4),
                BorderFactory.createEmptyBorder(5, 20, 5, 20)
        ));

        // 添加鼠标悬停效果
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor.brighter());
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(bgColor.darker().brighter(), 4),
                        BorderFactory.createEmptyBorder(5, 20, 5, 20)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(bgColor.darker(), 4),
                        BorderFactory.createEmptyBorder(5, 20, 5, 20)
                ));
            }
        });

        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}