package gui.base;

import java.awt.*;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import gui.component.Pager;
import org.apache.commons.collections4.CollectionUtils;

import common.AppContext;
import entity.IFile;
import entity.ITag;
import gui.component.FileBox;
import gui.component.FrameBar;

public class IFrame extends JFrame {
    private IPanel content;
    private IPanel center;
    private Pager pager;

    public IFrame() {
        super();
    }

    /**
     * diy frame: framebar + menu + content
     */
    public IFrame(String title, IPanel top) {
        super(title);
        // initialize
        content = new IPanel(new BorderLayout());
        IPanel northPanel = new IPanel(new BorderLayout());
        FrameBar frameBar = new FrameBar(this);
        northPanel.add(frameBar, BorderLayout.NORTH);
        northPanel.add(top, BorderLayout.CENTER);
        content.add(northPanel, BorderLayout.NORTH);

        IPanel temp = new IPanel(new BorderLayout());
        this.center = new IPanel();
        // content scrollpane
        center.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(center);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);// mouse' wheel speed
        // pager
        this.pager = new Pager(20);
        temp.add(scrollPane,BorderLayout.CENTER);
        temp.add(pager,BorderLayout.SOUTH);


        content.add(temp, BorderLayout.CENTER);


        setContentPane(content);
        // click close only close one window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // remove default menubar, set backgroud to transparent
        setUndecorated(true);
        if (AppContext.UI_TRANSPARENT) {
            setOpacity(0.1f);
        }
        pack();
        setSize(400, 500);
        setIconImage(new ImageIcon("src\\gui\\icon\\home.png").getImage());
        setLocationRelativeTo(null);// in the screen's center
        setVisible(true);
    }

    /**
     * show content
     */
    public void showContents(List<?> items) {
        center.removeAll();
        if (CollectionUtils.isEmpty(items)) {
            center.add(new JLabel("无结果"));
        } else {
            if (items.get(0) instanceof IFile) {
                // folder ==> file + tags
                Map<String, List<IFile>> map = items.stream().map(e -> (IFile) e)
                        .collect(Collectors.groupingBy(IFile::getBelong));
                map.forEach((dir, list) -> {
                    CollapsiblePanel collapsiblePanel = new CollapsiblePanel();
                    FileBox dirRow = new FileBox(new IFile(dir), center);
                    collapsiblePanel.addTitile(dirRow);
                    list.forEach(file -> {
                        FileBox fileRow = new FileBox(file, center);
                        collapsiblePanel.addCentent(fileRow);
                    });
                    center.add(collapsiblePanel);
                });
            } else if (items.get(0) instanceof ITag) {
                // tags and keys
                items.forEach(e -> {
                    ITag tag = (ITag) e;
                    center.addTagBox(tag, center);
                });
            }
        }
        center.reload();
    }

    public IPanel getContent() {
        return content;
    }

    public void setContent(IPanel content) {
        this.content = content;
    }

    public IPanel getCenter() {
        return center;
    }

    public void setCenter(IPanel center) {
        this.center = center;
    }

    // root bar
    public void setRoot(IPanel panel) {
        content.add(panel, BorderLayout.SOUTH);
        content.reload();
    }

    public Point getCenterPointOnScreen() {
        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;

        int frameX = getX();
        int frameY = getY();

        int screenCenterX = frameX + centerX;
        int screenCenterY = frameY + centerY;
        return new Point(screenCenterX, screenCenterY);
    }

    public Pager getPager() {
        return pager;
    }
}
