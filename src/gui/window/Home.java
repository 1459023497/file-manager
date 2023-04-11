package gui.window;

import java.awt.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import entity.IFile;
import entity.ITag;
import gui.component.FileLabel;
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

    private Boolean firstClick = true;

    public Home() {
        // 窗口，面版初始化
        frame = new JFrame("文件管理");
        content = new IPanel(new BorderLayout());
        starter = new Starter();

        IPanel top = new IPanel(new Dimension(0, 80));
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
        content.add(top, BorderLayout.NORTH);
        // 创建结果滚动面板
        IPanel center = new IPanel();
        this.center = center;
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(center);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);// 设置滚轮速度
        content.add(scrollPane, BorderLayout.CENTER);

        frame.setContentPane(content);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // TODO: 背景透明化
        frame.setUndecorated(true);// 去掉窗口菜单
        frame.setOpacity(0.1f);
        frame.pack();
        frame.setSize(400, 500);
        frame.setIconImage(new ImageIcon("src\\gui\\icon\\home.png").getImage());
        frame.setLocationRelativeTo(null);// 居中显示
        frame.setVisible(true);

        // 以下为事件处理
        // 添加鼠标监听器，支持拖动窗口
        MouseAdapter ma = new MouseAdapter(){
            private int x;
            private int y;

            /*
             * 记录鼠标按下时的坐标
             */
            @Override
            public void mousePressed(MouseEvent e) {
                x = e.getX();
                y = e.getY();
            }
            
            /*
             * 鼠标移进标题栏时，设置鼠标图标为移动图标
             */
            @Override
            public void mouseEntered(MouseEvent e) {
                frame.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }

            /*
             * 鼠标移出标题栏时，设置鼠标图标为默认指针
             */
            @Override
            public void mouseExited(MouseEvent e) {
                frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // 计算窗口移动后的位置
                int left = frame.getLocation().x + e.getX() - x;
                int top = frame.getLocation().y + e.getY() - y;
                // 移动窗口
                frame.setLocation(left, top);
            }
        };
        frame.addMouseListener(ma);
        // 接收鼠标拖动事件
        frame.addMouseMotionListener(ma);

        // 扫描按钮点击事件
        b_scan.addActionListener(e -> {
            // 空路径
            if (textField.getText().equals("")) {
                JOptionPane.showMessageDialog(frame, "输入为空，请重试！", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 获取路径，开始扫描
            String path = textField.getText();
            if(starter.scan(path)){
                //扫描成功
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
        b_search.addActionListener(e-> {
            String text = textField.getText();
            if (text.equals("")) {
                JOptionPane.showMessageDialog(frame, "输入为空，请重试！", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }else{
                search(text);
            }
        });

        // 全部按钮点击事件，展示数据库全部文件
        b_all.addActionListener(e -> queryAll());

        // 标签按钮点击事件，打开标签面板
        b_tag.addActionListener(e -> new Tag(frame));

        // 文件写入数据库
        b_init.addActionListener(e1 -> {
            starter.init();
            b_success.setText("写入完成");
        });

        // 点击查重，展示重复文件
        b_check.addActionListener(e1 -> {
            FileService service = new FileService();
            Map<String, List<IFile>> repMap = service.getRepeatMap();
            service.close();
            TagService tagService = new TagService();
            HashMap<String, Set<ITag>> fileMap = tagService.getFilesTagsMap();
            tagService.close();

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
        
    }

    /**
     * 获取主窗口
     * @return
     */
    public JFrame getFrame(){
        return frame;
    }

    public void queryAll() {
        // 获取所有文件，按文件夹：文件的方式输出，带上文件的标签
        FileService fileService = new FileService();
        HashMap<String, Set<IFile>> files = fileService.getAllFiles();
        fileService.close();
        // 获取文件标签字典
        TagService tagService = new TagService();
        HashMap<String, Set<ITag>> fileMap = tagService.getFilesTagsMap();
        tagService.close();
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
