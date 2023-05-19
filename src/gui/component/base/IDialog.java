package gui.component.base;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.Timer;

import common.myenum.InfoType;
import common.tool.TagColor;

public class IDialog extends JWindow{
    public IDialog(IFrame frame, String info, int type){
        super();
        RoundPanel panel = new RoundPanel();
        setContentPane(panel);
        if(type == InfoType.INFO){
            panel.setBackground(TagColor.GREEN.getColor());
        }
        panel.setBorder(new RoundedBorder(Color.BLACK, 20));
        JLabel label = new JLabel(info);
        panel.add(label);

        setOpacity(0.1f);
        setBackground(new Color(0, 0, 0, 0));
        setFocusableWindowState(false);
        setSize(100, 20);

        //TODO: 设置位置
        setLocation(frame.getCenterPointOnScreen());
        //setLocationRelativeTo(frame);
        pack();
        setVisible(true);

        // dispose after 3 seconds
        Timer timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
}
