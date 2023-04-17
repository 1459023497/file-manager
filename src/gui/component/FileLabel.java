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

    private String path;

    private IFile file;
    // 是否第一次提示路径不存在
    private boolean firstTap = true;

    private FileBox fileBox;

    // 文件夹
    public FileLabel(String dir) {
        super("<--" + dir + "-->");
        setForeground(Color.MAGENTA);// 字体颜色
        path = dir;
        file = new IFile(dir);
        this.addMouseListener(this);
    }

    // 文件
    public FileLabel(IFile file) {
        super(file.getName());
        this.file = file;
        path = file.getPath();
        this.addMouseListener(this);
    }

    // 文件夹
    public FileLabel(String dir, FileBox fileBox) {
        super("<--" + dir + "-->");
        setForeground(Color.MAGENTA);// 字体颜色
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
        this.fileBox = fileBox;
        this.addMouseListener(this);
    }

    // 刷新文件
    public void setFile(IFile file) {
        this.file = file;
        this.setText(file.getName());
    }

    /**
     * 设置高亮搜索字符
     * 
     * @param text
     */
    public void setHighlight(String text) {
        String s = getText();
        int start_pos = s.indexOf(text);
        int end_pos = start_pos + text.length();
        // 切分高亮再拼接
        if (start_pos != -1) {
            StringBuffer result = new StringBuffer("<html>");
            result.append(s.substring(0, start_pos));
            result.append("<font color='red'>");
            result.append(s.substring(start_pos, end_pos));
            result.append("</font>");
            result.append(s.substring(end_pos));
            result.append("</html>");
            setText(result.toString());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if ((e.getButton() == MouseEvent.BUTTON1)) {
            // 左键点击打开文件
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
