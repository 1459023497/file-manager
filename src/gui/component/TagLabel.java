package gui.component;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Set;

import javax.swing.*;

import entity.IFile;
import entity.ITag;
import service.TagService;

public class TagLabel extends JLabel implements MouseListener {
    private IPanel panel;//绘制点击结果的面板
    private int event;
    private ITag tag;//关联的标签

    /**
     * 带颜色的事件标签
     *
     * @param tag 标签
     * @param color 背景颜色
     * @param panel 绘制结果的面板
     * @param event 点击事件值: 1,展示该标签的文件, 2,选择要给文件添加标签, 3,移除要添加的标签
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

    //TODO: 删除标签事件

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
            if(event==1){
                //打开数据库链接
                TagService tagService = new TagService();
                //找到标签下的文件
                HashMap<String, Set<IFile>> files = tagService.getFilesByTag(tag);
                //显示结果
                panel.removeAll();
                files.forEach((dir, fileSet) -> {
                    panel.add(new FileLabel(dir));
                    fileSet.forEach(file ->{
                        FileBox row = new FileBox(file, panel);
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
        }else if(e.getButton() == MouseEvent.BUTTON2){
            //右键删除标签，提示信息
            String tap = "你真的要删除["+ tag.getName() +"]标签吗?";
            int confirmDel = JOptionPane.showConfirmDialog(this, tap, "确认信息", JOptionPane.YES_NO_OPTION);
            if (confirmDel == 1){
                TagService tagService = new TagService();
                tagService.deleteTag(tag);
                tagService.close();
                JOptionPane.showMessageDialog(this, "删除成功！");
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
