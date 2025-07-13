package com.fisher.manager;

import com.fisher.element.ElementObj;
import com.fisher.element.Fish;
import com.fisher.element.Fish.Type;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FishGenerator {
    // 添加安全距离
    private static final int SAFE_DISTANCE = 300; // 安全距离300像素

    private static final Random random = new Random();
    // 添加静态方法创建新鱼
    private static Fish createNewFish(String key) {
        ElementObj obj = GameLoad.getInstance().getElement(key);
        return (obj instanceof Fish) ? (Fish) obj : null;
    }
    /**
     * 根据鱼的类型生成鱼群
     */
    public static List<Fish> generateFishGroup(Fish baseFish) {
        Type type = baseFish.getFishType();

        switch (type) {
            case SMALL:
                return generateSmallFishSchool(baseFish);
            case MEDIUM:
                return generateMediumFishGroup(baseFish);
            case LARGE:
                return generateLargeFish(baseFish);
            default:
                return Collections.singletonList(baseFish);
        }
    }

    private static List<Fish> generateSmallFishSchool(Fish baseFish) {
        List<Fish> school = new ArrayList<>();
        int schoolSize = 1 + random.nextInt(5); // 1-6条

        // 基础位置和方向
        int baseX = baseFish.getX();
        int baseY = baseFish.getY();
        double baseDirection = baseFish.getDirection();

        // 添加基础鱼
        school.add(baseFish);

        // 生成额外的鱼
        for (int i = 1; i < schoolSize; i++) {
            Fish newFish = createNewFish(baseFish.getKey());
            if (newFish == null) continue;

            copyFishState(baseFish, newFish);
            // 计算偏移角度 (0°, 90°, 180°, 270°)
            double angle = Math.PI * 2 * (i - 1) / (schoolSize - 1);

            // 设置位置（距离基础鱼100-200像素）
            int distance = 100 + random.nextInt(100);
            int offsetX = (int)(Math.cos(angle) * distance);
            int offsetY = (int)(Math.sin(angle) * distance);

            newFish.setPosition(baseX + offsetX, baseY + offsetY);

            // 设置相同的方向
            newFish.setDirection(baseDirection);

            // 标记为鱼群成员
            newFish.setGroupId(baseFish.hashCode());
            newFish.setGroupLeader(baseFish);

            school.add(newFish);
        }

        return school;
    }

    private static List<Fish> generateMediumFishGroup(Fish baseFish) {
        List<Fish> group = new ArrayList<>();
        int groupSize = 2 + random.nextInt(2); // 2-3条

        // 基础位置和方向
        int baseX = baseFish.getX();
        int baseY = baseFish.getY();
        double baseDirection = baseFish.getDirection();

        // 添加基础鱼
        group.add(baseFish);

        // 生成额外的鱼
        for (int i = 1; i < groupSize; i++) {
            Fish newFish = createNewFish(baseFish.getKey());
            if (newFish == null) continue;

            // 设置位置（在基础位置附近150-250像素）
            int offsetX = 150 + random.nextInt(100);
            int offsetY = 150 + random.nextInt(100);
            if (random.nextBoolean()) offsetX = -offsetX;
            if (random.nextBoolean()) offsetY = -offsetY;

            newFish.setPosition(baseX + offsetX, baseY + offsetY);

            // 设置方向（与基础方向相同）
            newFish.setDirection(baseDirection);

            // 标记为鱼群成员
            newFish.setInSchool(true);

            group.add(newFish);
        }

        return group;
    }

    private static List<Fish> generateLargeFish(Fish baseFish) {
        Dimension size = ElementManager.getManager().getMainPanelSize();
        int boundaryWidth = (int) size.getWidth();
        int boundaryHeight = (int) size.getHeight();

        Random random = new Random();
        boolean leftToRight = random.nextBoolean();
        int buffer = 100; // 增大缓冲区

        if (leftToRight) {
            // 从左侧生成，向右移动
            baseFish.setPosition(-baseFish.getWidth() - buffer - random.nextInt(100),
                    random.nextInt(boundaryHeight - baseFish.getHeight()));
            baseFish.setDirection(0); // 0弧度 = 向右
        } else {
            // 从右侧生成，向左移动
            baseFish.setPosition(boundaryWidth + buffer + random.nextInt(100),
                    random.nextInt(boundaryHeight - baseFish.getHeight()));
            baseFish.setDirection(Math.PI); // π弧度 = 向左
        }

        // 标记大鱼方向已固定
        baseFish.setDirectionFixed(true);
        baseFish.setInSchool(false); // 大鱼不是鱼群成员

        return Collections.singletonList(baseFish);
    }

    // 添加方法复制鱼的状态
    private static void copyFishState(Fish src, Fish dest) {
        dest.setFishType(src.getFishType());
        dest.setSpeed(src.getSpeed());
        dest.setDirection(src.getDirection());
        dest.setGroupId(src.hashCode());
        dest.setGroupLeader(src);
        dest.setBoundary(new Dimension(
                (int)src.boundaryWidth,
                (int)src.boundaryHeight
        ));
    }
}