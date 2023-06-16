package gui.component;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import com.alibaba.druid.util.StringUtils;

import entity.IFile;
import entity.ITag;
import service.TagService;

public class TagMenu extends JPopupMenu{

    public TagMenu(ITag tag, TagLabel tagLabel, IFile file, FileBox fileBox){
        TagService tagService = new TagService();
        JMenuItem rename = new JMenuItem("重命名");
        JMenuItem setMain = new JMenuItem("设置为主标签");
        JMenuItem delete = new JMenuItem("删除");
        this.add(setMain);
        this.add(rename);
        this.add(delete);

        rename.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                // rename input
                String input = JOptionPane.showInputDialog(tagLabel, "新命名", tag.getName());
                if (!StringUtils.isEmpty(input)) {
                        tag.setName(input);
                        tagService.renameTag(tag);
                        // refresh
                        tagLabel.setText(input);   
        
                }
            }
        });

        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tagService.removeTag(tag, file);
                tagLabel.setVisible(false);
            }
        });

        setMain.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tagService.setMainTag(tag, file);
                tagLabel.setMainTag(true);
                //reset old main label and new main label
                TagLabel oldMainLabel = fileBox.getMainTagLabel();
                if (oldMainLabel != null){
                    oldMainLabel.setForeground(Color.BLACK);
                }
                fileBox.setMainTagLabel(tagLabel);
            }
        });
    }
    
}
