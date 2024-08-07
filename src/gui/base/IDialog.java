package gui.base;

import common.AppContext;
import common.myenum.InfoType;
import common.tool.TagColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class IDialog extends JWindow {
    public IDialog(IFrame frame, String info, int type) {
        super();
        RoundPanel panel = new RoundPanel();
        setContentPane(panel);
        if (type == InfoType.INFO) {
            panel.setBackground(TagColor.GREEN.getColor());
        } else if (type == InfoType.ERROR) {
            panel.setBackground(TagColor.RED.getColor());
        }
        panel.setBorder(new RoundedBorder(Color.BLACK, 20));
        JLabel label = new JLabel(info);
        panel.add(label);

        if (AppContext.UI_TRANSPARENT) {
            setOpacity(0.1f);
            setBackground(new Color(0, 0, 0, 0));
        }
        setFocusableWindowState(false);
        setSize(100, 20);

        // position
        Point point = frame.getCenterPointOnScreen();
        point.x = point.x - getWidth() / 2 + 15; // fix
        point.y = point.y - frame.getHeight() / 2 + 30;
        setLocation(point);
        // setLocationRelativeTo(frame);
        pack();
        setVisible(true);

        // follow the frame's position changes
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                // position
                Point point = frame.getCenterPointOnScreen();
                point.x = point.x - getWidth() / 2 + 5; // fix
                point.y = point.y - frame.getHeight() / 2 + 30;
                setLocation(point);
            }
        });

        // dispose after 2 seconds
        Timer timer = new Timer(2000, e -> dispose());
        timer.setRepeats(false);
        timer.start();
    }

}
