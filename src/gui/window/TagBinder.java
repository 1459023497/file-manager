package gui.window;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import entity.ITag;
import gui.component.IFrame;
import gui.component.IPanel;
import service.TagService;

public class TagBinder {
    private IFrame frame;
    private IPanel center;
    private JComboBox<ITag> tagSellector;
    private TagService tagService;

    public TagBinder(IFrame father) {
        tagService = new TagService();

        IPanel top = new IPanel(new Dimension(0, 80));
        frame = new IFrame("标签管理", top);
        center = frame.getCenter();
        frame.setLocationRelativeTo(father);
        JTextField input = new JTextField(10);
        tagSellector = new JComboBox<>();
        reloadSellector();
        JButton confirm  = new JButton("确认");
        top.add(input);
        top.add(tagSellector);
        top.add(confirm);

        confirm.addActionListener(e->{
            //确定绑定标签关键词
            String text = input.getText();
            String[] keys = text.split(",");
            ITag tag = (ITag) tagSellector.getSelectedItem();
            tagService.tagKeys(tag.getId(), keys);
            List<ITag> tags = tagService.getAllTagsWithKeys();
            frame.showContents(tags);
        });
    }

    /**
     * 加载全部分组
     * 
     * @param groups 下拉选择列表
     */
    public void reloadSellector() {
        tagSellector.removeAllItems();
        ArrayList<ITag> tags = tagService.getAllTags();
        tags.forEach(tag -> {
            tagSellector.addItem(tag);
        });
    }

}
