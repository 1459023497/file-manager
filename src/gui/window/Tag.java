package gui.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import common.tool.TagColor;
import entity.IFile;
import entity.ITag;
import gui.component.TagLabel;
import gui.component.base.IFrame;
import gui.component.base.IPanel;
import gui.component.base.MyScrollPane;
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

        // top panel for loading menu and tags
        IPanel top = new IPanel(new Dimension(0, 100));
        top.setLayout(new BorderLayout());
        frame = new IFrame("标签管理", top);
        center = frame.getCenter();
        frame.setLocationRelativeTo(father);
        IPanel menu = new IPanel();
        top.add(menu, BorderLayout.NORTH);
        // tag name input
        JTextField newTag = new JTextField(10);
        menu.add(newTag);
        // load tag's group
        groups = new JComboBox<>();
        reloadGroups();
        menu.add(groups);
        // there are father ana son tags relationships, any tag can be a group; when its group is null, it will be the top tag
        JButton addTag = new JButton("新建标签");
        menu.add(addTag);
        // load all the files
        JButton all = new JButton("全部");
        menu.add(all);
        JButton bindTag = new JButton("关联标签");
        menu.add(bindTag);
        // load all tags
        subTop = new IPanel(new Dimension(380, 80));
        MyScrollPane scrollPane = new MyScrollPane(subTop);
        top.add(scrollPane, BorderLayout.CENTER);
        reloadTags();
        all.addActionListener(e -> {
            queryAll();
        });

        addTag.addActionListener((a) -> {
            // get input name and group
            ITag selectedTag = (ITag) groups.getSelectedItem();
            String name = newTag.getText();
            String group = selectedTag.getName();
            if (name.length() == 0) {
                JOptionPane.showMessageDialog(addTag, "输入为空！");
                return;
            }
            // confirm message
            String tap = "新建标签：" + name + "  分组：" + group + "?";
            int confirm = JOptionPane.showConfirmDialog(frame, tap, "确认", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tagService.newTag(new ITag(name, selectedTag.getId()));
                reloadGroups();
                reloadTags();
            }
        });

        bindTag.addActionListener(e -> {
            new TagBinder(frame);
        });

    }

    public void queryAll() {
        List<IFile> files = fileService.getAllFiles();
        frame.showContents(files);
    }

    /**
     * load tags：【tag：tag, tag...】
     */
    public void reloadTags() {
        subTop.removeAll();
        // dict : tag and group
        Map<String, ITag> tagMap = tagService.getTagsMap();
        Map<String, Set<String>> groupMap = tagService.getGroupsMap();
        TagColor color = TagColor.RED;
        groupMap.forEach((groupId, tagSet) -> {
            subTop.add(new JLabel("【"));
            // get father
            ITag father = tagMap.get(groupId);
            subTop.add(new TagLabel(father, color.next(), center, 1, this));
            // get son
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
     * reload all groups
     * 
     */
    public void reloadGroups() {
        groups.removeAllItems();
        ArrayList<ITag> tags = tagService.getAllTags();
        // no grouped tags
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
