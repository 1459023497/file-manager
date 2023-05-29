package gui.component.base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

import javax.swing.JButton;

public class IButton extends JButton {

    /*
     * diy round button
     */
    public IButton() {
        super();
        Iinit();
    }

    private void Iinit() {
        setBackground(new Color(0, 0, 0, 0)); // transparent
        setFocusPainted(false); // cancel focus border
        setContentAreaFilled(false);// don't fill the content area
        // setForeground(Color.BLACK); 
        Dimension size = getPreferredSize();
        size.width = size.height = Math.max(size.width, size.height);
        setPreferredSize(size);
        setPreferredSize(new Dimension(20, 20)); // forced size

        // change color 
        addMouseListener(new MouseAdapter() {
            Color color;
            @Override
            public void mouseEntered(MouseEvent e) {
                color = getBackground();
                setBackground(Color.BLACK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(color);
            }
        });
    }

    public IButton(String label) {
        super(label);
        Iinit();
    }

    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            // press the button
            g.setColor(Color.darkGray); 
        } else {
            // owned color
            g.setColor(getBackground());
        }
        g.fillOval(0, 0, getSize().width - 1, getSize().height - 1); // fill rounded background
        super.paintComponent(g);
    }

    // paint rounded border
    protected void paintBorder(Graphics g) {
        g.setColor(Color.lightGray);
        g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
    }

    // create a rounded shape for listeners
    Shape shape;

    public boolean contains(int x, int y) {
        if ((shape == null) || (!shape.getBounds().equals(getBounds()))) {
            // creat a rouded shape
            shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
        }
        // it contains mouse point?
        return shape.contains(x, y);
    }
}
