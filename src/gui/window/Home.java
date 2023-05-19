package gui.window;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import common.AppContext;
import common.myenum.InfoType;
import common.myenum.Status;
import common.tool.FileUtils;
import entity.IFile;
import entity.ITag;
import gui.component.FileLabel;
import gui.component.base.IDialog;
import gui.component.base.IFrame;
import gui.component.base.IPanel;
import service.FileService;
import service.Starter;
import service.TagService;

public class Home {
    public final static String WIN_NAME = "Home";
    private IFrame frame;
    private IPanel content;
    private IPanel center;
    private IPanel bottom;
    private Starter starter;
    private FileService fileService;
    private TagService tagService;
    private List<IFile> files;

    public Home() {
        fileService = new FileService();
        tagService = new TagService();
        starter = new Starter();

        // 功能面板
        IPanel top = new IPanel(new Dimension(0, 60));
        JLabel l_path = new JLabel("文件夹");
        JTextField textField = new JTextField(15);
        JButton b_scan = new JButton("扫描");
        JButton b_search = new JButton("搜索");
        JButton b_all = new JButton("全部");
        JButton b_tag = new JButton("标签");
        JLabel l_success = new JLabel("扫描成功");
        JButton b_init = new JButton("初始化");
        JButton b_check = new JButton("查重");
        JButton b_auto = new JButton("自动归类");
        JButton b_bulid = new JButton("生成目录");
        bottom = new IPanel();
        JButton b_confirm = new JButton("确认");
        JButton b_cancel = new JButton("取消");
        bottom.add(b_confirm);
        bottom.add(b_cancel);
        bottom.setVisible(false);
        l_success.setVisible(false);
        top.add(l_path);
        top.add(textField);
        top.add(b_scan);
        top.add(b_search);
        top.add(b_all);
        top.add(b_tag);
        top.add(l_success);
        top.add(b_init);
        top.add(b_check);
        top.add(b_auto);
        top.add(b_bulid);
        frame = new IFrame("文件管理", top);
        center = frame.getCenter();
        frame.setBottom(bottom);

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
                l_success.setVisible(true);
                center.reload();
                content.reload();
            }
            bottom.setVisible(false);
        });

        b_auto.addActionListener(e -> {
            autoTag();
        });

        b_confirm.addActionListener(e -> {
            tagService.tag(files);
            queryAll();
            bottom.setVisible(false);
        });

        b_cancel.addActionListener(e -> {
            queryAll();
            bottom.setVisible(false);
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
            bottom.setVisible(false);
        });

        // 全部按钮点击事件，展示数据库全部文件
        b_all.addActionListener(e -> {
            queryAll();
            bottom.setVisible(false);
        });

        // 标签按钮点击事件，打开标签面板
        b_tag.addActionListener(e -> {
            AppContext.setKey(Tag.WIN_NAME, new Tag(frame));
            bottom.setVisible(false);
        });

        // 文件写入数据库
        b_init.addActionListener(e1 -> {
            starter.init();
            l_success.setText("写入完成");
            bottom.setVisible(false);
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
            l_success.setText("查重完成");
            center.reload();
            bottom.setVisible(false);
        });

        // 根据标签结构在指定位置生成目录
        b_bulid.addActionListener(e -> {
            // JFileChooser fileChooser = new JFileChooser();
            // fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            // fileChooser.setCurrentDirectory(new File("D:/"));
            
            // int result = fileChooser.showOpenDialog(frame);
            // if (result == JFileChooser.APPROVE_OPTION) {
            //     File file = fileChooser.getSelectedFile();
            //     String path = file.getPath();
            //     IFolder topFolder = tagService.getTagFolder(path);
            //     topFolder.generate();
            // }
            new IDialog(frame, "已生成文件目录！", InfoType.INFO);
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
        bottom.setVisible(false);

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

    /**
     * 给没有标签的文件自动贴标签
     */
    public void autoTag() {
        List<ITag> tags = tagService.getAllTagsWithKeys();
        Map<String, List<ITag>> tagMap = new HashMap<>();
        tags.forEach(tag -> {
            List<String> keys = tag.getKeys();
            // 将名字和关键词作为索引
            tagMap.computeIfAbsent(tag.getName(), k -> new ArrayList<>()).add(tag);
            keys.forEach(key -> {
                tagMap.computeIfAbsent(key, k -> new ArrayList<>()).add(tag);
            });
        });
        files = fileService.getUntaggedFiles();
        // 正则匹配
        String[] keywords = tagMap.keySet().toArray(new String[0]);
        String patternString = "^.+(" + String.join("|", keywords) + ")\\b";
        Pattern pattern = Pattern.compile(patternString);
        for (IFile file : files) {
            String name = file.getName();
            // 找到文件名中可能包含的标签
            Matcher matcher = pattern.matcher(name);
            while (matcher.find()) {
                // 直接group()会输出哈巴狗，在group()方法外再调用一次group(1)获取第一个捕获组的内容：哈巴狗->狗。
                String tagName = matcher.group(1);
                List<ITag> matchTags = tagMap.get(tagName);
                if (matchTags != null) {
                    matchTags.forEach(tag -> {
                        tag.setStatus(Status.INSERT);
                        file.add(tag);
                    });
                }
            }
        }
        frame.showContents(files);
        bottom.setVisible(true);
    }
}
