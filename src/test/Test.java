package test;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) throws IOException, SQLException {

//        JDBCConnector connector = new JDBCConnector();
//        FileService s = new FileService();
//        s.openDir("D:\\Downloads");
//        connector.close();
        File file = new File("D:\\Downloads\\苹果.txt");
        System.out.println(file.getName());

        String input = "This is a sample text containing some keywords like Java, Python and C++.";
        String[] keywords = {"Java", "Python", "C++"};

        // Build the regular expression pattern
        String patternString = "\\b(" + String.join("|", keywords) + ")\\b";
        Pattern pattern = Pattern.compile(patternString);

        // Match the pattern against the input string
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            System.out.println("Matched keyword: " + matcher.group());
        }
    }
}