package gui.component;

import gui.base.IButton;
import gui.base.IFrame;
import gui.base.IPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FrameBar extends JPanel {
    private int state = 0;

    /**
     * diy framebar
     * 
     * @param frame father frame
     */
    public FrameBar(IFrame frame) {
        setLayout(new BorderLayout());
        setBackground(Color.lightGray);
        IButton btnMin = new IButton();
        btnMin.setBackground(Color.GREEN);
        IButton btnMax = new IButton();
        btnMax.setBackground(Color.ORANGE);
        IButton btnClose = new IButton();
        btnClose.setBackground(Color.RED);

        IPanel centerBar = new IPanel();
        IPanel leftBar = new IPanel();
        SpaceBox comboBox = new SpaceBox();
        leftBar.add(comboBox);
    
        IPanel rightBar = new IPanel(new FlowLayout(FlowLayout.RIGHT));
        rightBar.add(btnMin);
        rightBar.add(btnMax);
        rightBar.add(btnClose);

        add(leftBar, BorderLayout.WEST);
        add(centerBar, BorderLayout.CENTER);
        add(rightBar, BorderLayout.EAST);

        btnMin.addActionListener(e -> {
            // min
            frame.setExtendedState(JFrame.ICONIFIED);
        });

        btnMax.addActionListener(e -> {
            if (state == JFrame.MAXIMIZED_BOTH) {
                // normal
                frame.setExtendedState(JFrame.NORMAL);
                state = JFrame.NORMAL;

            } else if (state == JFrame.NORMAL) {
                // max
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                state = JFrame.MAXIMIZED_BOTH;
            }

        });

        btnClose.addActionListener(e -> {
            frame.dispose();
        });

        // add listener to support moving frame
        MouseAdapter ma = new MouseAdapter() {
            private int x;
            private int y;

            /*
             * record mouse position
             */
            @Override
            public void mousePressed(MouseEvent e) {
                x = e.getX();
                y = e.getY();
            }

            /*
             * mouse in
             */
            @Override
            public void mouseEntered(MouseEvent e) {
                frame.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }

            /*
             * mouse out
             */
            @Override
            public void mouseExited(MouseEvent e) {
                frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // compute the last position
                int left = frame.getLocation().x + e.getX() - x;
                int top = frame.getLocation().y + e.getY() - y;
                // reset location
                frame.setLocation(left, top);
            }
        };
        centerBar.addMouseListener(ma);
        // listen for mouse moving
        centerBar.addMouseMotionListener(ma);
    }

}
