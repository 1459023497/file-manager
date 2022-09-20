package gui.component;

import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;

import entity.IFile;
import entity.ITag;
import tool.FileUtils;
import tool.TagColor;

public class FileBox extends Box {


    /**
     * 生成一个文件行，包括文件信息，标签，和添加标签按钮
     * 
     * @param file 文件
     * @param panel 承载面板
     * @param fileMap 文件标签字典
     */
    public FileBox(IFile file, IPanel panel, HashMap<String, Set<ITag>> fileMap) {
        // 默认构造为水平排列,左对齐, 形式=文件名+大小+标签+按钮
        super(BoxLayout.X_AXIS);
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        //this.setPreferredSize(new Dimension(0,5));
        this.setMaximumSize(new Dimension(3000,20));//设置组件变化的最大大小
        // 文件夹只展示路径
        if (file.isDirectory()) {
            this.add(new FileLabel(file.getPath(), this));
            this.add(Box.createHorizontalStrut(5)); //占位的隐藏间距
            this.add(new AddLabel(file));
            return;
        }
        // 只有文件才展示大小和标签
        this.add(new FileLabel(file,this));
        this.add(Box.createHorizontalStrut(5)); //占位的隐藏间距
        //展示大小
        String fileSize = FileUtils.getFileSizeString(String.valueOf(file.getSize()));
        this.add(new TagLabel(fileSize, TagColor.GREY.getColor()));
        this.add(Box.createHorizontalStrut(5)); //占位的隐藏间距
        //展示标签
        Set<ITag> tags = fileMap.get(file.getId());
        TagColor color = TagColor.RED;
        if(tags != null){
            tags.forEach(t -> {
                this.add(new TagLabel(t, color.next(), panel, 1, file));
                this.add(Box.createHorizontalStrut(5)); //占位的隐藏间距}
            });
        }
        // 添加标签按钮
        AddLabel addLabel = new AddLabel(file);
        // TODO: 2022/9/3 文件获取焦点时才展示添加
        this.add(addLabel);
    } 

}
