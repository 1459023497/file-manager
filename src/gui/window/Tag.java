package gui.window;

import common.AppContext;
import common.model.event.FireEvent;
import common.model.event.impl.PagerEvent;
import common.model.observer.Observer;
import common.myenum.EventType;
import common.myenum.InfoType;
import common.tool.TagColor;
import entity.IFile;
import entity.ITag;
import gui.base.IDialog;
import gui.base.IFrame;
import gui.base.IPanel;
import gui.base.MyScrollPane;
import gui.component.Pager;
import gui.component.TagLabel;
import gui.component.TreeSelect;
import service.FileService;
import service.TagService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Tag implements Observer {
    public static final String WIN_NAME = "Tag";
    private final IPanel subTop;
    private final IPanel center;
    private final TreeSelect tagGroups;
    private final FileService fileService;
    private final TagService tagService;
    private final IFrame frame;
    private final Pager pager;

    public Tag(JFrame father) {
        fileService = new FileService();
        tagService = new TagService();

        // top panel for loading menu and tags
        IPanel top = new IPanel(new Dimension(0, 100));
        top.setLayout(new BorderLayout());
        frame = new IFrame("标签管理", top);
        center = frame.getCenter();
        pager = frame.getPager();
        pager.setTotalCount(0);
        pager.addObserver(this);

        frame.setLocationRelativeTo(father);
        IPanel menu = new IPanel();
        top.add(menu, BorderLayout.NORTH);
        // tag name input
        JTextField newTag = new JTextField(10);
        menu.add(newTag);
        // load tag's group
        JTree tree = tagService.getTagTree();
        tagGroups = new TreeSelect(tree);
        menu.add(tagGroups);


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

        all.addActionListener(e -> queryByPage(pager.getPageSize(),pager.getPageNum()));

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                AppContext.currentWin = Tag.this;
                AppContext.currentFrame = frame;
            }
        });

        addTag.addActionListener(a -> {
            // get input name and group
            ITag selectedTag = (ITag) tagGroups.getSelectedItem();
            String name = newTag.getText();
            if (name.length() == 0) {
                new IDialog(frame, "输入为空！", InfoType.ERROR);
                return;
            }
            String groupId = "全部";
            String groupName = "全部";
            if (selectedTag != null){
                groupId = selectedTag.getId();
                groupName = selectedTag.getName();
            }
            // confirm message
            String tap = "新建标签：" + name + "  分组：" + groupName + "?";
            int confirm = JOptionPane.showConfirmDialog(frame, tap, "确认", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tagService.newTag(new ITag(name, groupId));
                reloadGroups();
                reloadTags();
            }
        });

        bindTag.addActionListener(e -> new TagBinder(frame));

    }

    public void queryAll(){
        queryByPage(pager.getPageSize(), pager.getPageNum());
    }

    private void queryByPage(int pageSize, int pageNum) {
        List<IFile> result = fileService.getFilesByPage(pageSize,pageNum);
        frame.showContents(result);
        int total = fileService.totalCount();
        pager.setTotalCount(total);
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
        JTree tree = tagService.getTagTree();
        tagGroups.reloadTree(tree);
    }

    public JFrame getFrame() {
        return frame;
    }

    @Override
    public void update(FireEvent event) {
        if (event.getEventType() == EventType.PAGE_EVENT){
            PagerEvent pagerEvent = (PagerEvent) event;
            queryByPage(pagerEvent.getPageSize(), pagerEvent.getPageNum());
        }

    }
}
