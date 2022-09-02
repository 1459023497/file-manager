package gui.component;

import jdbc.JDBCConnector;
import service.TagService;
import tool.FileUtils;
import tool.TagColor;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class FileBox extends Box{
    private AddLabel addLabel;

    public FileBox(int axis) {
        super(axis);
    }

    public FileBox(File file, IPanel panel){
        //默认构造为水平排列,左对齐, 形式=文件名+大小+标签
        super(BoxLayout.X_AXIS);
        this.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        this.add(new FileLabel(file));
        //只有文件才展示大小
        if(!file.isDirectory()){
            String fileSize = FileUtils.getFileSizeString(String.valueOf(file.length()));
            this.add(new TagLabel(fileSize, TagColor.GREY.getColor()));
        }
        TagService tagService = new TagService();
        ArrayList<String> tags = tagService.getTagsByFile(file);
        tagService.close();
        TagColor color = TagColor.RED;
        tags.forEach(s -> this.add(new TagLabel(s,color.next(),panel,1)));
        //添加标签功能
        addLabel = new AddLabel(file);
        //addLabel.setVisible(false);
        this.add(addLabel);
    }

}
