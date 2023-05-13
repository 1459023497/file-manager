package gui.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.HashSet;

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

    /**
     * 文件夹
     * @param dir 文件夹
     * @param center 父面板
     */
    public void addFileBox(String dir, IPanel center) {
        FileBox dirRow = new FileBox(new IFile(dir), center);
        this.add(dirRow);
        this.add(Box.createVerticalStrut(3));
    }

    /**
     * 文件
     * @param file 文件
     * @param center 父面板
     */
    public void addFileBox(IFile file, IPanel center) {
        FileBox dirRow = new FileBox(file, center);
        this.add(dirRow);
        this.add(Box.createVerticalStrut(3));//垂直間距
    }

    public void addFileBox(IFile file, IPanel center, String highlightText) {
        FileBox dirRow = new FileBox(file, center);
        dirRow.setHighlight(highlightText);
        this.add(dirRow);
        this.add(Box.createVerticalStrut(3));
    }

    public void addTagBox(ITag tag, IPanel center) {
        TagBox dirRow = new TagBox(tag);
        this.add(dirRow);
        this.add(Box.createVerticalStrut(3));
    }

}
