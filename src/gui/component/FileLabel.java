package gui.component;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import entity.IFile;

/**
 * 实现了鼠标监听的自定义文件标签
 */
public class FileLabel extends JLabel implements MouseListener {
    private IFile file;
    // first show the tip for no existed file
    private boolean firstTap = true;

    private FileBox fileBox;

    // folder
    public FileLabel(String dir) {
        super("<--" + dir + "-->");
        setForeground(Color.MAGENTA);
        file = new IFile(dir);
        this.addMouseListener(this);
    }

    // file
    public FileLabel(IFile file) {
        super(file.getName());
        this.file = file;
        this.addMouseListener(this);
    }

    // folder
    public FileLabel(String dir, FileBox fileBox) {
        super("<--" + dir + "-->");
        setForeground(Color.MAGENTA);
        file = new IFile(dir);
        this.fileBox = fileBox;
        this.addMouseListener(this);
    }

    // file and folder
    public FileLabel(IFile file, FileBox fileBox) {
        super();
        if(file.isDirectory()){
            setText("<--" + file.getPath() + "-->");
            setForeground(Color.MAGENTA);// text color
        }else{
            setText(file.getName());
        }
        this.file = file;
        this.fileBox = fileBox;
        this.addMouseListener(this);
    }

    // reset
    public void setFile(IFile file) {
        this.file = file;
        this.setText(file.getName());
    }

    /**
     * highlighting the search characters 
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
            if (!file.isExist() && firstTap) {
                firstTap = false;
                this.setText(this.getText() + "  该路径不存在！");
            } else {
                file.open();
            }
        }
        if ((e.getButton() == MouseEvent.BUTTON3) && fileBox != null) {
            // click right button to show file menu
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
        this.setBorder(BorderFactory.createLineBorder(Color.red, 1, true));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.setBorder(BorderFactory.createEmptyBorder());
    }
}
