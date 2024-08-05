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
import gui.window.TagSelector;

public class AddLabel extends JLabel implements MouseListener {
    private IFile file;

    /**
     * add tag button
     * 
     * @param file
     */
    public AddLabel(IFile file) {
        super();
        this.file = file;
        ImageIcon icon = new ImageIcon("src\\gui\\icon\\add.png");
        Image image = icon.getImage(); 
        Image scaledImage = image.getScaledInstance(20, 20, Image.SCALE_SMOOTH); // resize image
        ImageIcon scaledIcon = new ImageIcon(scaledImage); // new size ImageIcon

        setPreferredSize(new Dimension(scaledIcon.getIconWidth(), scaledIcon.getIconHeight())); // set size to image size
        setIcon(scaledIcon);
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        new TagSelector(file, this);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setBorder(BorderFactory.createLineBorder(Color.gray, 1, true));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setBorder(BorderFactory.createEmptyBorder());
    }
}
