package gui.component;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import entity.IFile;
import entity.ITag;
import service.TagService;

public class TagLabel extends JLabel implements MouseListener {
    private IPanel panel;//绘制点击结果的面板
    private int event;
    private ITag iTag;//关联的标签

    /**
     * 带颜色的事件标签
     *
     * @param text 文本
     * @param color 背景颜色
     * @param panel 绘制结果的面板
     * @param event 点击事件值: 1,展示该标签的文件, 2,选择要给文件添加标签, 3,移除要添加的标签
     */
    public TagLabel(String text, Color color, IPanel panel, int event) {
        super(text);
        this.panel = panel;
        this.event = event;
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
        if(event==1){
            //打开数据库链接
            TagService tagService = new TagService();
            //找到标签下的文件
            ITag tag = new ITag(getText());
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
            panel.getTags().add(this.getText());
            this.setEvent(3);
            panel.add(this);
            panel.reload();
        }else if(event ==3){
            //将标签从待选移除
            panel.getTags().remove(this.getText());
            panel.remove(this);
            panel.reload();
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
