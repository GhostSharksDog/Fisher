package com.fisher.element;

import javax.swing.*;
import java.awt.*;

public class Play extends ElementObj {
    /**
     * Play玩家属性
     * 效果： 玩家控制鼠标，大炮跟随鼠标转向，定时发射炮弹，捕鱼积分阶段性增长，大炮和炮弹种类会随等级变化
     * 玩家积分
     * 大炮等级
     * 炮弹类型
     * 发射间隔
     * 旋转角度
     * 大炮位置 x，y
     * 大炮图片宽高 width，height
     * 大炮图片素材 icon
     */


    public Play(int x, int y, int width, int height, ImageIcon icon) {
        super(x, y, width, height, icon);
    }

    @Override
    public void showElement(Graphics g) {
        g.drawImage(this.getIcon().getImage(),
                this.getX(),this.getY(),
                this.getWidth(),this.getHeight(),null);
    }


    public void keyClick(boolean b, int keycode) {
        if(b){
            switch(keycode){

            }
        }
    }
}
