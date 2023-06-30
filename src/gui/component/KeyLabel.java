package gui.component;

import javax.swing.JLabel;

import entity.ITag;
import gui.base.RoundedBorder;
import service.TagService;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class KeyLabel extends JLabel{

    /**
     * show tag's key
     */
    public KeyLabel(ITag tag, String key) {
        super(key);
        this.setOpaque(true);
        this.setBorder(new RoundedBorder(Color.BLACK, 5));
        this.setBackground(Color.LIGHT_GRAY);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // right-click to delete
                if (e.getButton() == MouseEvent.BUTTON3) {
                    TagService tagService = new TagService();
                    tagService.deleteTagKey(tag.getId(), key);
                    KeyLabel.this.setVisible(false);
                }
            }
            
        });
    }

}
