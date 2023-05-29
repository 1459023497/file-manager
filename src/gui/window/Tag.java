package gui.window;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import common.tool.TagColor;
import entity.IFile;
import entity.ITag;
import gui.component.TagLabel;
import gui.component.base.IFrame;
import gui.component.base.IPanel;
import service.FileService;
import service.TagService;

public class Tag {
    public static final String WIN_NAME = "Tag";
    private IPanel subTop;
    private IPanel center;
    private JComboBox<ITag> groups;
    private FileService fileService;
    private TagService tagService;
    private IFrame frame;

    public Tag(JFrame father) {
        fileService = new FileService();
        tagService = new TagService();

        // 顶部加载面板，按钮和标签
        IPanel top = new IPanel(new Dimension(0, 100));
        top.setLayout(new BorderLayout());
        frame = new IFrame("标签管理", top);
        center = frame.getCenter();
        frame.setLocationRelativeTo(father);
        IPanel menu = new IPanel();
        top.add(menu, BorderLayout.NORTH);
        // 新标签名字输入
        JTextField newTag = new JTextField(10);
        menu.add(newTag);
        // 加载分组下拉列表
        groups = new JComboBox<>();
        reloadGroups();
        menu.add(groups);
        // 标签子面板,加载全部标签
        subTop = new IPanel(new Dimension(400, 60));
        JScrollPane scrollPane =  new JScrollPane(subTop);
        reloadTags();
        // 添加标签按钮，事件
        // 标签有上下级关系，可以进行分组，如 水果是一个标签，默认分组为空，即为最高级，下级可以为苹果，香蕉等标签
        JButton addTag = new JButton("新建标签");
        menu.add(addTag);
        // 显示全部文件按钮，点击事件
        JButton all = new JButton("全部");
        menu.add(all);
        //关联标签
        JButton bindTag = new JButton("关联标签");
        menu.add(bindTag);
        // 添加标签子面板
        top.add(scrollPane, BorderLayout.SOUTH);

        all.addActionListener(e -> {
            queryAll();
        });

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
            int confirm = JOptionPane.showConfirmDialog(frame, tap, "确认", JOptionPane.YES_NO_OPTION);
            // 确认添加新标签
            if (confirm == JOptionPane.YES_OPTION) {
                tagService.newTag(new ITag(name, selectedTag.getId()));
                // 重新加载标签列表和面板
                reloadGroups();
                reloadTags();
            }
        });

        bindTag.addActionListener(e->{
            new TagBinder(frame);
        });

    }

    public void queryAll() {
        // 获取所有文件，按文件夹：文件的方式输出，带上文件的标签
        List<IFile> files = fileService.getAllFiles();
        frame.showContents(files);
    }

    /**
     * 加载全部标签，以分组的类型展示：【父标签：子标签。。。】
     */
    public void reloadTags() {
        subTop.removeAll();
        // 获取标签和标签组的字典
        Map<String, ITag> tagMap = tagService.getTagsMap();
        Map<String, Set<String>> groupMap = tagService.getGroupsMap();
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
        ArrayList<ITag> tags = tagService.getAllTags();
        // 无分组的顶级标签
        ITag blankTag = new ITag();
        blankTag.setId("无分组");
        blankTag.setName("无分组");
        groups.addItem(blankTag);
        tags.forEach(tag -> {
            groups.addItem(tag);
        });
    }

    public JFrame getFrame() {
        return frame;
    }
}
