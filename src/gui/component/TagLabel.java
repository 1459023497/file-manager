package gui.component;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import entity.IFile;
import entity.ITag;
import gui.window.Tag;
import service.TagService;

public class TagLabel extends JLabel implements MouseListener {
    private Tag tagWindow;//标签窗口
    private IPanel panel;//绘制点击结果的面板
    private int event;//点击事件值: 1,展示该标签的文件, 2,选择要给文件添加标签, 3,移除要添加的标签
    private ITag tag;//关联的标签
    private IFile file;//关联的文件
    private int type;//标签类型：1，在标签菜单的标签 2，文件的标签

    /**
     * 带颜色的标签事件
     * @param tag
     * @param color
     * @param panel
     * @param event
     */
    public TagLabel(ITag tag, Color color, IPanel panel, int event) {
        super(tag.getName());
        this.tag = tag;
        this.panel = panel;
        this.event = event;
        this.setOpaque(true);//背景不透明
        this.setBackground(color);
        this.addMouseListener(this);
    }

    /**
     * 带颜色的事件标签, 与文件相关联
     * @param tag
     * @param color
     * @param panel
     * @param event
     * @param file 
    */
    public TagLabel(ITag tag, Color color, IPanel panel, int event, IFile file) {
        super(tag.getName());
        this.tag = tag;
        this.panel = panel;
        this.event = event;
        this.file = file;
        this.type = 2;
        this.setOpaque(true);//背景不透明
        this.setBackground(color);
        this.addMouseListener(this);
    }

    /**
     * 带颜色的事件标签,跟展示该标签的面板相关联，因为移除标签涉及到该面板的刷新
     *
     * @param tag 标签
     * @param color 背景颜色
     * @param panel 绘制结果的面板
     * @param event 点击事件值: 1,展示该标签的文件, 2,选择要给文件添加标签, 3,移除要添加的标签
     * @param tagWindow 标签的承载面板
     */
    public TagLabel(ITag tag, Color color, IPanel panel, int event, Tag tagWindow) {
        super(tag.getName());
        this.tag = tag;
        this.panel = panel;
        this.event = event;
        this.tagWindow = tagWindow;
        this.type = 1;
        this.setOpaque(true);//背景不透明
        this.setBackground(color);
        this.addMouseListener(this);
    }

    /**
     * 无事件标签
     * @param text
     * @param color
     */
    public  TagLabel(String text, Color color){
        super(text);
        this.setOpaque(true);
        this.setBackground(color);
    }

    public void setEvent(int event) {
        this.event = event;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1){
            //左键点击
            if(event==1){
                //打开数据库链接
                TagService tagService = new TagService();
                //找到标签下的文件
                HashMap<String,Set<ITag>> fileMap = tagService.getFileMapByTag(tag);
                HashMap<String, Set<IFile>> files = tagService.getFilesByTag(tag);
                //显示结果
                panel.removeAll();
                files.forEach((dir, fileSet) -> {
                    panel.add(new FileLabel(dir));
                    fileSet.forEach(file ->{
                        FileBox row = new FileBox(file, panel);
                        row.setMap(fileMap);
                        panel.add(row);
                    });
                });
                panel.reload();
                //关闭链接
                tagService.close();
            }else if(event==2){
                //将标签添加到待选列表
                panel.getTags().add(tag);
                this.setEvent(3);
                panel.add(this);
                panel.reload();
            }else if(event ==3){
                //将标签从待选移除
                panel.getTags().remove(tag);
                panel.remove(this);
                panel.reload();
            }
        }else if(e.getButton() == MouseEvent.BUTTON3){
            //右键删除标签，提示信息
            if(type == 1){
                String tap = "你真的要删除["+ tag.getName() +"]标签吗?";
                int confirmDel = JOptionPane.showConfirmDialog(this, tap, "确认信息", JOptionPane.YES_NO_OPTION);
                if (confirmDel == JOptionPane.YES_OPTION){
                    TagService tagService = new TagService();
                    tagService.deleteTag(tag);
                    tagService.close();
                    JOptionPane.showMessageDialog(this, "删除成功！");
                    //页面信息重载
                    tagWindow.reloadGroups();
                    tagWindow.reloadTags();
                }
            }else if(type ==2 ){
                String tap = "你真的要移除该文件的["+ tag.getName() +"]标签吗?";
                int confirmDel = JOptionPane.showConfirmDialog(this, tap, "确认信息", JOptionPane.YES_NO_OPTION);
                if (confirmDel == JOptionPane.YES_OPTION){
                    TagService tagService = new TagService();
                    tagService.removeTag(tag, file);
                    tagService.close();
                    JOptionPane.showMessageDialog(this, "移除成功！");
                    this.setVisible(false);//先隐藏
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
        //线条边框
        this.setBorder(BorderFactory.createLineBorder(Color.red, 1, true));

    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.setBorder(BorderFactory.createEmptyBorder());
    }
}
