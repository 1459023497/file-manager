package gui.window;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import common.AppContext;
import common.tool.TagColor;
import entity.IFile;
import entity.ITag;
import gui.base.IPanel;
import gui.component.AddLabel;
import gui.component.TagLabel;
import service.TagService;

public class TagSellector {
    private TagService tagService;

    // adding tags frame
    public TagSellector(IFile file, AddLabel addLabel) {
        // frame initialization
        JWindow window = new JWindow();
        IPanel content = new IPanel(new Dimension(210, 300));
        content.setBorder(BorderFactory.createLineBorder(Color.black, 1, true));
        window.setContentPane(content);
        // transparency
        if(AppContext.UI_TRANSPARENT){
            window.setOpacity(0.2f);
        }  
        window.pack();
        window.setSize(200, 300);
        // get conponent's frame
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(addLabel);
        window.setLocationRelativeTo(frame);
        window.setVisible(true);

        // top panel show all tags
        IPanel top = new IPanel(new Dimension(200, 130));
        top.setBorder(BorderFactory.createLineBorder(Color.gray, 1, true));
        // down panel show sellected tags
        IPanel down = new IPanel(new Dimension(200, 130));
        down.setBorder(BorderFactory.createLineBorder(Color.gray, 1, true));
        down.setTags(new HashSet<>());
        tagService = new TagService();
        Map<String, ITag> tagMap = tagService.getTagsMap();
        Map<String, Set<String>> groupMap = tagService.getGroupsMap();
        TagColor color = TagColor.RED;
        groupMap.forEach((group, set) -> {
            // each pick will add it to the down panel
            top.add(new TagLabel(tagMap.get(group), color.next(), down, 2));
            if (set.isEmpty())
                return;
            top.add(new JLabel(":"));
            set.forEach(son -> top.add(new TagLabel(tagMap.get(son), color.next(), down, 2)));
        });
        // confirm to add
        JButton confirm = new JButton("确定");
        confirm.addActionListener(e -> {
            // get adding tags
            HashSet<ITag> tags = down.getTags();
            if (tags.isEmpty()) {
                return;
            }
            tagService.tags(tags, file);
            //entermine focus window
            Object currentFrame = AppContext.currentWin;
            if (currentFrame instanceof Home){
                ((Home)currentFrame).queryAll();
            }else if (currentFrame instanceof Tag){
                ((Tag)currentFrame).queryAll();
            }
            window.dispose();
        });

        JButton cancel = new JButton("取消");
        cancel.addActionListener(e -> {
            window.dispose();
        });

        window.add(top);
        window.add(down);
        window.add(confirm);
        window.add(cancel);
    }

}
