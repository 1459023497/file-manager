package gui;

import gui.component.FileLabel;
import gui.component.IPanel;
import tool.FileUtils;
import main.Starter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Set;

public class Home {
    private JFrame frame;
    private IPanel content;
    private Starter starter;

    private Boolean firstClick = true;

    public Home() {
        //窗口，面版初始化
        frame = new JFrame("文件管理");
        //frame.setIconImage(new ImageIcon("con\\add.jpeg").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT));
        content = new IPanel(new BorderLayout());
        content.setBackground(new Color(142, 147, 147));
        starter = new Starter();

        IPanel top = new IPanel(new Dimension(0, 80));
        JLabel label = new JLabel("文件夹");
        JTextField textField = new JTextField(15);
        JButton button = new JButton("扫描");
        JButton button1 =new JButton("标签");
        JLabel label1 = new JLabel("扫描成功");
        label1.setVisible(false);
        top.add(label);
        top.add(textField);
        top.add(button);
        top.add(button1);
        top.add(label1);
        content.add(top, BorderLayout.NORTH);
        //创建结果滚动面板
        IPanel center = new IPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(center);
        scrollPane.setOpaque(false);
        content.add(scrollPane, BorderLayout.CENTER);

        frame.setContentPane(content);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(400, 500);
        frame.setVisible(true);

        //以下为事件处理
        //扫描按钮点击事件
        button.addActionListener(e -> {
            //空路径
            if (textField.getText().equals("")) {
                JOptionPane.showMessageDialog(frame, "输入错误", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }
            //添加操作按钮,只有第一次点击添加
            label1.setVisible(true);
            if (firstClick) {
                firstClick = false;
                //文件写入数据库
                JButton button2 = new JButton("初始化");
                button2.addActionListener(e1 -> {
                    starter.init();
                    label1.setText("写入完成");
                });
                //展示重复文件
                JButton button3 = new JButton("查重");
                button3.addActionListener(e1 -> {
                    HashMap<String, Set<File>> refileMap = starter.getRefileMap();
                    center.removeAll();
                    refileMap.forEach((size, files) -> {
                        center.add(new JLabel("大小：" + FileUtils.getFileSizeString(size)));
                        files.forEach(file -> {
                            center.add(new JLabel(file.getPath()));
                        });
                    });
                    label1.setText("查重完成");
                    center.reload();
                });
                top.add(label1);
                top.add(button2);
                top.add(button3);
            }
            //获取路径，开始扫描
            center.removeAll();
            String path = textField.getText();
            starter.scan(path);
            HashMap<String, Set<File>> fileMap = starter.getFileMap();
            //显示结果
            fileMap.forEach((dir, files) -> {
                center.add(new FileLabel(dir));
                files.forEach(file -> center.add(new FileLabel(file)));
            });
            label1.setText("扫描完成");
            center.reload();
            content.reload();
        });

        //标签按钮点击事件
        button1.addActionListener(e -> {
            new Tag();
        });
    }

    public static void main(String[] args) {
        new Home();
    }
}
