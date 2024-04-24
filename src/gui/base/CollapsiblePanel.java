package gui.base;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;

public class CollapsiblePanel extends IPanel {
    IPanel top;
    IPanel center;
    boolean isOpened = true;

    public CollapsiblePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.gray));
        top = new IPanel();
        top.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton open = new JButton("收起");
        top.add(open);

        center = new IPanel();
        // inner border
        center.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isOpened = !isOpened;
                center.setVisible(isOpened);
                open.setText(isOpened ? "收起" : "展开");
            }
        });

    }

    public void addTitile(Component component) {
        top.add(component);
    }

    public void addCentent(Component component) {
        center.add(component);
    }

    public void addCentents(Component... components) {
        for (Component component : components) {
            center.add(component);
        }
    }

}
