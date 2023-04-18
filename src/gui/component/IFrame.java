package gui.component;

import java.awt.*;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import entity.IFile;

public class IFrame extends JFrame {
    private IPanel content;
    private IPanel center;

    /**
     * 统一窗口: 窗口栏+功能+内容展示
     */
    public IFrame(String title, IPanel top) {
        super(title);
        //// 窗口，面版初始化
        content = new IPanel(new BorderLayout());
        IPanel northPanel = new IPanel(new BorderLayout());
        FrameBar frameBar = new FrameBar(this);
        northPanel.add(frameBar, BorderLayout.NORTH);
        northPanel.add(top, BorderLayout.CENTER);
        content.add(northPanel, BorderLayout.NORTH);

        // 创建结果滚动面板
        this.center = new IPanel();;
        // 内边距
        center.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(center);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);// 设置滚轮速度
        content.add(scrollPane, BorderLayout.CENTER);

        setContentPane(content);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //背景透明化
        setUndecorated(true);// 去掉窗口菜单
        setOpacity(0.1f);
        pack();
        setSize(400, 500);
        setIconImage(new ImageIcon("src\\gui\\icon\\home.png").getImage());
        setLocationRelativeTo(null);// 居中显示
        setVisible(true);
    }

    /**
     * 展示内容
     */
    public void showContents(List<IFile> files) {
        // 按文件夹：文件的方式输出，带上文件的标签
        Map<String, List<IFile>> map = files.stream().collect(Collectors.groupingBy(IFile::getBelong));
        center.removeAll();
        map.forEach((dir, list) -> {
            center.addFileBox(dir, center);
            list.forEach(file -> center.addFileBox(file, center));
        });
        center.reload();

    }

    public IPanel getContent() {
        return content;
    }

    public void setContent(IPanel content) {
        this.content = content;
    }

    public IPanel getCenter() {
        return center;
    }

    public void setCenter(IPanel center) {
        this.center = center;
    }

    
}
