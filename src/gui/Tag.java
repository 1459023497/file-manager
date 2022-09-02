package gui;

import gui.component.FileBox;
import gui.component.IPanel;
import gui.component.TagLabel;
import jdbc.JDBCConnector;
import service.FileService;
import service.TagService;
import tool.TagColor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Set;

public class Tag {
    private JFrame frame;
    private IPanel content;

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
        tagService = new TagService();
        fileService = new FileService();

        //顶部加载项
        IPanel top = new IPanel(new Dimension(0, 80));
        //显示全部文件按钮，点击事件
        JButton all = new JButton("全部");
        top.add(all);
        all.addActionListener(e -> {
            //获取所有文件，按文件夹：文件的方式输出，带上文件的标签
            HashMap<String, Set<File>> files = fileService.getAllFiles();
            fileService.close();
            center.removeAll();
            files.forEach((dir, set) -> {
                FileBox dirRow = new FileBox(new File(dir),center);
                center.add(dirRow);
                set.forEach(file -> {
                    //文件行
                    FileBox row = new FileBox(file, center);
                    center.add(row);
                });
            });
            center.reload();
        });
        //加载全部标签
        HashMap<String, Set<String>> tagMap = tagService.getTagsMap();
        tagService.close();
        TagColor color = TagColor.RED;
        tagMap.forEach((group, set) -> {
            top.add(new TagLabel(group, color.next(), center,1));
            top.add(new JLabel(":"));
            set.forEach(name -> top.add(new TagLabel(name, color.next(), center,1)));
        });
        content.add(top, BorderLayout.NORTH);
        //创建下方结果滚动面板，用于显示文件
        JScrollPane scrollPane = new JScrollPane(center);
        scrollPane.setOpaque(false);
        content.add(scrollPane, BorderLayout.CENTER);

        frame.setContentPane(content);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setSize(400, 500);
        frame.setVisible(true);
    }
}
