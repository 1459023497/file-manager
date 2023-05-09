package gui.component;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

public class ScratchPanel extends IPanel implements MouseListener, MouseMotionListener {
    private BufferedImage maskImg;
    private Graphics2D maskG2D;
    private int mouseX, mouseY, brushSize = 100;

    public ScratchPanel(String imagePath, int width, int height) {
        try {
            maskImg = ImageIO.read(new FileInputStream(imagePath));
            maskImg = resize(maskImg, width, height);
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
        //绘制文字
        g.drawString("hello world", 30, 30);
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

    public BufferedImage resize(BufferedImage img, int newWidth, int newHeight) {
        Image tmp = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return resizedImage;
    }

}
