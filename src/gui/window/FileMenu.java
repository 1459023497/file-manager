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
import gui.component.base.IPanel;
import service.FileService;

public class FileMenu {
    private FileService fileService;

    // File right-click menu
    public FileMenu(IFile file, FileLabel fileLabel) {
        fileService = new FileService();
        JWindow window = new JWindow();
        IPanel content = new IPanel(new Dimension(50, 70));
        content.setBorder(BorderFactory.createLineBorder(Color.black));
        window.setContentPane(content);
        window.pack();
        window.setSize(50, 70);
        window.setLocationRelativeTo(fileLabel);
        window.setVisible(true);
        //window.requestFocus();

        // menu
        JLabel rename = new JLabel("重命名");
        JLabel delete = new JLabel("删除");
        JLabel cancel = new JLabel("取消");
        content.add(rename);
        content.add(delete);
        content.add(cancel);

        rename.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                // rename input
                String input = JOptionPane.showInputDialog(rename, "新命名",file.getName());
                if (input != null && !input.equals("")) {
                    file.setName(input);
                    fileService.renameFile(file);
                    // refresh
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
