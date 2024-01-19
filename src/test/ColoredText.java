package test;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import gui.component.SpaceBox;

public class ColoredText {

  public static void main(String[] args) {
    JFrame frame = new JFrame("Colored Text Demo");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(400, 200);

    JTextPane textPane = new JTextPane();
    textPane.setBorder(BorderFactory.createLineBorder(Color.red));
    textPane.setContentType("text/html");
    textPane.setText("Here is some text with <b>bold</b> and <font color='red'>red</font> text.");

    SimpleAttributeSet keyWord = new SimpleAttributeSet();
    StyleConstants.setForeground(keyWord, Color.blue);
    StyleConstants.setBold(keyWord, true);

    textPane.getStyledDocument().setCharacterAttributes(10, 4, keyWord, false);

    SpaceBox box = new SpaceBox();
    frame.add(box);

    //frame.add(textPane);
    frame.setVisible(true);
  }
}