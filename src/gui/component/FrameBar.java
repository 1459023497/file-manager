package gui.component;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class FrameBar extends JPanel {


    /**
     * 自定义窗口栏
     * @param frame 关联窗口
     */
    public FrameBar(JFrame frame) {
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        setBackground(Color.lightGray);
        JButton btnMin = new JButton("一");
        JButton btnMax = new JButton("口");
        JButton btnClose = new JButton("x");
        add(btnMin);
        add(btnMax);
        add(btnClose);

        btnMin.addActionListener(e->{
            //最小化
            frame.setExtendedState(JFrame.ICONIFIED);
        });

        btnMax.addActionListener(e->{
            //最大化
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        });

        btnClose.addActionListener(e->{
            frame.dispose();
        });
    }

}
