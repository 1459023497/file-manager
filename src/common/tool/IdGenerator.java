package common.tool;

import java.text.SimpleDateFormat;

public class IdGenerator {

    public IdGenerator(){

    }

    /**
     * 根据当前时间戳(精确到毫秒)生成id
     * @return
     */
    public String next(){
        SimpleDateFormat timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        try {
            Thread.sleep(50);//批量操作生成id过快会出现相同id，强制间隔50毫秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return timestamp.format(System.currentTimeMillis());
    }

}
