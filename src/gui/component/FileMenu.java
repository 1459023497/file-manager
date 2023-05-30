package gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import com.alibaba.druid.util.StringUtils;

import common.AppContext;
import entity.IFile;
import gui.window.Home;
import service.FileService;

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
        rename.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                // rename input
                String input = JOptionPane.showInputDialog(fileLabel, "新命名", file.getName());
                if (!StringUtils.isEmpty(input)) {
                    if(file.isExist()){
                        file.setName(input);
                        fileService.renameFile(file);
                        // refresh
                        fileLabel.setFile(file);   
                    }
                }
            }
        });

        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message  = file.isDirectory() ? "确认删除该文件夹吗？":"确认删除该文件吗？";
                int confirm = JOptionPane.showConfirmDialog(fileLabel, message, "确认", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    fileService.removeFile(file);
                    // remove
                    fileBox.setVisible(false);
                    // refresh
                    Home home = (Home) AppContext.getKey(Home.WIN_NAME);
                    home.queryAll();
                }
            }
        });
        if(file != null && !file.isDirectory()){
            this.add(rename);
        }
        this.add(delete);
    }
}
