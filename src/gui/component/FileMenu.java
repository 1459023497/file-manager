package gui.component;

import com.alibaba.druid.util.StringUtils;
import common.AppContext;
import entity.IFile;
import gui.window.Home;
import service.FileService;

import javax.swing.*;

public class FileMenu extends JPopupMenu {
    /**
     * file menu
     *
     * @param file
     * @param fileLabel
     * @param fileBox
     */
    public FileMenu(IFile file, FileLabel fileLabel, FileBox fileBox) {
        FileService fileService = new FileService();
        JMenuItem rename = new JMenuItem("重命名");
        JMenuItem delete = new JMenuItem("删除");
        rename.addActionListener(e -> {
            // rename input
            String input = JOptionPane.showInputDialog(fileLabel, "新命名", file.getName());
            if (!StringUtils.isEmpty(input) && file.isExist()) {
                file.setName(input);
                fileService.renameFile(file);
                // refresh
                fileLabel.setFile(file);
            }
        });

        delete.addActionListener(e -> {
            String message = file.isDirectory() ? "确认删除该文件夹吗？" : "确认删除该文件吗？";
            int confirm = JOptionPane.showConfirmDialog(fileLabel, message, "确认", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                fileService.removeFile(file);
                // remove
                fileBox.setVisible(false);
                // refresh
                Home home = (Home) AppContext.getKey(Home.WIN_NAME);
                home.queryAll();
            }
        });
        if (file != null && !file.isDirectory()) {
            this.add(rename);
        }
        this.add(delete);
    }
}
