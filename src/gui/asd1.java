package gui;

import javax.swing.*;

public class asd1 {
    private JFrame frame;
    private JTextField textField1;
    private JButton button1;
    private JPanel root;
    private JLabel 文件夹Label;

    public asd1() {
        JFrame frame = new JFrame("asd1");
        frame.setContentPane(root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        button1.addActionListener(e -> {
            JLabel jLabel= new JLabel("新的");
            frame.getContentPane().add(jLabel);
            frame.getContentPane().revalidate();
            frame.getContentPane().repaint();
        });
    }

    public static void main(String[] args) {
        new asd1();
    }
}
