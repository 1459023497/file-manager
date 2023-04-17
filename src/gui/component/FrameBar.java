package gui.component;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class FrameBar extends JPanel {

    /**
     * 自定义窗口栏
     * 
     * @param frame 关联窗口
     */
    public FrameBar(JFrame frame) {
        setLayout(new BorderLayout());
        setBackground(Color.lightGray);
        IButton btnMin = new IButton();
        btnMin.setBackground(Color.GREEN);
        IButton btnMax = new IButton();
        btnMax.setBackground(Color.ORANGE);
        IButton btnClose = new IButton();
        btnClose.setBackground(Color.RED);
        IPanel leftBar = new IPanel();
        IPanel rightBar = new IPanel(new FlowLayout(FlowLayout.RIGHT));
        rightBar.add(btnMin);
        rightBar.add(btnMax);
        rightBar.add(btnClose);
        add(leftBar, BorderLayout.CENTER);
        add(rightBar, BorderLayout.EAST);

        btnMin.addActionListener(e -> {
            // 最小化
            frame.setExtendedState(JFrame.ICONIFIED);
        });

        btnMax.addActionListener(e -> {
            // 最大化
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        });

        btnClose.addActionListener(e -> {
            // 关闭
            frame.dispose();
        });

        // 添加鼠标监听器，支持拖动窗口
        MouseAdapter ma = new MouseAdapter() {
            private int x;
            private int y;

            /*
             * 记录鼠标按下时的坐标
             */
            @Override
            public void mousePressed(MouseEvent e) {
                x = e.getX();
                y = e.getY();
            }

            /*
             * 鼠标移进标题栏时，设置鼠标图标为移动图标
             */
            @Override
            public void mouseEntered(MouseEvent e) {
                frame.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }

            /*
             * 鼠标移出标题栏时，设置鼠标图标为默认指针
             */
            @Override
            public void mouseExited(MouseEvent e) {
                frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // 计算窗口移动后的位置
                int left = frame.getLocation().x + e.getX() - x;
                int top = frame.getLocation().y + e.getY() - y;
                // 移动窗口
                frame.setLocation(left, top);
            }
        };
        leftBar.addMouseListener(ma);
        // 接收鼠标拖动事件
        leftBar.addMouseMotionListener(ma);

        // TODO: 边框调整还未生效，待完成
        MouseAdapter btnMa = new MouseAdapter() {
            Border border;

            @Override
            public void mouseEntered(MouseEvent e) {
                Object source = e.getSource();
                if (source instanceof IButton) {
                    IButton btn = (IButton) source;
                    border = btn.getBorder();
                    btn.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Object source = e.getSource();
                if (source instanceof IButton) {
                    IButton btn = (IButton) source;
                    btn.setBorder(border);
                }
            }
        };

        btnMin.addMouseListener(btnMa);
        btnMax.addMouseListener(btnMa);
        btnClose.addMouseListener(btnMa);
    }

}
