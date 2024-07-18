package test;

import gui.component.Pager;

import javax.swing.*;

/**
 * @author yty
 * @date 2024/4/29
 * @description
 */
public class FrameTest {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Pager pager = new Pager(1000);
        frame.add(pager);


        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }
}
