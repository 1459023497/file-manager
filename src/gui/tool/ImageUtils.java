package gui.tool;

import java.awt.Image;

import javax.swing.ImageIcon;

public class ImageUtils {

    public static ImageIcon resizeImage(String src, int width, int height){
        ImageIcon icon = new ImageIcon(src);
        Image image = icon.getImage(); 
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH); // resize image
        return new ImageIcon(scaledImage); // new size ImageIcon
    }
    
}
