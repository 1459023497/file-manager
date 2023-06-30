package gui.component;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import entity.IFile;
import entity.ITag;
import gui.base.IPanel;
import gui.base.RoundedBorder;
import gui.window.Tag;
import service.TagService;

public class TagLabel extends JLabel implements MouseListener {
    private Tag tagWindow;
    private Color color;
    private IPanel panel;
    private int event;// Click the event value: 1 to show the file with the label, 2 to select the file to be tagged, and 3 to remove the label to be added
    private ITag tag;
    private IFile file;
    private int type;// Label type: 1: label in the label menu; 2: file label
    private FileBox fileBox;

    /**
     * A colored event label
     * 
     * @param tag
     * @param color
     * @param panel result panel
     * @param event Click the event value: 1 to show the file with the label, 2 to select the file to be tagged, and 3 to remove the label to be added
     */
    public TagLabel(ITag tag, Color color, IPanel panel, int event) {
        super(tag.getName());
        this.tag = tag;
        this.panel = panel;
        this.event = event;
        this.color = color;
        this.setOpaque(true);
        this.setBackground(color);
        this.setBorder(new RoundedBorder(Color.BLACK, 5));
        this.addMouseListener(this);
    }

    /**
     * A colored event label associated with a file
     * 
     * @param tag
     * @param color
     * @param panel result panel
     * @param event Click the event value: 1 to show the file with the label, 2 to select the file to be tagged, and 3 to remove the label to be added
     * @param file
     */
    public TagLabel(ITag tag, Color color, IPanel panel, int event, IFile file, FileBox fileBox) {
        super(tag.getName());
        this.tag = tag;
        this.panel = panel;
        this.event = event;
        this.file = file;
        this.color = color;
        this.type = 2;
        this.fileBox = fileBox;
        this.setOpaque(true);
        this.setBackground(color);
        this.setBorder(new RoundedBorder(Color.BLACK, 5));
        if (tag.getIsMain()){
            this.setForeground(Color.RED);
        }
        this.addMouseListener(this);
    }

    /**
     * The colored event label is associated with the panel that displays the label, because removing the label involves a refresh of the panel
     *
     * @param tag       tag
     * @param color     background color
     * @param panel     result panel
     * @param event     Click the event value: 1 to show the file with the label, 2 to select the file to be tagged, and 3 to remove the label to be added
     * @param tagWindow show window for this tag
     */
    public TagLabel(ITag tag, Color color, IPanel panel, int event, Tag tagWindow) {
        super(tag.getName());
        this.tag = tag;
        this.panel = panel;
        this.event = event;
        this.tagWindow = tagWindow;
        this.type = 1;
        this.setOpaque(true);
        this.setBackground(color);
        this.color = color;
        this.setBorder(new RoundedBorder(Color.BLACK, 5));
        this.addMouseListener(this);
    }

    /**
     * no event tag
     * 
     * @param text
     * @param color
     */
    public TagLabel(String text, Color color) {
        super(text);
        this.setOpaque(true);
        this.setBorder(new RoundedBorder(Color.BLACK, 5));
        this.setBackground(color);
    }
    
    public TagLabel(ITag tag, Color color) {
        super(tag.getName());
        this.tag = tag;
        this.setOpaque(true);
        this.setBorder(new RoundedBorder(Color.BLACK, 5));
        this.setBackground(color);
    }

    //set event type
    public void setEvent(int event) {
        this.event = event;
    }

    public TagLabel clone(){
        return new TagLabel(tag, color, panel, event);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        TagService tagService = new TagService();
        if (e.getButton() == MouseEvent.BUTTON1) {
            // clicked left button to find files that belong to this tag
            if (event == 1) {
                Map<String, Set<ITag>> fileMap = tagService.getFileMapByTag(tag);
                Map<String, Set<IFile>> files = tagService.getFilesMapByTag(tag);
                // show all
                panel.removeAll();
                files.forEach((dir, fileSet) -> {
                    // folder
                    IFile dirFile = new IFile();
                    dirFile.setDirectory(true);
                    dirFile.setPath(dir);
                    panel.add(new FileBox(dirFile, panel, fileMap));
                    panel.add(Box.createVerticalStrut(3));
                    fileSet.forEach(file -> {
                        FileBox row = new FileBox(file, panel, fileMap);
                        panel.add(row);
                        panel.add(Box.createVerticalStrut(3));
                    });
                });
                panel.reload();
            } else if (event == 2) {
                // add to candidate list
                if(panel.getTags().contains(tag)) return;
                panel.getTags().add(tag);
                // clone the tag you want to add, change the event type to remove, add to below, refresh
                TagLabel copy = clone();
                copy.setEvent(3);
                panel.add(copy);
                panel.reload();
            } else if (event == 3) {
                // remove it from candidate list
                panel.getTags().remove(tag);
                panel.remove(this);
                panel.reload();
            }
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            // clicked right button to delete
            if (type == 1) {
                String tap = "你真的要删除[" + tag.getName() + "]标签吗?";
                int confirmDel = JOptionPane.showConfirmDialog(this, tap, "确认信息", JOptionPane.YES_NO_OPTION);
                if (confirmDel == JOptionPane.YES_OPTION) {
                    tagService.deleteTag(tag);
                    // frame information reload
                    tagWindow.reloadGroups();
                    tagWindow.reloadTags();
                }
            } else if (type == 2) {
                new TagMenu(tag, this, file, fileBox).show(this, e.getX(), e.getY());
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        this.setBorder(new RoundedBorder(Color.red, 5));

    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.setBorder(new RoundedBorder(Color.BLACK, 5));
    }

    public void setMainTag(boolean isMain) {
        if(isMain){
            this.setForeground(Color.RED);
        }else{
            this.setForeground(Color.BLACK);
        }
    }
}
