package gui;

import gui.component.AddLabel;
import gui.component.IPanel;
import gui.component.TagLabel;
import jdbc.JDBCConnector;
import service.TagService;
import tool.TagColor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class TagSellector {
    private JDBCConnector conn;
    private TagService tagService;
    //添加标签时的选择窗口
    public TagSellector(File file, AddLabel addLabel){
        //窗口初始化
        JWindow window = new JWindow();
        IPanel content = new IPanel(new Dimension(100,200));
        window.setContentPane(content);
        window.pack();
        window.setSize(100,150);
        window.setVisible(true);
        // TODO: 2022/9/1  窗口边框，背景，定位
        //顶部显示全部标签
        IPanel top = new IPanel(new Dimension(100,50));
        //底部显示选择的标签
        IPanel down  = new IPanel(new Dimension(100,50));
        down.setTags(new ArrayList<>());
        //顶部查询加载全部标签
        conn = new JDBCConnector();
        tagService = new TagService(conn);
        HashMap<String, Set<String>> tagMap = tagService.getTagsMap();
        TagColor color = TagColor.RED;
        tagMap.forEach((group, set) -> {
            //每次选择的标签会添加到下方的待选面板
            top.add(new TagLabel(group, color.next(),down,2));
            top.add(new JLabel(":"));
            set.forEach(name -> top.add(new TagLabel(name, color.next(),down,2)));
        });
        //点击确定按钮后给文件添加标签
        JButton button = new JButton("确定");
        button.addActionListener(e -> {
            //获取待添加的标签
            ArrayList<String> tags = down.getTags();
            tags.forEach(t->{
                tagService.tag(t,file);
            });
        });


    }
}