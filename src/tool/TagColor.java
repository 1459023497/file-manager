package tool;

import java.awt.*;

public enum TagColor {
    RED(new Color(250, 183, 183), 1),
    GREEN(new Color(141, 246, 148), 2),
    BLUE(new Color(186, 255, 235), 3),
    YELLOW(new Color(248, 232, 102), 4);

    private Color color;
    private int index;

    TagColor(Color color, int index) {
        this.color = color;
        this.index = index;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color next() {
        switch (index) {
            case 1:
                index++;
                return RED.getColor();
            case 2:
                index++;
                return GREEN.getColor();
            case 3:
                index++;
                return BLUE.getColor();
            case 4:
                index = 1;
                return YELLOW.getColor();
            default:
                return RED.getColor();
        }
    }
}
