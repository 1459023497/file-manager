package gui.base;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class MyScrollPane extends JScrollPane {

    public MyScrollPane(Component component) {
        super(component);
        getVerticalScrollBar().setUI(new MyScrollBarUI(0));
        getHorizontalScrollBar().setUI(new MyScrollBarUI(1));
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }
}
