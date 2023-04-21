package gui.window;

import java.awt.Dimension;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import common.AppContext;
import common.tool.FileUtils;
import entity.IFile;
import gui.component.FileLabel;
import gui.component.IFrame;
import gui.component.IPanel;
import service.FileService;
import service.Starter;

public class Home {
    public final static String WIN_NAME = "Home";
    private IFrame frame;
    private IPanel content;
    private IPanel center;
    private Starter starter;
    private FileService fileService;

    public Home() {
        fileService = new FileService();
        starter = new Starter();

        //功能面板
        IPanel top = new IPanel(new Dimension(0, 60));
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
        frame = new IFrame("文件管理", top);
        center = frame.getCenter();

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
            // 输出大小相同的文件
            center.removeAll();
            repMap.forEach((size, list) -> {
                center.add(new JLabel("大小：" + FileUtils.getFileSizeString(size)));
                list.forEach(file -> {
                    // 文件行
                    center.addFileBox(file, center);
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
                center.addFileBox(file, center, text);
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
        List<IFile> files = fileService.getAllFiles();
        frame.showContents(files);
    }
}
