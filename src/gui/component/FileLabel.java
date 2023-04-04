package gui.component;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import entity.IFile;
import service.FileService;

/**
 * 实现了鼠标监听的自定义文件标签
 */
public class FileLabel extends JLabel implements MouseListener {

    private FileService fileService;
    // 是否是目录
    private Boolean isDir;

    private String path;

    private IFile file;
    // 是否第一次提示路径不存在
    private boolean firstTap = true;

    private FileBox fileBox;

    // 文件夹
    public FileLabel(String dir) {
        super("<--" + dir + "-->");
        setForeground(Color.MAGENTA);// 字体颜色
        isDir = true;
        path = dir;
        file = new IFile(dir);
        this.addMouseListener(this);
    }

    // 文件
    public FileLabel(IFile file) {
        super(file.getName());
        this.file = file;
        path = file.getPath();
        isDir = false;
        this.addMouseListener(this);
    }

    // 文件夹
    public FileLabel(String dir, FileBox fileBox) {
        super("<--" + dir + "-->");
        setForeground(Color.MAGENTA);// 字体颜色
        isDir = true;
        path = dir;
        file = new IFile(dir);
        this.fileBox = fileBox;
        this.addMouseListener(this);
    }

    // 文件
    public FileLabel(IFile file, FileBox fileBox) {
        super(file.getName());
        this.file = file;
        path = file.getPath();
        isDir = false;
        this.fileBox = fileBox;
        this.addMouseListener(this);
    }

    // 刷新文件
    public void setFile(IFile file) {
        this.file = file;
        this.setText(file.getName());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if ((e.getButton() == MouseEvent.BUTTON1) && isDir) {
            // 左键点击打开文件夹if
            fileService = new FileService();
            // 路径不存在于本地
            if (!fileService.openDir(path) && firstTap) {
                firstTap = false;
                this.setText(this.getText() + "     该路径不存在！");
            }
        }
        if ((e.getButton() == MouseEvent.BUTTON3) && fileBox != null) {
            // 文件右键菜单
            new FileMenu(file, this, fileBox).show(this, e.getX(), e.getY());
            ;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // 线条边框
        this.setBorder(BorderFactory.createLineBorder(Color.red, 1, true));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.setBorder(BorderFactory.createEmptyBorder());
    }
}
