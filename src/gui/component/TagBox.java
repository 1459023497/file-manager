package gui.component;

import java.awt.Component;
import java.awt.Dimension;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;

import org.apache.commons.collections4.CollectionUtils;

import common.tool.TagColor;
import entity.ITag;

public class TagBox extends Box {

    public TagBox(ITag tag) {
        super(BoxLayout.X_AXIS);
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.setMaximumSize(new Dimension(3000, 20));
        // 展示标签
        TagColor color = TagColor.RED;
        this.add(new TagLabel(tag, color.next()));
        this.add(Box.createHorizontalStrut(5));
        // 展示关键词
        List<String> keyList = tag.getKeys();
        if (CollectionUtils.isNotEmpty(keyList)) {
            keyList.forEach(key->{
                KeyLabel keyLabel = new KeyLabel(tag, key);
                this.add(keyLabel);
                this.add(Box.createHorizontalStrut(5));
            });
        }
    }

}
