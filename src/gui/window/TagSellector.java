package gui.window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JWindow;

import common.AppContext;
import entity.IFile;
import entity.ITag;
import gui.component.AddLabel;
import gui.component.IPanel;
import gui.component.TagLabel;
import service.TagService;
import tool.TagColor;

public class TagSellector {
    private TagService tagService;

    // 添加标签时的选择窗口
    public TagSellector(IFile file, AddLabel addLabel, Point point) {
        // 窗口初始化
        JWindow window = new JWindow();
        IPanel content = new IPanel(new Dimension(210, 300));
        content.setBorder(BorderFactory.createLineBorder(Color.black, 1, true));
        window.setContentPane(content);
        //透明化
        window.setOpacity(0.2f);
        window.pack();
        window.setSize(200, 300);
        window.setLocationRelativeTo(addLabel);
        window.setVisible(true);
    
        // 顶部显示全部标签和操作按钮
        IPanel top = new IPanel(new Dimension(200, 100));
        top.setBorder(BorderFactory.createLineBorder(Color.gray, 1, true));
        // 底部显示已选择的标签
        IPanel down = new IPanel(new Dimension(200, 100));
        down.setBorder(BorderFactory.createLineBorder(Color.gray, 1, true));
        down.setTags(new HashSet<>());
        // 顶部查询加载全部标签
        tagService = new TagService();
        HashMap<String,ITag> tagMap = tagService.getTagsMap();
        HashMap<String, Set<String>> groupMap = tagService.getGroupsMap();
        TagColor color = TagColor.RED;
        groupMap.forEach((group, set) -> {
            // 每次选择的标签会添加到下方的待选面板
            top.add(new TagLabel(tagMap.get(group), color.next(), down, 2));
            if (set.isEmpty())
                return;
            top.add(new JLabel(":"));
            set.forEach(son -> top.add(new TagLabel(tagMap.get(son), color.next(), down, 2)));
        });
        // 点击确定按钮后给文件添加标签
        JButton confirm = new JButton("确定");
        confirm.addActionListener(e -> {
            // 获取待添加的标签
            HashSet<ITag> tags = down.getTags();
            if (tags.isEmpty()) {
                JOptionPane.showMessageDialog(confirm, "你未选择标签！", "错误", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // 提示用户确认
            String name = file.getName() == null ? file.getPath() : file.getName();
            String tap = "确认给[" + name + "] 添加标签 " + tags + " 吗？";
            int confirmTag = JOptionPane.showConfirmDialog(confirm, tap, "确认信息", JOptionPane.YES_NO_OPTION);
            if (confirmTag == 0) {
                // 打标签
                tagService.tags(tags, file);
            }
            JOptionPane.showMessageDialog(confirm, "添加成功！");
            AppContext.getHome().queryAll();
            window.dispose();
        });
        // 点击取消关闭选择窗口
        JButton cancel = new JButton("取消");
        cancel.addActionListener(e -> {
            window.dispose();// 关闭窗口
        });

        window.add(top);
        window.add(down);
        window.add(confirm);
        window.add(cancel);
    }

}
