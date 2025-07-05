package com.fisher.show;

import com.fisher.element.ElementObj;
import com.fisher.element.FishMap;
import com.fisher.element.Play;
import com.fisher.manager.ElementManager;
import com.fisher.manager.GameElement;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 游戏主面板
 * 元素的显示
 * 界面的刷新
 */
public class GameMainPanel extends JPanel {
    //联动管理器，调用元素
    private ElementManager EM;

    public GameMainPanel(){
        init();

        //Test load
        load();
    }

    public void init(){
        EM = ElementManager.getManager();

    }

    public void load(){
        URL imgUrl1 = FindImgUrl("image/cannon/00.png");
        URL imgUrl2 = FindImgUrl("image/background/fishlightbg_0.jpg");

        ImageIcon icon = new ImageIcon(imgUrl1);
        ElementObj obj = new Play(100,100,66,77,icon);
        EM.addElement(obj, GameElement.PLAYER);

        ImageIcon icon2 = new ImageIcon(imgUrl2);
        ElementObj bg = new FishMap(0,0,800,480,icon2);
        EM.addElement(bg, GameElement.MAP);
    }

    public URL FindImgUrl(String address){
        URL imgUrl = getClass().getClassLoader().getResource(address);
        if (imgUrl == null) {
            return null;
        }
        return imgUrl;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Map<GameElement, List<ElementObj>> all = EM.getGameElements();
        Set<GameElement> set = all.keySet();
        for(GameElement e : set){
            List<ElementObj> list = all.get(e);
            for (ElementObj obj : list) {
                System.out.println("show img");
                obj.showElement(g); //调用每个类自己的show进行显示
            }
        }
    }


}
