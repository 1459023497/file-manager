package tool;

import java.text.SimpleDateFormat;

public class IdGenerator {
    /**
     * 根据当前时间戳(精确到毫秒)生成id
     * @return
     */
    public String next(){
        SimpleDateFormat timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return timestamp.format(System.currentTimeMillis());
    }

}
