package gui.component;

import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;

import org.apache.commons.collections4.CollectionUtils;

import common.tool.FileUtils;
import common.tool.TagColor;
import entity.IFile;
import entity.ITag;
import gui.component.base.IPanel;

public class FileBox extends Box {
    private FileLabel fileLabel;
    private boolean isDir;

    /**
     * 生成一个文件行，包括文件信息，标签，和添加标签按钮
     * 
     * @param file    文件
     * @param panel   承载面板
     * @param fileMap 文件标签字典
     */
    public FileBox(IFile file, IPanel panel, Map<String, Set<ITag>> fileMap) {
        // 默认构造为水平排列,左对齐, 形式=文件名+大小+标签+按钮
        super(BoxLayout.X_AXIS);
        init(file, panel);
        if (isDir) {
            return;
        }
        // 展示标签
        Set<ITag> tags = fileMap.get(file.getId());
        TagColor color = TagColor.RED;
        color.init();
        if (tags != null) {
            tags.forEach(t -> {
                this.add(new TagLabel(t, color.next(), panel, 1, file));
                this.add(Box.createHorizontalStrut(5));
            });
        }
        // 添加标签按钮
        AddLabel addLabel = new AddLabel(file);
        this.add(addLabel);
    }

    /**
     * 生成一个文件行，包括文件信息，标签，和添加标签按钮
     * 
     * @param file  文件
     * @param panel 承载面板
     */
    public FileBox(IFile file, IPanel panel) {
        // 默认构造为水平排列,左对齐, 形式=文件名+大小+标签+按钮
        super(BoxLayout.X_AXIS);
        init(file, panel);
        if (!isDir) {
            // 展示标签
            List<ITag> tags = file.getTags();
            TagColor color = TagColor.RED;
            color.init();
            if (CollectionUtils.isNotEmpty(tags)) {
                tags.forEach(t -> {
                    this.add(new TagLabel(t, color.next(), panel, 1, file));
                    this.add(Box.createHorizontalStrut(5));
                });
            }
        }
        // 添加标签按钮
        AddLabel addLabel = new AddLabel(file);
        this.add(addLabel);
    }

    private void init(IFile file, IPanel panel) {
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.setMaximumSize(new Dimension(3000, 20));// 设置组件变化的最大大小
        // 文件夹只展示路径,只有文件才展示大小和标签
        isDir = file.isDirectory();
        fileLabel = new FileLabel(file, this);
        this.add(fileLabel);
        this.add(Box.createHorizontalStrut(5));
        // 展示大小
        if (!isDir) {
            String fileSize = FileUtils.getFileSizeString(String.valueOf(file.getSize()));
            this.add(new TagLabel(fileSize, TagColor.GREY.getColor()));
            this.add(Box.createHorizontalStrut(5));
        }
    }

    public void setHighlight(String text) {
        fileLabel.setHighlight(text);
    }

}
