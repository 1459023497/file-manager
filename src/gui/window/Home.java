package gui.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import common.AppContext;
import entity.IFile;
import entity.ITag;
import gui.component.FileLabel;
import gui.component.FrameBar;
import gui.component.IPanel;
import main.Starter;
import service.FileService;
import service.TagService;
import tool.FileUtils;

public class Home {
    public final static String WIN_NAME = "Home";
    private JFrame frame;
    private IPanel content;
    private IPanel center;
    private Starter starter;
    private FileService fileService;
    private TagService tagService;

    public Home() {
        // 窗口，面版初始化
        fileService = new FileService();
        tagService = new TagService();
        frame = new JFrame("文件管理");
        content = new IPanel(new BorderLayout());
        starter = new Starter();

        IPanel northPanel = new IPanel(new BorderLayout());
        FrameBar frameBar = new FrameBar(frame);
        northPanel.add(frameBar, BorderLayout.NORTH);
        IPanel top = new IPanel(new Dimension(0, 60));
        northPanel.add(top, BorderLayout.CENTER);
        content.add(northPanel, BorderLayout.NORTH);

        JLabel l_path = new JLabel("文件夹");
        JTextField textField = new JTextField(15);
        JButton b_scan = new JButton("扫描");
        JButton b_search = new JButton("搜索");
        JButton b_all = new JButton("全部");
        JButton b_tag = new JButton("标签");
        JLabel b_success = new JLabel("扫描成功");
        JButton b_init = new JButton("初始化");
        JButton b_check = new JButton("查重");
        b_success.setVisible(false);
        top.add(l_path);
        top.add(textField);
        top.add(b_scan);
        top.add(b_search);
        top.add(b_all);
        top.add(b_tag);
        top.add(b_success);
        top.add(b_init);
        top.add(b_check);
        // 创建结果滚动面板
        IPanel center = new IPanel();
        this.center = center;
        // 内边距
        center.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(center);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);// 设置滚轮速度
        content.add(scrollPane, BorderLayout.CENTER);

        frame.setContentPane(content);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 背景透明化
        frame.setUndecorated(true);// 去掉窗口菜单
        frame.setOpacity(0.1f);
        frame.pack();
        frame.setSize(400, 500);
        frame.setIconImage(new ImageIcon("src\\gui\\icon\\home.png").getImage());
        frame.setLocationRelativeTo(null);// 居中显示
        frame.setVisible(true);

        // 以下为事件处理
        // 扫描按钮点击事件
        b_scan.addActionListener(e -> {
            // 空路径
            if (textField.getText().equals("")) {
                JOptionPane.showMessageDialog(frame, "输入为空，请重试！", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 获取路径，开始扫描
            String path = textField.getText();
            if (starter.scan(path)) {
                // 扫描成功
                center.removeAll();
                HashMap<String, Set<File>> fileMap = starter.getFileMap();
                // 显示结果
                fileMap.forEach((dir, files) -> {
                    center.add(new FileLabel(dir));
                    files.forEach(file -> center.add(new FileLabel(new IFile(file))));
                });
                b_success.setVisible(true);
                center.reload();
                content.reload();

            }
        });

        // 搜索文件
        b_search.addActionListener(e -> {
            String text = textField.getText();
            if (text.equals("")) {
                JOptionPane.showMessageDialog(frame, "输入为空，请重试！", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                search(text);
            }
        });

        // 全部按钮点击事件，展示数据库全部文件
        b_all.addActionListener(e -> queryAll());

        // 标签按钮点击事件，打开标签面板
        b_tag.addActionListener(e -> {
            AppContext.setKey(Tag.WIN_NAME, new Tag(frame));
        });

        // 文件写入数据库
        b_init.addActionListener(e1 -> {
            starter.init();
            b_success.setText("写入完成");
        });

        // 点击查重，展示重复文件
        b_check.addActionListener(e1 -> {
            Map<String, List<IFile>> repMap = fileService.getRepeatMap();
            HashMap<String, Set<ITag>> fileMap = tagService.getFilesTagsMap();

            // 输出大小相同的文件
            center.removeAll();
            repMap.forEach((size, list) -> {
                center.add(new JLabel("大小：" + FileUtils.getFileSizeString(size)));
                list.forEach(file -> {
                    // 文件行
                    center.addFileBox(file, center, fileMap);
                });
            });
            b_success.setText("查重完成");
            center.reload();
        });
    }

    private void search(String text) {
        List<IFile> files = fileService.search(text);
        if (files != null) {
            center.removeAll();
            files.forEach(file -> {
                // 文件行
                center.addFileBox(file, center);
            });
            center.reload();
        } else {
            center.removeAll();
            center.add(new JLabel("没有搜索到相关内容！"));
            center.reload();
        }

    }

    /**
     * 获取主窗口
     * 
     * @return
     */
    public JFrame getFrame() {
        return frame;
    }

    public void queryAll() {
        // 获取所有文件，按文件夹：文件的方式输出，带上文件的标签
        HashMap<String, Set<IFile>> files = fileService.getAllFiles();
        // 获取文件标签字典
        HashMap<String, Set<ITag>> fileMap = tagService.getFilesTagsMap();
        center.removeAll();
        files.forEach((dir, set) -> {
            // 目录行
            center.addFileBox(dir, center, fileMap);
            set.forEach(file -> {
                // 文件行
                center.addFileBox(file, center, fileMap);
            });
        });
        center.reload();
    }
}
