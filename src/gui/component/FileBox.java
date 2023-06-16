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
    private TagLabel mainTagLabel;

    /**
     * file row : file + tags + add button
     * 
     * @param file    
     * @param panel   content panel
     * @param fileMap dictionary
     */
    public FileBox(IFile file, IPanel panel, Map<String, Set<ITag>> fileMap) {
        // Align : horizontal, left; filename + size + tags + button
        super(BoxLayout.X_AXIS);
        init(file, panel);
        if (isDir) {
            return;
        }
        // show tags
        Set<ITag> tags = fileMap.get(file.getId());
        TagColor color = TagColor.RED;
        color.init();
        if (tags != null) {
            tags.forEach(t -> {
                TagLabel tagLabel = new TagLabel(t, color.next(), panel, 1, file, this);
                if (t.getIsMain()){
                    mainTagLabel = tagLabel;
                }
                this.add(tagLabel);
                this.add(Box.createHorizontalStrut(5));
            });
        }
        // add button
        AddLabel addLabel = new AddLabel(file);
        this.add(addLabel);
    }

    /**
     * file row : file + tags + add button
     * 
     * @param file  
     * @param panel content panel
     */
    public FileBox(IFile file, IPanel panel) {
        // Align : horizontal, left; filename + size + tags + button
        super(BoxLayout.X_AXIS);
        init(file, panel);
        if (!isDir) {
            // show tags
            List<ITag> tags = file.getTags();
            TagColor color = TagColor.RED;
            color.init();
            if (CollectionUtils.isNotEmpty(tags)) {
                tags.forEach(t -> {
                    TagLabel tagLabel = new TagLabel(t, color.next(), panel, 1, file, this);
                    if (t.getIsMain()){
                        mainTagLabel = tagLabel;
                    }
                    this.add(tagLabel);
                    this.add(Box.createHorizontalStrut(5));
                });
            }
        }
        // tag button
        AddLabel addLabel = new AddLabel(file);
        this.add(addLabel);
    }

    private void init(IFile file, IPanel panel) {
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.setMaximumSize(new Dimension(3000, 20));
        // folder show path, file show size and tags
        isDir = file.isDirectory();
        fileLabel = new FileLabel(file, this);
        this.add(fileLabel);
        this.add(Box.createHorizontalStrut(5));
        // show size
        if (!isDir) {
            String fileSize = FileUtils.getFileSizeString(String.valueOf(file.getSize()));
            this.add(new TagLabel(fileSize, TagColor.GREY.getColor()));
            this.add(Box.createHorizontalStrut(5));
        }
    }

    public void setHighlight(String text) {
        fileLabel.setHighlight(text);
    }

    public TagLabel getMainTagLabel() {
        return mainTagLabel;
    }

    public void setMainTagLabel(TagLabel mainTagLabel) {
        this.mainTagLabel = mainTagLabel;
    }
    
}
