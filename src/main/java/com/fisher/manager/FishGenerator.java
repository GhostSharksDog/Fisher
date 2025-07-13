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
                return Collections.singletonList(baseFish);
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

            // 设置位置（在基础位置附近随机偏移）
            int offsetX = 30 + random.nextInt(60);
            int offsetY = 30 + random.nextInt(60);
            if (random.nextBoolean()) offsetX = -offsetX;
            if (random.nextBoolean()) offsetY = -offsetY;

            newFish.setPosition(baseX + offsetX, baseY + offsetY);

            // 设置方向（与基础方向略有偏差）
            double directionOffset = (random.nextDouble() - 0.5) * Math.PI / 8; // ±22.5度
            newFish.setDirection(baseDirection + directionOffset);

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

            // 设置位置（在基础位置附近随机偏移）
            int offsetX = 50 + random.nextInt(100);
            int offsetY = 50 + random.nextInt(100);
            if (random.nextBoolean()) offsetX = -offsetX;
            if (random.nextBoolean()) offsetY = -offsetY;

            newFish.setPosition(baseX + offsetX, baseY + offsetY);

            // 设置方向（与基础方向一致）
            newFish.setDirection(baseDirection);

            group.add(newFish);
        }

        return group;
    }

    /**
     * 创建相似的鱼（相同类型）
     */
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