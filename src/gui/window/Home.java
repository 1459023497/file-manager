package gui.window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import common.AppContext;
import common.myenum.InfoType;
import common.myenum.Status;
import common.tool.FileUtils;
import common.tool.StringUtils;
import entity.IFile;
import entity.IFolder;
import entity.ITag;
import gui.base.IDialog;
import gui.base.IFrame;
import gui.base.IPanel;
import gui.component.FileLabel;
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
    private String lastChoosePath;

    public Home() {
        AppContext.currentWin = frame;
        fileService = new FileService();
        tagService = new TagService();
        starter = new Starter();

        // menu buttons
        IPanel top = new IPanel(new Dimension(0, 60));
        JLabel l_path = new JLabel("请输入");
        JTextField textField = new JTextField(15);
        JButton b_scan = new JButton("扫描");
        JButton b_search = new JButton("搜索");
        JButton b_all = new JButton("全部");
        JButton b_tag = new JButton("标签");
        JButton b_init = new JButton("写入文件");
        JButton b_check = new JButton("查重");
        JButton b_auto = new JButton("自动归类");
        JButton b_bulid = new JButton("生成目录");
        JButton b_move = new JButton("移动");
        bottom = new IPanel();
        JButton b_confirm = new JButton("确认");
        JButton b_cancel = new JButton("取消");
        bottom.add(b_confirm);
        bottom.add(b_cancel);
        bottom.setVisible(false);
        top.add(l_path);
        top.add(textField);
        top.add(b_scan);
        top.add(b_search);
        top.add(b_all);
        top.add(b_tag);
        top.add(b_init);
        top.add(b_check);
        top.add(b_auto);
        top.add(b_bulid);
        top.add(b_move);

        frame = new IFrame("文件管理", top);
        center = frame.getCenter();
        frame.setRoot(bottom);

        // event listeners
        frame.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                AppContext.currentWin = Home.this;
                AppContext.currentFrame = frame;
            }
        });

        b_scan.addActionListener(e -> {
            // empty path
            if (textField.getText().equals("")) {
                new IDialog(frame, "输入为空，请重试！", InfoType.INFO);
                return;
            }

            String path = textField.getText();
            if (starter.scan(path)) {
                // success, show result
                center.removeAll();
                HashMap<String, Set<File>> fileMap = starter.getFileMap();
                fileMap.forEach((dir, files) -> {
                    center.add(new FileLabel(dir));
                    files.forEach(file -> center.add(new FileLabel(new IFile(file))));
                });
                new IDialog(frame, "扫描完成", InfoType.INFO);
                center.reload();
                //content.reload();
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

        // search for files
        b_search.addActionListener(e -> {
            String text = textField.getText();
            if (text.equals("")) {
                new IDialog(frame, "输入为空，请重试！", InfoType.INFO);
                return;
            } else {
                search(text);
            }
            bottom.setVisible(false);
        });

        // show all files
        b_all.addActionListener(e -> {
            queryAll();
            bottom.setVisible(false);
        });

        // open tag frame
        b_tag.addActionListener(e -> {
            AppContext.setKey(Tag.WIN_NAME, new Tag(frame));
            bottom.setVisible(false);
        });

        // file data persistence
        b_init.addActionListener(e1 -> {
            starter.init();
            new IDialog(frame, "写入完成", InfoType.INFO);
            bottom.setVisible(false);
        });

        // show repeated files
        b_check.addActionListener(e1 -> {
            Map<String, List<IFile>> repMap = fileService.getRepeatMap();
            // same size as repeat
            center.removeAll();
            repMap.forEach((size, list) -> {
                IPanel temp = new IPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel label = new JLabel("【大小：" + FileUtils.getFileSizeString(size) + "】");
                label.setOpaque(true);
                label.setBackground(Color.GREEN);
                temp.add(label);
                center.add(temp);
                list.forEach(file -> {
                    center.addFileBox(file, center);
                });
            });
            new IDialog(frame, "查重完成", InfoType.INFO);
            center.reload();
            bottom.setVisible(false);
        });

        // generate tag's diretory structure
        b_bulid.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setCurrentDirectory(new File("D:/Downloads/test"));

            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                lastChoosePath = file.getPath();
                IFolder topFolder = tagService.getTagFolder(lastChoosePath);
                topFolder.generate();
                new IDialog(frame, "已生成文件目录！", InfoType.INFO);
            }
        });

        // move files to their own folder
        b_move.addActionListener(e -> {
            if (lastChoosePath == null) {
                new IDialog(frame, "请先生成目录！", InfoType.ERROR);
                return;
            }
            IFolder topFolder = tagService.getTagFolder(lastChoosePath);
            List<String> result = new ArrayList<String>();
            topFolder.moveFiles(result);
            queryAll();
            if (result.isEmpty()) {
                new IDialog(frame, "全部移动完成！", InfoType.INFO);
            } else {
                new IDialog(frame, "以下文件移动失败：" + String.join(",", result), InfoType.WARN);
            }
        });
    }

    private void search(String text) {
        List<IFile> files = fileService.search(text);
        if (files != null) {
            center.removeAll();
            files.forEach(file -> {
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
     * get this frame
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
     * auto-tag for untagged files
     */
    public void autoTag() {
        List<ITag> tags = tagService.getAllTagsWithKeys();
        Map<String, List<ITag>> tagMap = new HashMap<>();
        tags.forEach(tag -> {
            List<String> keys = tag.getKeys();
            // names and keys as indexes
            tagMap.computeIfAbsent(tag.getName(), k -> new ArrayList<>()).add(tag);
            keys.forEach(key -> {
                tagMap.computeIfAbsent(key, k -> new ArrayList<>()).add(tag);
            });
        });
        files = fileService.getUntaggedFiles();
        // regular expression for matching files
        String[] keywords = tagMap.keySet().toArray(new String[0]);
        String patternString = "(" + String.join("|", keywords) + ")";
        Pattern pattern = Pattern.compile(patternString);
        for (IFile file : files) {
            String name = StringUtils.RemoveSuffix(file.getName());
            // Find index that may be included in the file name
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
