package gui.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        // set font size 
        setFont(new Font("Arial", Font.PLAIN, 13));
        // support html to change some characters color
        if (file.isDirectory()) {
            setText("<html>" + file.getPath() + "</html>");
            setForeground(Color.MAGENTA);// text color
        } else {
            setText("<html>" + file.getName() + "</html>");
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
     * @param key
     */
    public void setHighlight(String key) {
        String s = getText();
        //get the real text
        Pattern pattern = Pattern.compile("<html>(.*?)</html>");
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            String text = matcher.group(1);
            //only use file name, highlight the characters in the file name
            int lastDotIndex = text.lastIndexOf(".");
            String fileName = (lastDotIndex != -1) ? text.substring(0, lastDotIndex) : text;
            if (fileName.contains(key)){
                int startPos = s.indexOf(key);
                int endPos = startPos + key.length();
                // splice, highlight, concat
                if (startPos != -1) {
                    //StringBuffer result = new StringBuffer("<html>");
                    StringBuffer result = new StringBuffer();
                    result.append(s, 0, startPos);
                    result.append("<font color='red'>");
                    result.append(s, startPos, endPos);
                    result.append("</font>");
                    result.append(s.substring(endPos));
                    //result.append("</html>");
                    setText(result.toString());
                }
            }
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
