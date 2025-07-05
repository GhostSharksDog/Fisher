package com.fisher.element;

import javax.swing.*;
import java.awt.*;

public class Play extends ElementObj {

    public Play(int x, int y, int width, int height, ImageIcon icon) {
        super(x, y, width, height, icon);
    }

    @Override
    public void showElement(Graphics g) {
        g.drawImage(this.getIcon().getImage(),
                this.getX(),this.getY(),
                this.getWidth(),this.getHeight(),null);
    }
}
