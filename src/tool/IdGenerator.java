package tool;

import java.text.SimpleDateFormat;

public class IdGenerator {
    /**
     * 根据当前时间戳生成id
     * @return
     */
    public String next(){
        SimpleDateFormat timestamp = new SimpleDateFormat("yyyyMMddHHmmss");
        return timestamp.format(System.currentTimeMillis());
    }

}
