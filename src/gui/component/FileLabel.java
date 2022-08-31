package gui.component;

import service.FileService;
import tool.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

/**
 * 实现了鼠标监听的自定义文件标签
 */
public class FileLabel extends JLabel implements MouseListener {

    private FileService fileService;
    // 是否是目录
    private Boolean isDir;

    private String path;

    // 是否第一次提示路径不存在
    private boolean firstTap = true;

    // 文件夹
    public FileLabel(String text) {
        super(text);
        isDir = true;
        path = text;
        this.addMouseListener(this);
    }

    // 文件
    public FileLabel(File file) {
        super(file.getName() + " " + FileUtils.getFileSizeString(String.valueOf(file.length())));
        path = file.getPath();
        isDir = false;
        this.addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if ((e.getButton() == MouseEvent.BUTTON1) && isDir) {
            // 左键点击打开文件夹if
            fileService = new FileService();
            // 路径不存在于本地
            if (!fileService.openDir(this.getText()) && firstTap) {
                firstTap = false;
                this.setText(this.getText() + "     该路径不存在！");
            }
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
