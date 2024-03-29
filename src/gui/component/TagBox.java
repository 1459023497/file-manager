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

    /**
     * show tag and keys
     * @param tag
     */
    public TagBox(ITag tag) {
        super(BoxLayout.X_AXIS);
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.setMaximumSize(new Dimension(3000, 20));
        // show tag
        TagColor color = TagColor.RED;
        this.add(new TagLabel(tag, color.next()));
        this.add(Box.createHorizontalStrut(5));
        // show keys
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
