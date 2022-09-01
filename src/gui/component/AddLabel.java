package gui.component;

import gui.TagSellector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

public class AddLabel extends JLabel implements MouseListener {
    private File file;

    //绑定文件的添加标签按钮
    public AddLabel(File file){
        super("+++");
        this.file = file;
        this.addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        new TagSellector(file, this);
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
