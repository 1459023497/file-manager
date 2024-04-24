package common.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static String RemoveSuffix(String filename){
        String regex = "\\.[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(filename);
        return matcher.replaceAll("");
    }
}
