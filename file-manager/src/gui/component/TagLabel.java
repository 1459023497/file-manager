package gui.component;

import jdbc.JDBCConnector;
import service.TagService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.HashMap;
import java.util.Set;

public class TagLabel extends JLabel implements MouseListener {
    private TagService tagService;
    private IPanel panel;//绘制点击结果的面板

    /**
     * 带颜色标签
     *
     * @param text 文本
     * @param color 背景
     * @param panel 绘制结果的面板
     */
    public TagLabel(String text, Color color, IPanel panel) {
        super(text);
        this.panel = panel;
        this.setOpaque(true);//背景不透明
        this.setBackground(color);
        this.addMouseListener(this);
    }



    @Override
    public void mouseClicked(MouseEvent e) {
        //打开数据库链接
        JDBCConnector conn = new JDBCConnector();
        tagService = new TagService(conn);
        //找到标签下的文件
        HashMap<String, Set<File>> files = tagService.getFilesByTag(getText());
        //显示结果
        panel.removeAll();
        files.forEach((dir, fileSet) -> {
            panel.add(new FileLabel(dir));
            fileSet.forEach(file -> panel.add(new JLabel(file.getName())));
        });
        panel.reload();
        //关闭链接
        conn.close();
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
