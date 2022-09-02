package gui;

import gui.component.AddLabel;
import gui.component.IPanel;
import gui.component.TagLabel;
import service.TagService;
import tool.TagColor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TagSellector {
    private TagService tagService;
    //添加标签时的选择窗口
    public TagSellector(File file, AddLabel addLabel){
        //窗口初始化
        JWindow window = new JWindow();
        IPanel content = new IPanel(new Dimension(200,300));
        window.setContentPane(content);
        window.pack();
        window.setSize(200,300);
        window.setLocationRelativeTo(addLabel);
        window.setVisible(true);
        // TODO: 2022/9/1  窗口边框，背景，定位
        //顶部显示全部标签
        IPanel top = new IPanel(new Dimension(200,100));
        top.setBorder(BorderFactory.createLineBorder(Color.red, 1, true));
        //底部显示选择的标签
        IPanel down  = new IPanel(new Dimension(200,100));
        down.setBorder(BorderFactory.createLineBorder(Color.red, 1, true));
        down.setTags(new HashSet<>());
        //顶部查询加载全部标签
        tagService = new TagService();
        HashMap<String, Set<String>> tagMap = tagService.getTagsMap();
        TagColor color = TagColor.RED;
        tagMap.forEach((group, set) -> {
            //每次选择的标签会添加到下方的待选面板
            top.add(new TagLabel(group, color.next(),down,2));
            top.add(new JLabel(":"));
            set.forEach(name -> top.add(new TagLabel(name, color.next(),down,2)));
        });
        //点击确定按钮后给文件添加标签
        JButton confirm = new JButton("确定");
        confirm.addActionListener(e -> {
            //获取待添加的标签
            HashSet<String> tags = down.getTags();
            if(tags.isEmpty()) return;
            tags.forEach(t->
                //打标签
                tagService.tag(t,file)
            );
        });
        //点击取消关闭选择窗口
        JButton cancel = new JButton("取消");
        cancel.addActionListener(e -> {
            tagService.close();//关闭数据库连接
            window.dispose();//关闭窗口
        });

        window.add(top);
        window.add(down);
        window.add(confirm);
        window.add(cancel);
    }
}
