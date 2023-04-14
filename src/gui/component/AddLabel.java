package gui.component;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import entity.IFile;
import gui.window.TagSellector;

public class AddLabel extends JLabel implements MouseListener {
    private IFile file;

    /**
     * 绑定文件的添加标签按钮，可以给文件添加标签
     * 
     * @param file
     */
    public AddLabel(IFile file) {
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
        // 线条边框
        this.setBorder(BorderFactory.createLineBorder(Color.red, 1, true));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.setBorder(BorderFactory.createEmptyBorder());
    }
}
