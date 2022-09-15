package gui.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import entity.IFile;
import entity.ITag;
import gui.component.FileBox;
import gui.component.IPanel;
import gui.component.TagLabel;
import service.FileService;
import service.TagService;
import tool.TagColor;

public class Tag {
    private IPanel subTop;
    private IPanel center;
    private JComboBox<ITag> groups;

    public Tag(JFrame father) {
        // 窗口，面版初始化
        JFrame frame = new JFrame("文件管理");
        IPanel content = new IPanel(new BorderLayout());
        content.setBackground(new Color(142, 147, 147));
        center = new IPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));// 新增行布局

        // 顶部加载面板，按钮和标签
        IPanel top = new IPanel(new Dimension(0, 80));
        // 新标签名字输入
        JTextField newTag = new JTextField(10);
        top.add(newTag);
        // 加载分组下拉列表
        groups = new JComboBox<>();
        reloadGroups();
        top.add(groups);
        // 标签子面板,加载全部标签
        subTop = new IPanel(new Dimension(400, 50));
        reloadTags();
        // 添加标签按钮，事件
        // 标签有上下级关系，可以进行分组，如 水果是一个标签，默认分组为空，即为最高级，下级可以为苹果，香蕉等标签
        JButton addTag = new JButton("新建标签");
        top.add(addTag);
        addTag.addActionListener((a) -> {
            // 获取新标签名和选择的父标签
            ITag selectedTag = (ITag) groups.getSelectedItem();
            String name = newTag.getText();
            String group = selectedTag.getName();
            // 判断标签输入是否为空
            if (name.length() == 0) {
                JOptionPane.showMessageDialog(addTag, "输入为空！");
                return;
            }
            // 提示信息
            String tap = "新建标签：" + name + "  分组：" + group + "?";
            int confirm = JOptionPane.showConfirmDialog(addTag, tap, "确认", JOptionPane.YES_NO_OPTION);
            // 确认添加新标签
            if (confirm == JOptionPane.YES_OPTION) {
                TagService tagService = new TagService();
                tagService.newTag(new ITag(name, selectedTag.getId()));
                tagService.close();
                // 重新加载标签列表和面板
                reloadGroups();
                reloadTags();
            }
        });
        // 显示全部文件按钮，点击事件
        JButton all = new JButton("全部");
        top.add(all);
        all.addActionListener(e -> {
            // 获取所有文件，按文件夹：文件的方式输出，带上文件的标签
            FileService fileService = new FileService();
            HashMap<String, Set<IFile>> files = fileService.getAllFiles();
            fileService.close();
            // 获取文件标签字典
            TagService tagService = new TagService();
            HashMap<String, Set<ITag>> fileMap = tagService.getFilesTagsMap();
            tagService.close();
            center.removeAll();
            files.forEach((dir, set) -> {
                FileBox dirRow = new FileBox(new IFile(dir), center,fileMap);
                center.add(dirRow);
                set.forEach(file -> {
                    // 文件行
                    FileBox row = new FileBox(file, center,fileMap);
                    center.add(row);
                });
            });
            center.reload();
        });
        // 添加标签子面板
        top.add(subTop);
        content.add(top, BorderLayout.NORTH);
        // 创建下方结果滚动面板，用于显示点击标签后的文件
        JScrollPane scrollPane = new JScrollPane(center);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);//设置滚轮速度
        content.add(scrollPane, BorderLayout.CENTER);

        frame.setContentPane(content);
        frame.setIconImage(
                new ImageIcon("src\\gui\\icon\\home.png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setSize(400, 500);
        frame.setLocationRelativeTo(father);
        frame.setVisible(true);
    }

    /**
     * 加载全部标签，以分组的类型展示：【父标签：子标签。。。】
     */
    public void reloadTags() {
        subTop.removeAll();
        TagService tagService = new TagService();
        // 获取标签和标签组的字典
        HashMap<String, ITag> tagMap = tagService.getTagsMap();
        HashMap<String, Set<String>> groupMap = tagService.getGroupsMap();
        tagService.close();
        TagColor color = TagColor.RED;
        groupMap.forEach((groupId, tagSet) -> {
            subTop.add(new JLabel("【"));
            // 获取父标签
            ITag father = tagMap.get(groupId);
            subTop.add(new TagLabel(father, color.next(), center, 1, this));
            // 获取子标签
            if (!tagSet.isEmpty()) {
                subTop.add(new JLabel(":"));
                tagSet.forEach(tagId -> {
                    ITag son = tagMap.get(tagId);
                    subTop.add(new TagLabel(son, color.next(), center, 1, this));
                });
            }
            subTop.add(new JLabel("】"));
        });
        subTop.reload();
    }

    /**
     * 加载全部分组
     * 
     * @param groups 下拉选择列表
     */
    public void reloadGroups() {
        groups.removeAllItems();
        TagService tagService = new TagService();
        ArrayList<ITag> tags = tagService.getAllTags();
        // 无分组的顶级标签
        ITag blankTag = new ITag();
        blankTag.setId("无分组");
        blankTag.setName("无分组");
        groups.addItem(blankTag);
        tags.forEach(tag -> {
            groups.addItem(tag);
        });
        tagService.close();
    }
}
