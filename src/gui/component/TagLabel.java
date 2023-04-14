package gui.component;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import entity.IFile;
import entity.ITag;
import gui.window.Tag;
import service.TagService;

public class TagLabel extends JLabel implements MouseListener {
    private Tag tagWindow;// 标签窗口
    private Color color;
    private IPanel panel;// 绘制点击结果的面板
    private int event;// 点击事件值: 1,展示该标签的文件, 2,选择要给文件添加标签, 3,移除要添加的标签
    private ITag tag;// 关联的标签
    private IFile file;// 关联的文件
    private int type;// 标签类型：1，在标签菜单的标签 2，文件的标签

    /**
     * 带颜色的标签事件
     * 
     * @param tag
     * @param color
     * @param panel 绘制点击结果的面板
     * @param event 点击事件值: 1,展示该标签的文件, 2,选择要给文件添加标签, 3,移除要添加的标签
     */
    public TagLabel(ITag tag, Color color, IPanel panel, int event) {
        super(tag.getName());
        this.tag = tag;
        this.panel = panel;
        this.event = event;
        this.color = color;
        this.setOpaque(true);// 背景不透明
        this.setBackground(color);
        this.setBorder(new RoundedBorder(Color.BLACK, 5));// 圆角边框
        this.addMouseListener(this);
    }

    /**
     * 带颜色的事件标签, 与文件相关联
     * 
     * @param tag
     * @param color
     * @param panel 绘制点击结果的面板
     * @param event 点击事件值: 1,展示该标签的文件, 2,选择要给文件添加标签, 3,移除要添加的标签
     * @param file
     */
    public TagLabel(ITag tag, Color color, IPanel panel, int event, IFile file) {
        super(tag.getName());
        this.tag = tag;
        this.panel = panel;
        this.event = event;
        this.file = file;
        this.color = color;
        this.type = 2;
        this.setOpaque(true);// 背景不透明
        this.setBackground(color);
        this.setBorder(new RoundedBorder(Color.BLACK, 5));// 圆角边框
        this.addMouseListener(this);
    }

    /**
     * 带颜色的事件标签,跟展示该标签的面板相关联，因为移除标签涉及到该面板的刷新
     *
     * @param tag       标签
     * @param color     背景颜色
     * @param panel     绘制结果的面板
     * @param event     点击事件值: 1,展示该标签的文件, 2,选择要给文件添加标签, 3,移除要添加的标签
     * @param tagWindow 标签的承载面板
     */
    public TagLabel(ITag tag, Color color, IPanel panel, int event, Tag tagWindow) {
        super(tag.getName());
        this.tag = tag;
        this.panel = panel;
        this.event = event;
        this.tagWindow = tagWindow;
        this.type = 1;
        this.setOpaque(true);// 背景不透明
        this.setBackground(color);
        this.color = color;
        this.setBorder(new RoundedBorder(Color.BLACK, 5));// 圆角边框
        this.addMouseListener(this);
    }

    /**
     * 无事件标签
     * 
     * @param text
     * @param color
     */
    public TagLabel(String text, Color color) {
        super(text);
        this.setOpaque(true);
        this.setBorder(new RoundedBorder(Color.BLACK, 5));// 圆角边框
        this.setBackground(color);
    }
    
    //设置事件类型
    public void setEvent(int event) {
        this.event = event;
    }

    //克隆
    public TagLabel clone(){
        return new TagLabel(tag, color, panel, event);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        TagService tagService = new TagService();
        if (e.getButton() == MouseEvent.BUTTON1) {
            // 左键点击
            if (event == 1) {
                // 找到标签下的文件
                HashMap<String, Set<ITag>> fileMap = tagService.getFileMapByTag(tag);
                HashMap<String, Set<IFile>> files = tagService.getFilesByTag(tag);
                // 显示结果
                panel.removeAll();
                files.forEach((dir, fileSet) -> {
                    //将路径包装成对象
                    IFile dirFile = new IFile();
                    dirFile.setDirectory(true);
                    dirFile.setPath(dir);
                    panel.add(new FileBox(dirFile, panel, fileMap));
                    panel.add(Box.createVerticalStrut(3));//垂直間距
                    fileSet.forEach(file -> {
                        FileBox row = new FileBox(file, panel, fileMap);
                        panel.add(row);
                        panel.add(Box.createVerticalStrut(3));//垂直間距
                    });
                    //panel.add(Box.createVerticalGlue());//撑满空的地方
                });
                panel.reload();
            } else if (event == 2) {
                // 将标签添加到下方待选列表
                if(panel.getTags().contains(tag)) return;
                panel.getTags().add(tag);
                //克隆要添加的标签,事件类型修改为移除，添加到下方，刷新
                TagLabel copy = clone();
                copy.setEvent(3);
                panel.add(copy);
                panel.reload();
            } else if (event == 3) {
                // 将标签从待选移除
                panel.getTags().remove(tag);
                panel.remove(this);
                panel.reload();
            }
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            // 右键删除标签，提示信息
            if (type == 1) {
                String tap = "你真的要删除[" + tag.getName() + "]标签吗?";
                int confirmDel = JOptionPane.showConfirmDialog(this, tap, "确认信息", JOptionPane.YES_NO_OPTION);
                if (confirmDel == JOptionPane.YES_OPTION) {
                    tagService.deleteTag(tag);
                    // 页面信息重载
                    tagWindow.reloadGroups();
                    tagWindow.reloadTags();
                }
            } else if (type == 2) {
                String tap = "你真的要移除该文件的[" + tag.getName() + "]标签吗?";
                int confirmDel = JOptionPane.showConfirmDialog(this, tap, "确认信息", JOptionPane.YES_NO_OPTION);
                if (confirmDel == JOptionPane.YES_OPTION) {
                    tagService.removeTag(tag, file);
                    this.setVisible(false);// 先隐藏
                }
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
}
