package gui.component;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;

public class IPanel extends JPanel {
    //标签列表
    private HashSet<String> tags;

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

    public HashSet<String> getTags() {
        return tags;
    }

    public void setTags(HashSet<String> tags) {
        this.tags = tags;
    }
}
