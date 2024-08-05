package gui.base;

import common.AppContext;
import entity.IFile;
import entity.ITag;
import gui.component.FileBox;
import gui.component.TagBox;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class IPanel extends JPanel {
    private Set<ITag> tags;

    /**
     * transparent panel
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


    private void init(){
        if(AppContext.UI_TRANSPARENT){
            this.setBackground(new Color(0,0,0,0));
            this.setOpaque(false);
        }
    }

    public void reload(){
        this.revalidate();
        this.repaint();
    }

    public Set<ITag> getTags() {
        return tags;
    }

    public void setTags(Set<ITag> tags) {
        this.tags = tags;
    }

    /**
     * folder
     * @param dir folder path
     * @param center father panel
     */
    public void addFileBox(String dir, IPanel center) {
        FileBox dirRow = new FileBox(new IFile(dir), center);
        this.add(dirRow);
        this.add(Box.createVerticalStrut(3));//vertical gap
    }

    /**
     * file
     * @param file 
     * @param center father panel
     */
    public void addFileBox(IFile file, IPanel center) {
        FileBox dirRow = new FileBox(file, center);
        this.add(dirRow);
        this.add(Box.createVerticalStrut(3));
    }

    public void addFileBox(IFile file, IPanel center, String highlightText) {
        FileBox dirRow = new FileBox(file, center);
        dirRow.setHighlight(highlightText);
        this.add(dirRow);
        this.add(Box.createVerticalStrut(3));
    }

    public void addTagBox(ITag tag) {
        TagBox dirRow = new TagBox(tag);
        this.add(dirRow);
        this.add(Box.createVerticalStrut(3));
    }

}
