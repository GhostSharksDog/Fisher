package com.fisher.controller;

import com.fisher.element.ElementObj;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ColliderManager {
    private static ColliderManager instance = new ColliderManager();

    public static ColliderManager getInstance() {
        return instance;
    }

    //    管理的所有刚体对象
    private final List<Collider> colliders = new ArrayList<>();

    /**
     * 向管理器中添加刚体对象
     * @param collider 刚体对象
     * @return 添加成功返回true，否则返回false
     */
    public boolean addCollider(Collider collider) {
//        查重
        for (Collider value : colliders) {
            if (value.equals(collider)) {
                return false;
            }
        }
        colliders.add(collider); // 向colliders的末尾添加刚体对象
        return true;
    }

    /**
     * 移除指定刚体对象
     * @param collider 指定刚体对象
     * @return 移除成功返回true，否则返回false
     */
    public boolean removeCollider(Collider collider) {
        return colliders.remove(collider);
    }

    /**
     * 获取与指定刚体碰撞的所有刚体对象
     * @param collider 指定刚体对象
     * @return 与指定刚体碰撞的所有刚体对象的List
     */
    public List<ElementObj> getIntersectColliders(Collider collider) {
        List<ElementObj> result = new ArrayList<>();
        int i = 0;
        while (i < colliders.size()) {
            Collider otherCollider = colliders.get(i);
            if(otherCollider == collider){
                i++;
                continue;
            }
            if(collider.getBounds().intersects(otherCollider.getBounds())){
                result.add(otherCollider.getThis());
            }
            i++;
        }
        return result;
    }
}
