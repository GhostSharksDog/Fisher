package com.fisher.manager;

import com.fisher.show.GameMainPanel;

// TODO: 🐟 在拼尽全力塔塔开后依旧无法战胜大炮，献出心脏后转生为存在金币于这个游戏中...
public class CoinManager {
    private static CoinManager instance = new CoinManager();
    private int coins = 200;  // 当前金币数量

    public static CoinManager getInstance() {
        return instance;
    }

    public int getCoins() {
        return this.coins;
    }

    // 增加金币
    public void addCoins(int mount) {
        this.coins += mount;
    }

    // 减少金币
    public boolean reduceCoins(int mount) {
        if (this.coins >= mount) {
            this.coins -= mount;
            return true;
        }
        return false;
    }
}
