package gui.component;

import java.util.Collections;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

import org.apache.commons.collections4.CollectionUtils;

import common.tool.TagColor;
import entity.ITag;

public class TagBox extends Box {

    public TagBox(ITag tag) {
        super(BoxLayout.X_AXIS);
        // this.setAlignmentX(Component.LEFT_ALIGNMENT);
        // this.setMaximumSize(new Dimension(3000, 20));
        // 展示标签
        TagColor color = TagColor.RED;
        color.init();
        this.add(new TagLabel(tag, color.next()));
        this.add(Box.createHorizontalStrut(5));
        // 展示关键词
        List<String> keyList = tag.getKeys();
        if (CollectionUtils.isNotEmpty(keyList)) {
            String concat = String.join(",", keyList);
            JLabel keys = new JLabel(concat);
            this.add(keys);
        }
    }

}
