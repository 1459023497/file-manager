package gui.component;

import javax.swing.*;
import java.awt.*;

public class IPanel extends JPanel {

    public IPanel(){
        super();
        //背景透明
        this.setOpaque(false);
    }

    public IPanel(Dimension d){
        super();
        this.setPreferredSize(d);
        this.setOpaque(false);
    }

    public IPanel(LayoutManager layoutManager) {
        super(layoutManager);
        this.setOpaque(false);
    }

    public void reload(){
        this.revalidate();//重布局
        this.repaint();//重绘
    }
}
