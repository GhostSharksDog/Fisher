package com.fisher.show;
import javax.swing.*;

import com.fisher.manager.GameLoad;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class StartPanel extends JPanel {
    private final ActionListener onStart;
    private final ActionListener onExit;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel previewPanel = new JPanel(cardLayout);
    private int selectedMapIndex = 0;
    
    // 地图配置数据
    private final Map<Integer, MapConfig> mapConfigs = new HashMap<>();
    private final JLabel descriptionLabel = new JLabel("", SwingConstants.CENTER);
    
    // 地图配置内部类
    private static class MapConfig {
        String name;
        String previewPath;
        String plistPath;
        String spriteName;
        
        public MapConfig(String name, String previewPath, String plistPath, String spriteName) {
            this.name = name;
            this.previewPath = previewPath;
            this.plistPath = plistPath;
            this.spriteName = spriteName;
        }
    }

    public StartPanel(ActionListener onStart, ActionListener onExit) {
        this.onStart = onStart;
        this.onExit = onExit;
        initMapConfigs(); // 初始化地图配置
        initUI();         // 初始化界面
    }

    private void initMapConfigs() {
        // 使用资源目录中实际的背景图片配置三个地图
        mapConfigs.put(0, new MapConfig(
            "浅海区", 
            "/image/background/fishlightbg_0.jpg", 
            null, 
            null
        ));
        
        mapConfigs.put(1, new MapConfig(
            "深海区", 
            "/image/background/fishlightbg_3.jpg", 
            null, 
            null
        ));
        
        mapConfigs.put(2, new MapConfig(
            "珊瑚礁", 
            "/image/background/fishlightbg_6.jpg", 
            null, 
            null
        ));
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 1. 顶部标题
        JLabel titleLabel = new JLabel("选择游戏地图", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);
        
        // 2. 中部预览区
        previewPanel.setPreferredSize(new Dimension(600, 400));
        previewPanel.setOpaque(false);
        loadMapPreviews(); // 加载地图预览图
        add(previewPanel, BorderLayout.CENTER);
        
        // 3. 地图描述
        descriptionLabel.setFont(new Font("宋体", Font.PLAIN, 18));
        descriptionLabel.setForeground(Color.WHITE);
        updateDescription();
        add(descriptionLabel, BorderLayout.SOUTH);
        
        // 4. 底部按钮区
        add(createButtonPanel(), BorderLayout.SOUTH);
        
        // 5. 设置背景
        setBackgroundWithImage();
    }

    // 使用GameLoad设置背景图片
    private void setBackgroundWithImage() {
        // 使用资源目录中的 start.jpg 作为开始界面背景
        ImageIcon bgIcon = GameLoad.findResourceIcon("/image/background/start.jpg");
        if (bgIcon != null) {
            setLayout(new BorderLayout());
            JLabel bgLabel = new JLabel(bgIcon);
            bgLabel.setLayout(new BorderLayout());
            add(bgLabel, BorderLayout.CENTER);
            
            Component[] components = getComponents();
            for (Component comp : components) {
                bgLabel.add(comp);
            }
        }
    }

    // 创建操作按钮面板
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        buttonPanel.setOpaque(false);
        
        JButton prevBtn = createStyledButton("上一张");
        prevBtn.addActionListener(e -> switchMap(-1));
        
        JButton nextBtn = createStyledButton("下一张");
        nextBtn.addActionListener(e -> switchMap(1));
        
        JButton startBtn = createStyledButton("开始游戏");
        startBtn.setBackground(new Color(46, 204, 113));
        startBtn.addActionListener(onStart);
        
        JButton exitBtn = createStyledButton("退出游戏");
        exitBtn.setBackground(new Color(231, 76, 60));
        exitBtn.addActionListener(onExit);
        
        buttonPanel.add(prevBtn);
        buttonPanel.add(startBtn);
        buttonPanel.add(nextBtn);
        buttonPanel.add(exitBtn);
        return buttonPanel;
    }

    // 创建带样式的按钮
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("微软雅黑", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setForeground(Color.BLACK);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createRaisedBevelBorder());
        
        // 悬停效果
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(btn.getBackground().brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(UIManager.getColor("Button.background"));
            }
        });
        return btn;
    }

    // 加载地图预览图
    private void loadMapPreviews() {
        for (int i = 0; i < mapConfigs.size(); i++) {
            MapConfig config = mapConfigs.get(i);
            
            // 使用GameLoad加载预览图
            ImageIcon previewIcon = GameLoad.findResourceIcon(config.previewPath);
            JLabel mapLabel;
            
            if (previewIcon != null) {
                // 调整预览图尺寸
                Image scaledImg = previewIcon.getImage().getScaledInstance(600, 400, Image.SCALE_SMOOTH);
                mapLabel = new JLabel(new ImageIcon(scaledImg));
            } else {
                // 加载失败时使用默认背景
                mapLabel = new JLabel("加载失败: " + config.name);
                mapLabel.setBackground(i == 0 ? Color.BLUE : i == 1 ? Color.GREEN : Color.ORANGE);
                mapLabel.setOpaque(true);
            }
            
            // 设置边框和标签
            mapLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(i == selectedMapIndex ? Color.RED : Color.GRAY, 3),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            mapLabel.setHorizontalTextPosition(JLabel.CENTER);
            mapLabel.setVerticalTextPosition(JLabel.BOTTOM);
            mapLabel.setText(config.name);
            mapLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
            mapLabel.setForeground(Color.WHITE);
            
            previewPanel.add(mapLabel, "map" + i);
        }
    }

    // 切换地图
    private void switchMap(int direction) {
        selectedMapIndex = (selectedMapIndex + direction + mapConfigs.size()) % mapConfigs.size();
        cardLayout.show(previewPanel, "map" + selectedMapIndex);
        updateDescription();
        refreshMapBorders();
    }

    // 更新地图描述
    private void updateDescription() {
        MapConfig config = mapConfigs.get(selectedMapIndex);
        descriptionLabel.setText(
            String.format("地图 %d/%d: %s", 
                selectedMapIndex + 1, 
                mapConfigs.size(), 
                config.name
            )
        );
    }

    // 刷新所有地图边框状态
    private void refreshMapBorders() {
        for (int i = 0; i < previewPanel.getComponentCount(); i++) {
            Component comp = previewPanel.getComponent(i);
            if (comp instanceof JLabel) {
                ((JLabel)comp).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(i == selectedMapIndex ? Color.RED : Color.GRAY, 3),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
        }
    }

    // 获取当前选中地图ID（供主游戏逻辑调用）
    public MapConfig getSelectedMapConfig() {
        return mapConfigs.get(selectedMapIndex);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 添加半透明背景增强文字可读性
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}