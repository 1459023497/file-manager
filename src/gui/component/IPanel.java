package gui.component;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.HashSet;

import javax.swing.JPanel;

import entity.ITag;

public class IPanel extends JPanel {
    //标签列表
    private HashSet<ITag> tags;

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

    public HashSet<ITag> getTags() {
        return tags;
    }

    public void setTags(HashSet<ITag> tags) {
        this.tags = tags;
    }
}
