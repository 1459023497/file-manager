
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import common.AppContext;
import gui.window.Home;

public class Main {
    public static void main(String[] args) {
        //切换window风格ui
        String lookAndFeel ="com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        //初始化程序上下文
        AppContext.init();
        Home home = new Home();
        //添加到上下文
        AppContext.setKey(Home.WIN_NAME, home);
    }
}