package main;

import jdbc.JDBCConnector;
import service.FileService;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

//初始化
public class Starter {
    public static final Logger logger = Logger.getLogger("Starter.class");
    //查重文件的字典map<文件大小，set<文件>>
    private HashMap<String, Set<File>> refileMap = new HashMap<>();
    //文件字典 <文件夹，set<文件>>
    private HashMap<String,Set<File>> fileMap = new HashMap<>();

    /**
     * 文件扫描，不添加数据库
     * @param path
     */
    public void scan(String path){
        File file = new File(path);
        File[] tempList = file.listFiles();
        assert tempList != null;
        if (tempList.length == 0) {
            System.out.println("文件夹:" + path + " 对象个数：0");
        } else {
            System.out.println("文件夹:" + path + " 对象个数："+tempList.length);
            Set<File> fileList = new HashSet<>();
            for (File singleFile : tempList) {
                //是文件夹，向内递归
                if (singleFile.isDirectory()) {
                    scan(singleFile.getPath());
                }
                if (singleFile.isFile()) {
                    fileList.add(singleFile);
                    //记录大小相同的文件
                    String fileSize = String.valueOf(singleFile.length());
                    //System.out.println("文件：" + singleFile + ",大小：" + fileSize);
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
    }

    /**
     * 输出扫描的文件
     */
    public void showFiles(){
        for(Map.Entry<String, Set<File>> entry: fileMap.entrySet()){
            for(File file:entry.getValue()){
                System.out.println(file+"  大小："+file.length());
            }
        }
    }

    /**
     * 输出重复的文件
     */
    public void showRepeat() {
        for (Set<File> list : refileMap.values()) {
            if (list.size() > 1) {
                System.out.println("重复的文件：" + list);
            }
        }
    }

    /**
     *扫描的文件写入数据库
     */
    public void init() {
        //开启数据库链接
        JDBCConnector conn = new JDBCConnector();
        FileService service = new FileService(conn);
        for(Map.Entry<String,Set<File>> entry: fileMap.entrySet()){
            for(File file : entry.getValue() ){
                service.addFile(file,entry.getKey());
            }
        }
        //关闭数据库链接
        conn.close();
    }

    public HashMap<String, Set<File>> getFileMap(){
        return fileMap;
    }

    public HashMap<String,Set<File>> getRefileMap(){
        return refileMap;
    }
}
