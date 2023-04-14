package gui.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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
        // super("+++");
        super();
        this.file = file;
        ImageIcon icon = new ImageIcon("src\\gui\\icon\\add.png");
        Image image = icon.getImage(); // 获取Image对象
        Image scaledImage = image.getScaledInstance(20, 20, Image.SCALE_SMOOTH); // 调整大小
        ImageIcon scaledIcon = new ImageIcon(scaledImage); // 创建新的ImageIcon

        setPreferredSize(new Dimension(scaledIcon.getIconWidth(), scaledIcon.getIconHeight())); // 设置JLabel大小
        setIcon(scaledIcon);
        addMouseListener(this);
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
        setBorder(BorderFactory.createLineBorder(Color.gray, 1, true));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setBorder(BorderFactory.createEmptyBorder());
    }
}
