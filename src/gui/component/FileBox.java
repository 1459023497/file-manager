package gui.component;

import service.TagService;
import tool.FileUtils;
import tool.TagColor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class FileBox extends Box{
    /**
     * 生成一个文件行，包括文件信息，标签，和添加标签按钮
     * @param file
     * @param panel
     */
    public FileBox(File file, IPanel panel){
        //默认构造为水平排列,左对齐, 形式=文件名+大小+标签
        super(BoxLayout.X_AXIS);
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
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
        AddLabel addLabel = new AddLabel(file);
        // TODO: 2022/9/3 文件获取焦点时才展示添加
        this.add(addLabel);
    }

}
