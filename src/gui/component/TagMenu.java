package gui.component;

import com.alibaba.druid.util.StringUtils;
import entity.IFile;
import entity.ITag;
import service.TagService;

import javax.swing.*;
import java.awt.*;

public class TagMenu extends JPopupMenu{

    public TagMenu(ITag tag, TagLabel tagLabel, IFile file, FileBox fileBox){
        TagService tagService = new TagService();
        JMenuItem rename = new JMenuItem("重命名");
        JMenuItem setMain = new JMenuItem("设置为主标签");
        JMenuItem delete = new JMenuItem("删除");
        this.add(setMain);
        this.add(rename);
        this.add(delete);

        rename.addActionListener(e -> {
            // rename input
            String input = JOptionPane.showInputDialog(tagLabel, "新命名", tag.getName());
            if (!StringUtils.isEmpty(input)) {
                    tag.setName(input);
                    tagService.renameTag(tag);
                    // refresh
                    tagLabel.setText(input);

            }
        });

        delete.addActionListener(e -> {
            tagService.removeTag(tag, file);
            tagLabel.setVisible(false);
        });

        setMain.addActionListener(e -> {
            if(tag.getIsMain()){
                return;
            }
            tagService.setMainTag(tag, file);
            tagLabel.setMainTag(true);
            //reset old main label and new main label
            TagLabel oldMainLabel = fileBox.getMainTagLabel();
            if (oldMainLabel != null){
                oldMainLabel.setForeground(Color.BLACK);
            }
            fileBox.setMainTagLabel(tagLabel);
        });
    }
    
}
