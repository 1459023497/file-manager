package service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import common.AppContext;
public class Starter {
    public static final Logger logger = Logger.getLogger("Starter.class");
    // map<size，files>
    private HashMap<String, Set<File>> refileMap = new HashMap<>();
    // map<folder，files>
    private HashMap<String, Set<File>> fileMap = new HashMap<>();

    /**
     * only scan files in the specified directory
     * 
     * @param path
     */
    public boolean scan(String path) {
        File file = new File(path);
        File[] tempList = file.listFiles();
        //no files in the directory
        if(tempList == null) {
            JOptionPane.showMessageDialog(AppContext.getHome().getFrame(), "输入路径有误，请检查！", "提示", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (tempList.length == 0) {
            System.out.println("文件夹:" + path + " 对象个数: 0");
        } else {
            System.out.println("文件夹:" + path + " 对象个数：" + tempList.length);
            Set<File> fileList = new HashSet<>();
            for (File singleFile : tempList) {
                // folder, recursively
                if (singleFile.isDirectory()) {
                    scan(singleFile.getPath());
                }
                if (singleFile.isFile()) {
                    fileList.add(singleFile);
                    // record same size files
                    String fileSize = String.valueOf(singleFile.length());
                    if (refileMap.get(fileSize) == null) {
                        Set<File> refileList = new HashSet<>();
                        refileList.add(singleFile);
                        refileMap.put(fileSize, refileList);
                    } else {
                        refileMap.get(fileSize).add(singleFile);
                    }
                }
            }
            fileMap.put(path, fileList);
        }
        return true;
    }

    /**
     * show scan results
     */
    public void showFiles() {
        for (Map.Entry<String, Set<File>> entry : fileMap.entrySet()) {
            for (File file : entry.getValue()) {
                System.out.println(file + "  大小：" + file.length());
            }
        }
    }

    /**
     * show repeat files
     */
    public void showRepeat() {
        for (Set<File> list : refileMap.values()) {
            if (list.size() > 1) {
                System.out.println("重复的文件：" + list);
            }
        }
    }

    /**
     * write database
     */
    public void init() {
        FileService service = new FileService();
        for (Map.Entry<String, Set<File>> entry : fileMap.entrySet()) {
            logger.info("*****************开始写入***************");
            for (File file : entry.getValue()) {
                service.addFile(file, entry.getKey());
            }
            logger.info("*****************写入完成***************");
        }
    }

    /**
     * get scan files
     * @return map<folder，files>
     */
    public Map<String, Set<File>> getFileMap() {
        return fileMap;
    }

    /**
     * get repeat map<size，files>
     * @return
     */
    public Map<String,Set<File>> getRefileMap(){
        HashMap<String,Set<File>> result = new HashMap<>();
        refileMap.forEach((key, value) -> {
            if (value.size() > 1) {
                result.put(key, value);
            }
        });
        return result;
    }
}
