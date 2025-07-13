//package com.fisher.manager;
//
//import com.fisher.element.ElementObj;
//import com.fisher.element.Fish;
//
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Random;
//
//public class FishGenerator {
//    private static final Random random = new Random();
//
//    /**
//     * 根据鱼的类型生成鱼群
//     */
//    public static List<Fish> generateFishGroup(Fish baseFish) {
//        Fish.Type type = baseFish.getFishType();
//
//        switch (type) {
//            case SMALL:
//                return generateSmallFishSchool(baseFish);
//            case MEDIUM:
//                return Collections.singletonList(baseFish);
//            case LARGE:
//                return generateLargeFish(baseFish);
//            default:
//                return Collections.singletonList(baseFish);
//        }
//    }
//
//    private static List<Fish> generateSmallFishSchool(Fish baseFish) {
//        List<Fish> school = new ArrayList<>();
//        int schoolSize = 2 + random.nextInt(4); // 2-5条
//
//        // 基础位置和方向
//        int baseX = baseFish.getX();
//        int baseY = baseFish.getY();
//        double baseDirection = baseFish.getDirection();
//
//        // 添加基础鱼
//        school.add(baseFish);
//
//        // 生成额外的鱼
//        for (int i = 1; i < schoolSize; i++) {
//            Fish newFish = createSimilarFish(baseFish);
//            if (newFish == null) continue;
//
//            boolean positionValid = false;
//            int attempts = 0;
//            int maxOffset = Math.max(newFish.getWidth(), newFish.getHeight()) * 2;
//
//            // 尝试找到不重叠的位置
//            while (!positionValid && attempts < 10) {
//                // 设置位置（在基础位置附近随机偏移）
//                int offsetX = random.nextInt(maxOffset * 2) - maxOffset;
//                int offsetY = random.nextInt(maxOffset * 2) - maxOffset;
//                newFish.setPosition(baseX + offsetX, baseY + offsetY);
//
//                // 检查是否与已有鱼重叠
//                positionValid = true;
//                for (Fish existingFish : school) {
//                    if (newFish.getBounds().intersects(existingFish.getBounds())) {
//                        positionValid = false;
//                        break;
//                    }
//                }
//                attempts++;
//            }
//
//            // 设置方向（与基础方向略有偏差）
//            double directionOffset = (random.nextDouble() - 0.5) * Math.PI / 10; // ±18度
//            newFish.setDirection(baseDirection + directionOffset);
//
//            school.add(newFish);
//        }
//
//        return school;
//    }
//
//    private static List<Fish> generateLargeFish(Fish baseFish) {
//        Dimension size = ElementManager.getManager().getMainPanelSize();
//        int boundaryWidth = (int) size.getWidth();
//        int boundaryHeight = (int) size.getHeight();
//
//        // 随机选择从左到右或从右到左
//        boolean leftToRight = random.nextBoolean();
//
//        // 确保鱼完全在屏幕外
//        int buffer = 50; // 屏幕外缓冲区
//
//        if (leftToRight) {
//            // 从左侧生成，向右移动
//            baseFish.setPosition(-baseFish.getWidth() - buffer - random.nextInt(50),
//                    random.nextInt(boundaryHeight - baseFish.getHeight()));
//            baseFish.setDirection(0); // 0弧度 = 向右
//        } else {
//            // 从右侧生成，向左移动
//            baseFish.setPosition(boundaryWidth + buffer + random.nextInt(50),
//                    random.nextInt(boundaryHeight - baseFish.getHeight()));
//            baseFish.setDirection(Math.PI); // π弧度 = 向左
//        }
//
//        return Collections.singletonList(baseFish);
//    }
//
//    /**
//     * 创建相似的鱼（相同类型）
//     */
//    private static Fish createSimilarFish(Fish original) {
//        // 创建新鱼对象
//        ElementObj newFishObj = GameLoad.getInstance().getElement(original.getKey());
//        if (!(newFishObj instanceof Fish)) return null;
//
//        Fish newFish = (Fish) newFishObj;
//
//        // 复制关键属性
//        newFish.setFishType(original.getFishType());
//        newFish.setWidth(original.getWidth());
//        newFish.setHeight(original.getHeight());
//        newFish.setSpeed(original.getSpeed());
//        newFish.setScore(original.getScore());
//
//        return newFish;
//    }
//}
