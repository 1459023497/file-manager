package gui.window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JWindow;

import entity.IFile;
import gui.component.FileLabel;
import gui.component.IPanel;
import service.FileService;

public class FileMenu {
    private FileService fileService;

    // 文件右键的菜单
    public FileMenu(IFile file, FileLabel fileLabel) {
        // 窗口初始化
        fileService = new FileService();
        // 获取焦点
        //window.requestFocus();
        JWindow window = new JWindow();
        IPanel content = new IPanel(new Dimension(50, 70));
        content.setBorder(BorderFactory.createLineBorder(Color.black));
        window.setContentPane(content);
        window.pack();
        window.setSize(50, 70);
        window.setLocationRelativeTo(fileLabel);
        window.setVisible(true);
        //获取焦点
        //window.requestFocus();

        // 菜单选项
        JLabel rename = new JLabel("重命名");
        JLabel delete = new JLabel("删除");
        JLabel cancel = new JLabel("取消");
        content.add(rename);
        content.add(delete);
        content.add(cancel);

        // 重命名事件
        rename.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                // 弹出输入框，更新名字
                String input = JOptionPane.showInputDialog(rename, "新命名",file.getName());
                if (input != null && !input.equals("")) {
                    file.setName(input);
                    fileService.renameFile(file);
                    // 刷新文件
                    fileLabel.setFile(file);
                }
                window.dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                rename.setBorder(BorderFactory.createLineBorder(Color.red, 1, true));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                rename.setBorder(BorderFactory.createEmptyBorder());
            }

        });

        // 删除文件
        delete.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                // 弹出确认框
                int confirm = JOptionPane.showConfirmDialog(delete, "确认删除该文件吗？", "确认", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    fileService.removeFile(file);
                    // 移除文件
                    fileLabel.setVisible(false);
                }
                window.dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                delete.setBorder(BorderFactory.createLineBorder(Color.red, 1, true));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                delete.setBorder(BorderFactory.createEmptyBorder());
            }

        });

        cancel.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                window.dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                cancel.setBorder(BorderFactory.createLineBorder(Color.red, 1, true));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cancel.setBorder(BorderFactory.createEmptyBorder());
            }
        });
    }

}
