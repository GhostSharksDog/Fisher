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
    private static final Random random = new Random();

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
        int schoolSize = 3 + random.nextInt(3); // 3-5条

        // 基础位置和方向
        int baseX = baseFish.getX();
        int baseY = baseFish.getY();
        double baseDirection = baseFish.getDirection();

        // 添加基础鱼
        school.add(baseFish);

        // 生成额外的鱼
        for (int i = 1; i < schoolSize; i++) {
            Fish newFish = createSimilarFish(baseFish);
            if (newFish == null) continue;

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
            Fish newFish = createSimilarFish(baseFish);
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

    /**
     * 创建相似的鱼（相同类型）
     */
    private static Fish createSimilarFish(Fish original) {
        // 使用原始鱼的属性创建新鱼
        Fish newFish = new Fish();

        // 手动设置属性（避免使用 GameLoad）
        newFish.setFishType(original.getFishType());
        newFish.setWidth(original.getWidth());
        newFish.setHeight(original.getHeight());
        newFish.setSpeed(original.getSpeed());
        newFish.setScore(original.getScore());

        // 复制动画帧
        newFish.animationFrames = new ArrayList<>(original.animationFrames);
        newFish.catchFrames = new ArrayList<>(original.catchFrames);

        // 设置存活状态
        newFish.setAlive(true);

        // 设置边界（非常重要）
        Dimension size = ElementManager.getManager().getMainPanelSize();
        newFish.boundaryWidth = size.getWidth();
        newFish.boundaryHeight = size.getHeight();

        // 设置 key
        newFish.key = original.getKey();

        return newFish;
    }
}