package gui.window;

import common.AppContext;
import common.model.event.FireEvent;
import common.model.event.impl.PagerEvent;
import common.model.observer.Observer;
import common.myenum.EventType;
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
import gui.component.FileBox;
import gui.component.FileLabel;
import gui.component.Pager;
import org.apache.commons.collections4.CollectionUtils;
import service.FileService;
import service.Starter;
import service.TagService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Home implements Observer {
    public static final String WIN_NAME = "Home";
    private final IFrame frame;
    private final IPanel center;
    private final IPanel bottom;
    private final Starter starter;
    private final FileService fileService;
    private final TagService tagService;
    private List<IFile> files;
    private String lastChoosePath;
    private final Pager pager;
    /**
     * 0: search, 1: check repeat
     */
    private int action = 0;

    public Home() {
        fileService = new FileService();
        tagService = new TagService();
        starter = new Starter();

        // menu buttons
        IPanel top = new IPanel(new Dimension(0, 60));
        JLabel lPath = new JLabel("请输入");
        JTextField textField = new JTextField(15);
        JButton bScan = new JButton("扫描");
        JButton bSearch = new JButton("搜索");
        JButton bAll = new JButton("全部");
        JButton bTag = new JButton("标签");
        JButton bInit = new JButton("写入文件");
        JButton bCheck = new JButton("查重");
        JButton bAuto = new JButton("自动归类");
        JButton bBuild = new JButton("生成目录");
        JButton bMove = new JButton("移动");
        bottom = new IPanel();
        JButton bConfirm = new JButton("确认");
        JButton bCancel = new JButton("取消");
        bottom.add(bConfirm);
        bottom.add(bCancel);
        bottom.setVisible(false);
        top.add(lPath);
        top.add(textField);
        top.add(bScan);
        top.add(bSearch);
        top.add(bAll);
        top.add(bTag);
        top.add(bInit);
        top.add(bCheck);
        top.add(bAuto);
        top.add(bBuild);
        top.add(bMove);

        //frame initialization
        frame = new IFrame("文件管理", top);
        center = frame.getCenter();
        pager = frame.getPager();
        int totalCount = fileService.totalCount();
        pager.setTotalCount(totalCount);
        pager.addObserver(this);

        frame.setRoot(bottom);
        AppContext.currentWin = frame;

        // event listeners
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                AppContext.currentWin = Home.this;
                AppContext.currentFrame = frame;
            }
        });

        bScan.addActionListener(e -> {
            // empty path
            if (textField.getText().equals("")) {
                new IDialog(frame, "输入为空，请重试！", InfoType.INFO);
                return;
            }

            String path = textField.getText();
            if (starter.scan(path)) {
                // success, show result
                center.removeAll();
                Map<String, Set<File>> fileMap = starter.getFileMap();
                fileMap.forEach((dir, fileList) -> {
                    center.add(new FileLabel(dir));
                    fileList.forEach(file -> center.add(new FileLabel(new IFile(file))));
                });
                new IDialog(frame, "扫描完成", InfoType.INFO);
                center.reload();
            }
            bottom.setVisible(false);
        });

        bAuto.addActionListener(e -> autoTag());

        bConfirm.addActionListener(e -> {
            tagService.tag(files);
            autoTag();
        });

        bCancel.addActionListener(e -> {
            queryAll();
            bottom.setVisible(false);
            pager.setVisible(true);
        });

        // search for files
        bSearch.addActionListener(e -> {
            action = 0;
            String text = textField.getText();
            if (text.equals("")) {
                new IDialog(frame, "输入为空，请重试！", InfoType.INFO);
                return;
            } else {
                search(text);
            }
            bottom.setVisible(false);
            pager.setVisible(false);
        });

        // show all files
        bAll.addActionListener(e -> {
            action = 0;
            queryAll();
            bottom.setVisible(false);
            pager.setVisible(true);
        });

        // open tag frame
        bTag.addActionListener(e -> {
            AppContext.setKey(Tag.WIN_NAME, new Tag(frame));
            bottom.setVisible(false);
        });

        // file data persistence
        bInit.addActionListener(e1 -> {
            starter.init();
            new IDialog(frame, "写入完成", InfoType.INFO);
            bottom.setVisible(false);
        });

        // show repeated files
        bCheck.addActionListener(e1 -> {
            action = 1;
            showRepeat();
        });

        // generate tag's directory structure
        bBuild.addActionListener(e -> {
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
        bMove.addActionListener(e -> {
            if (lastChoosePath == null) {
                new IDialog(frame, "请先生成目录！", InfoType.ERROR);
                return;
            }
            IFolder topFolder = tagService.getTagFolder(lastChoosePath);
            List<String> result = new ArrayList<>();
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
        List<IFile> fileList = fileService.search(text);
        center.removeAll();
        if (fileList != null) {
            fileList.forEach(file -> center.addFileBox(file, center, text));
        } else {
            center.add(new JLabel("没有搜索到相关内容！"));
        }
        center.reload();
    }

    /**
     * get this frame
     *
     */
    public JFrame getFrame() {
        return frame;
    }

    public void queryAll() {
        queryByPage(pager.getPageSize(), pager.getPageNum());
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
            keys.forEach(key -> tagMap.computeIfAbsent(key, k -> new ArrayList<>()).add(tag));
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
        pager.setVisible(false);
    }

    private void showRepeat() {
        List<JCheckBox> boxList = new ArrayList<>();
        List<IFile> fileList = new ArrayList<>();
        List<IFile> selectedFileList = new ArrayList<>();
        Map<String, List<IFile>> repMap = fileService.getRepeatMap(pager.getPageSize(), pager.getPageNum());
        // same size as repeat
        center.removeAll();
        //sort by size, descending
        List<String> keys = new ArrayList<>(repMap.keySet());
        Collections.reverse(keys);

        //batch delete menu
        JPanel menu = new JPanel();
        JButton bAll = new JButton("全选");
        JButton bDelete1 = new JButton("只保留最新的");
        JButton bDelete2 = new JButton("只保留最旧的");
        JButton bDelete3 = new JButton("删除已选");
        menu.add(bAll);
        menu.add(bDelete1);
        menu.add(bDelete2);
        menu.add(bDelete3);
        center.add(menu);

        AtomicBoolean isAll = new AtomicBoolean(true);
        bAll.addActionListener(actionEvent -> {
            // select all items
            if (isAll.get()){
                boxList.forEach(e-> e.setSelected(true));
                selectedFileList.addAll(fileList);
                isAll.set(false);
                bAll.setText("取消勾选");
            }else{
                boxList.forEach(e-> e.setSelected(false));
                selectedFileList.removeAll(new ArrayList<>(selectedFileList));
                isAll.set(true);
                bAll.setText("全选");
            }
        });

        bDelete1.addActionListener(actionEvent -> {
            List<IFile> delList = new ArrayList<>();
            keys.forEach(size -> {
                List<IFile> list = repMap.get(size);
                list.sort(Comparator.comparing(IFile::getModifiedTime));
                String newFileId = list.get(list.size() - 1).getId();
                List<IFile> itemList = list.stream().filter(e -> !e.getId().equals(newFileId)).collect(Collectors.toList());
                delList.addAll(itemList);
            });
            showDelete(delList);
        });

        bDelete2.addActionListener(actionEvent -> {
            List<IFile> delList = new ArrayList<>();
            keys.forEach(size -> {
                List<IFile> list = repMap.get(size);
                list.sort(Comparator.comparing(IFile::getModifiedTime));
                delList.add(list.get(list.size() - 1));
                String oldFileId = list.get(0).getId();
                List<IFile> itemList = list.stream().filter(e -> !e.getId().equals(oldFileId)).collect(Collectors.toList());
                delList.addAll(itemList);
            });
            showDelete(delList);
        });

        bDelete3.addActionListener(actionEvent -> {
            if (CollectionUtils.isNotEmpty(selectedFileList)) {
                showDelete(selectedFileList);
            }
        });

        keys.forEach(size -> {
            List<IFile> list = repMap.get(size);
            IPanel temp = new IPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel label = new JLabel("【大小：" + FileUtils.getFileSizeString(size) + "】");
            label.setOpaque(true);
            label.setBackground(Color.GREEN);
            temp.add(label);
            center.add(temp);
            list.forEach(file -> {
                fileList.add(file);
                IPanel fileRow = new IPanel(new FlowLayout(FlowLayout.LEFT));
                JCheckBox checkBox = new JCheckBox();
                checkBox.addActionListener(e -> {
                    if (checkBox.isSelected()) {
                        selectedFileList.add(file);
                    } else {
                        selectedFileList.remove(file);
                    }
                });
                boxList.add(checkBox);
                fileRow.add(checkBox);
                FileBox fileBox = new FileBox(file, center);
                fileRow.add(fileBox);
                center.add(fileRow);
            });
        });
        new IDialog(frame, "查重完成", InfoType.INFO);
        center.reload();
        bottom.setVisible(false);
        int total = fileService.repeatTotalCount();
        pager.setTotalCount(total);
    }

    private void showDelete(List<IFile> delList) {
        if (!delList.isEmpty()) {
            String names = delList.stream().map(IFile::getName).collect(Collectors.joining("，"));
            int choice = JOptionPane.showConfirmDialog(this.frame, "以下文件将会被删除：" + names, "确认窗口", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                fileService.removeFiles(delList);
                showRepeat();
            }
        }
    }

    /**
     * listen to the events of pager changes and execute something
     *
     */
    @Override
    public void update(FireEvent event) {
        if (event.getEventType() == EventType.PAGE_EVENT) {
            PagerEvent pagerEvent = (PagerEvent) event;
            //judge current action
            if (action == 0) {
                queryByPage(pagerEvent.getPageSize(), pagerEvent.getPageNum());
            } else if (action == 1) {
                showRepeat();
            }
        }
    }

    private void queryByPage(int pageSize, int pageNum) {
        List<IFile> result = fileService.getFilesByPage(pageSize, pageNum);
        frame.showContents(result);
        int total = fileService.totalCount();
        pager.setTotalCount(total);
    }
}
