package gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import entity.IFile;
import service.FileService;

public class FileMenu extends JPopupMenu {
    /**
     * 文件右键弹出菜单
     * 
     * @param file      文件
     * @param fileLabel 文件标签
     * @param fileBox 该文件行
     */
    public FileMenu(IFile file, FileLabel fileLabel, FileBox fileBox) {
        //菜单选项
        JMenuItem rename = new JMenuItem("重命名");
        JMenuItem delete = new JMenuItem("删除");

        //菜单事件
        rename.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                // 弹出输入框，更新名字
                String input = JOptionPane.showInputDialog(fileLabel, "新命名", file.getName());
                if (input != null && !input.equals("")) {
                    file.setName(input);
                    FileService fileService = new FileService();
                    fileService.renameFile(file);
                    fileService.close();
                    // 刷新文件
                    fileLabel.setFile(file);
                }
            }
        });

        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 弹出确认框
                int confirm = JOptionPane.showConfirmDialog(fileLabel, "确认删除该文件吗？", "确认", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    FileService fileService = new FileService();
                    fileService.removeFile(file);
                    fileService.close();
                    // 移除文件
                    fileBox.setVisible(false);
                }
            }
        });
        this.add(rename);
        this.add(delete);
    }
}
