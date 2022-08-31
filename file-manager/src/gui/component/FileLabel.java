package gui.component;

import service.FileService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * 实现了鼠标监听的自定义标签
 */
public class FileLabel extends JLabel implements MouseListener {

    private FileService fileService;
    public FileLabel(String text){
        super(text);
        this.addMouseListener(this);
    }

    /**
     * 变色标签
     * @param text
     * @param color
     */
    public FileLabel(String text, Color color){
        super(text);
        this.setBackground(color);
        this.addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //点击打开文件夹
        fileService = new FileService();
        fileService.openDir(this.getText());
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
        this.setBorder(BorderFactory.createLineBorder(Color.red,1,true));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.setBorder(BorderFactory.createEmptyBorder());
    }
}
