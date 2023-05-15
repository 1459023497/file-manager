package gui.component;

import javax.swing.JLabel;

import entity.ITag;
import service.TagService;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class KeyLabel extends JLabel{

    /**
     * 标签关键词
     */
    public KeyLabel(ITag tag, String key) {
        super(key);
        this.setOpaque(true);
        this.setBorder(new RoundedBorder(Color.BLACK, 5));// 圆角边框
        this.setBackground(Color.LIGHT_GRAY);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 右键删除
                if (e.getButton() == MouseEvent.BUTTON3) {
                    // 处理右键点击事件
                    TagService tagService = new TagService();
                    tagService.deleteTagKey(tag.getId(), key);
                    KeyLabel.this.setVisible(false);
                }
            }
            
        });
    }

}
