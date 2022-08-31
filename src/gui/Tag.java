package gui;

import gui.component.FileLabel;
import gui.component.IPanel;
import gui.component.TagLabel;
import jdbc.JDBCConnector;
import service.FileService;
import service.TagService;
import tool.TagColor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Tag {
    private JFrame frame;
    private IPanel content;

    private JDBCConnector conn;

    private TagService tagService;

    private FileService fileService;

    public Tag() {
        //窗口，面版初始化
        frame = new JFrame("文件管理");
        content = new IPanel(new BorderLayout());
        content.setBackground(new Color(142, 147, 147));
        IPanel center = new IPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));//新增行布局
        //打开数据库，链接标签服务
        conn = new JDBCConnector();
        tagService = new TagService(conn);
        fileService = new FileService(conn);

        //顶部加载项
        IPanel top = new IPanel(new Dimension(0, 80));
        //显示全部文件按钮，点击事件
        JButton all =  new JButton("全部");
        top.add(all);
        all.addActionListener(e -> {
            HashMap<String,Set<File>> files = fileService.getAllFiles();
            center.removeAll();
            files.forEach((dir,set)->{
                center.add(new FileLabel(dir));
                set.forEach(file -> {
                    Box row = Box.createHorizontalBox();
                    row.setAlignmentX(JComponent.LEFT_ALIGNMENT);//要设置左对齐才能正确布局
                    row.add(new JLabel(file.getName()));
                    ArrayList<String> tags = tagService.getTagsByFile(file);
                    TagColor color = TagColor.RED;
                    tags.forEach(s -> {
                        row.add(new TagLabel(s,color.next(),center));
                    });
                    center.add(row);
                });
            });
            center.reload();
        });
        //加载全部标签
        HashMap<String, Set<String>> tagMap = tagService.getTagsMap();
        TagColor color = TagColor.RED;
        tagMap.forEach((group, set) -> {
            top.add(new TagLabel(group, color.next(),center));
            top.add(new JLabel(":"));
            set.forEach(name -> top.add(new TagLabel(name, color.next(), center)));
        });
        content.add(top, BorderLayout.NORTH);
        //创建下方结果滚动面板，用于显示文件
        JScrollPane scrollPane = new JScrollPane(center);
        scrollPane.setOpaque(false);
        content.add(scrollPane, BorderLayout.CENTER);

        frame.setContentPane(content);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(400, 500);
        frame.setVisible(true);
    }


}
