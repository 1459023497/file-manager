
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import common.AppContext;
import gui.window.Home;

public class Main {
    public static void main(String[] args) {
        //set window ui style
        String lookAndFeel ="com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
        try {
            UIManager.setLookAndFeel(lookAndFeel);
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        AppContext.init();
        Home home = new Home();
        //adding frame to context
        AppContext.setKey(Home.WIN_NAME, home);
    }
}