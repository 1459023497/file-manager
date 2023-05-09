package test;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * 刮图实现
 */
public class ScratchCard extends JPanel implements MouseListener, MouseMotionListener {

    private BufferedImage backImg, maskImg;
    private Graphics2D maskG2D;
    private int mouseX, mouseY, brushSize = 100;

    public ScratchCard(String backgroundPath) {
        try {
            backImg = ImageIO.read(new FileInputStream(backgroundPath));
            backImg = resize(backImg, 600, 400);
            maskImg = ImageIO.read(new FileInputStream("src\\gui\\icon\\silver.jpeg"));
            maskImg = resize(maskImg, 600, 400);
            maskG2D = maskImg.createGraphics();
            addMouseListener(this);
            addMouseMotionListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backImg, 0, 0, null);
        g.drawImage(maskImg, 0, 0, null);
    }

    private void scratch(int x, int y) {
        maskG2D.setComposite(AlphaComposite.Clear);
        maskG2D.fillOval(x - brushSize / 2, y - brushSize / 2, brushSize, brushSize);
        repaint();
    }

    // MouseListener and MouseMotionListener methods
    @Override
    public void mousePressed(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        scratch(mouseX, mouseY);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        scratch(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Scratch Card");
        ScratchCard scratchCard = new ScratchCard("src\\gui\\icon\\night.jpg");
        frame.getContentPane().add(scratchCard);
        frame.setSize(600, 400);
        frame.setVisible(true);
    }

    public BufferedImage resize(BufferedImage img, int newWidth, int newHeight) {
        Image tmp = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
    
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
    
        return resizedImage;
    }
    
}
