package gui.window;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import entity.ITag;
import gui.component.HintTextField;
import gui.component.base.IFrame;
import gui.component.base.IPanel;
import service.TagService;

public class TagBinder {
    private IFrame frame;
    private JComboBox<ITag> tagSellector;
    private TagService tagService;

    public TagBinder(IFrame father) {
        tagService = new TagService();

        IPanel top = new IPanel(new Dimension(0, 60));
        frame = new IFrame("标签管理", top);
        frame.setLocationRelativeTo(father);
        HintTextField input = new HintTextField("支持输入多个关键词，以空格分隔");
        input.setColumns(30);
        tagSellector = new JComboBox<>();
        reloadSellector();
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
            tagService.tagKeys(tag.getId(), keys);
            List<ITag> tags = tagService.getAllTagsWithKeys();
            frame.showContents(tags);
        });
    }

    /**
     * reload groups
     */
    public void reloadSellector() {
        tagSellector.removeAllItems();
        ArrayList<ITag> tags = tagService.getAllTags();
        tags.forEach(tag -> {
            tagSellector.addItem(tag);
        });
    }

}
