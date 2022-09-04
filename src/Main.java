
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import gui.Home;

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
        new Home();
    }
}