package gui.component;

import com.alibaba.druid.util.StringUtils;
import common.AppContext;
import entity.ISpace;
import gui.base.IPanel;
import gui.tool.ImageUtils;
import org.apache.commons.collections4.CollectionUtils;
import service.SpaceService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SpaceBox extends IPanel {
    private SpaceService spaceService;
    private JComboBox<ISpace> comboBox;
    private JButton add;
    private JButton del;
    private List<String> names;

    public SpaceBox() {
        spaceService = new SpaceService();
        List<ISpace> spaceList = spaceService.getAllSpaces();
        if (CollectionUtils.isNotEmpty(spaceList)){
            AppContext.currSpace = spaceList.get(0).getId();
        }
        this.names = spaceList.stream().map(ISpace::getName).collect(Collectors.toList());
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        ImageIcon addIcon = ImageUtils.resizeImage("src\\gui\\icon\\add.png", 20, 20);
        ImageIcon delIcon = ImageUtils.resizeImage("src\\gui\\icon\\del.png", 20, 20);
        add = new JButton(addIcon);
        del = new JButton(delIcon);
        add.setPreferredSize(new Dimension(addIcon.getIconWidth(), addIcon.getIconHeight()));
        del.setPreferredSize(new Dimension(delIcon.getIconWidth(), delIcon.getIconHeight()));
        comboBox = new JComboBox<>(spaceList.toArray(new ISpace[0]));
        if(CollectionUtils.isNotEmpty(spaceList)){
            comboBox.setSelectedIndex(0);
        }

        this.add(comboBox);
        this.add(add);
        this.add(del);

        // events
        add.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(AppContext.currentFrame, "新的空间");
            if (!StringUtils.isEmpty(input) && !names.contains(input)) {
                ISpace space = spaceService.add(input);
                comboBox.addItem(space);
            }
        });

        del.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(AppContext.currentFrame, "删除空间涉及包括的文件，请输入666确认");
            if (!StringUtils.isEmpty(input) && Objects.equals("666", input)) {
                // delete selected item
                ISpace space = (ISpace) comboBox.getSelectedItem();
                if (space != null) {
                    spaceService.deleteByName(space.getName());
                }
                int selectedIndex = comboBox.getSelectedIndex();
                if (selectedIndex != -1) {
                    comboBox.removeItemAt(selectedIndex);
                }
            }
        });

        comboBox.addActionListener(e->{
            //update space
            ISpace space = (ISpace) comboBox.getSelectedItem();
            if (space!=null){
                AppContext.currSpace = space.getId();
            }
        });
    }

}
