package gui.window;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import entity.ITag;
import gui.base.IFrame;
import gui.base.IPanel;
import gui.component.HintTextField;
import service.TagService;

public class TagBinder {
    private final IFrame frame;
    private final JComboBox<ITag> tagSellector;
    private final TagService tagService;

    public TagBinder(IFrame father) {
        tagService = new TagService();

        IPanel top = new IPanel(new Dimension(0, 60));
        frame = new IFrame("标签管理", top);
        frame.setLocationRelativeTo(father);
        HintTextField input = new HintTextField("支持输入多个关键词，以空格分隔");
        input.setColumns(30);
        tagSellector = new JComboBox<>();
        reloadSelector();
        JButton confirm  = new JButton("确认");
        JLabel hint = new JLabel("右键点击关键词可以删除");
        top.add(input);
        top.add(tagSellector);
        top.add(confirm);
        top.add(hint);
    
        frame.showContents(tagService.getAllTagsWithKeys());

        confirm.addActionListener(e->{
            //add key to the tag
            String text = input.getText();
            if (text.length() == 0) return;
            String[] keys = text.split(" ");
            ITag tag = (ITag) tagSellector.getSelectedItem();
            if (tag != null) {
                tagService.tagKeys(tag.getId(), keys);
            }
            List<ITag> tags = tagService.getAllTagsWithKeys();
            frame.showContents(tags);
        });
    }

    /**
     * reload groups
     */
    public void reloadSelector() {
        tagSellector.removeAllItems();
        List<ITag> tags = tagService.getAllTags();
        tags.forEach(tagSellector::addItem);
    }

}
