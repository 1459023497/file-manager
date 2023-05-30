package gui.component.base;

import java.awt.*;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class MyScrollBarUI extends BasicScrollBarUI{
    private int direction;

    /**
     * 
     * @param direction vertical : 0, horizontal : 1
     */
    public MyScrollBarUI(int direction){
        this.direction = direction;
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        JScrollBar sb = (JScrollBar)c;
        sb.setOpaque(false);  // set transparent
    }
    
    @Override
    public void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(Color.gray);  
        g.fillRect(trackBounds.x, trackBounds.y, 
                   trackBounds.width, trackBounds.height);
    }
    
    @Override
    public void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {  
        g.setColor(Color.lightGray);
        g.fillRect(thumbBounds.x, thumbBounds.y, 
                   thumbBounds.width, thumbBounds.height);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        // customized vertical and horizontal size
        if(direction == 0){
            return new Dimension(10, 200); 
        }else if(direction == 1){
            return new Dimension(200, 10);
        }
        return new Dimension(10, 150); 
    }

    @Override
    protected void configureScrollBarColors() {
        this.thumbColor = new Color(0, 0, 0);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton jbutton = new JButton();
        jbutton.setPreferredSize(new Dimension(0, 0));
        jbutton.setMinimumSize(new Dimension(0, 0));
        jbutton.setMaximumSize(new Dimension(0, 0));
        return jbutton;
    }

    
}
