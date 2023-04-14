package gui.component;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import javax.swing.*;

public class IButton extends JButton{

    /*
     * 自定义圆形按钮
     */
    public IButton() {
        super();
        Iinit();
    }

    private void Iinit() {
        setBackground(new Color(0, 0, 0, 0)); // 将背景颜色设为透明
        setFocusPainted(false); // 取消焦点边框
        setContentAreaFilled(false);// 取消周围区域
        //setForeground(Color.BLACK); // 设置前景颜色为黑色
        // 获取外部设置的大小
        Dimension size = getPreferredSize();
        size.width = size.height = Math.max(size.width, size.height);
        setPreferredSize(size);
        setPreferredSize(new Dimension(20, 20)); // 强制设置大小为
    }

    public IButton(String label) {
        super(label);
        Iinit();
    }

    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) { // 如果按钮被按下
            g.setColor(Color.darkGray); // 绘制灰色的圆形背景
        } else {
            g.setColor(getBackground()); // 使用按钮的背景色绘制圆形背景
        }
        g.fillOval(0, 0, getSize().width - 1, getSize().height - 1); // 绘制圆形背景
        super.paintComponent(g);
    }

    // 绘制圆形边框
    protected void paintBorder(Graphics g) {
        g.setColor(Color.lightGray);
        g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
    }

    // shape对象用于保存按钮的形状，用于监听事件
    Shape shape;

    public boolean contains(int x, int y) {
        if ((shape == null) || (!shape.getBounds().equals(getBounds()))) {
            // 构造一个圆的对象
            shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
        }
        // 判断鼠标坐标是否落在按钮形状内
        return shape.contains(x, y);
    }
}
