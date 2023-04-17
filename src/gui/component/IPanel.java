package gui.component;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JPanel;

import entity.IFile;
import entity.ITag;

public class IPanel extends JPanel {
    //标签列表
    private HashSet<ITag> tags;

    /**
     * 透明面板
     */
    public IPanel(){
        super();
        init();
    }

    public IPanel(Dimension d){
        super();
        this.setPreferredSize(d);
        init();
    }

    public IPanel(LayoutManager layoutManager) {
        super(layoutManager);
        init();
    }

    /*
     * 面板初始化
     */
    private void init(){
        this.setBackground(new Color(0,0,0,0));
        this.setOpaque(false);//背景透明
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

    public void addFileBox(String dir, IPanel center, HashMap<String, Set<ITag>> fileMap) {
        FileBox dirRow = new FileBox(new IFile(dir), center, fileMap);
        this.add(dirRow);
        this.add(Box.createVerticalStrut(3));//垂直間距
    }

    public void addFileBox(IFile file, IPanel center, HashMap<String, Set<ITag>> fileMap) {
        FileBox dirRow = new FileBox(file, center, fileMap);
        this.add(dirRow);
        this.add(Box.createVerticalStrut(3));
    }

    public void addFileBox(IFile file, IPanel center) {
        FileBox dirRow = new FileBox(file, center);
        this.add(dirRow);
        this.add(Box.createVerticalStrut(3));
    }

}
