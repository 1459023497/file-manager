package gui.component;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JWindow;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import common.AppContext;
import gui.base.TagTreeNode;

public class TreeSelect extends JButton {
    private JButton button;
    private JWindow window;
    private JScrollPane scrollPane;
    private Object selectedItem;
    private JTree tree;

    public TreeSelect(JTree tree) {
        super("请选择");
        this.tree = tree;
        button = this;
        window = new JWindow();
        window.setSize(100, 200);

        initTree();
        // load tree on scrollpane
        scrollPane = new JScrollPane(tree);
        window.add(scrollPane);
        if (AppContext.UI_TRANSPARENT) {
            window.setOpacity(0.1f);
        }

        // AncestorListener can listen all the ancestors of this component
        addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {
                window.setVisible(false);
            }

            public void ancestorRemoved(AncestorEvent event) {
                window.setVisible(false);
            }

            public void ancestorMoved(AncestorEvent event) {
                // when window's ancestor changes(etc: moving frame), hide the window
                if (event.getSource() != window)
                    window.setVisible(false);
            }
        });

        addActionListener(new ActionListener() {
            // show tree
            @Override
            public void actionPerformed(ActionEvent e) {
                Point point = button.getLocationOnScreen();
                point.y = point.y + button.getHeight();
                
                window.setLocation(point);
                window.setVisible(true);
                window.toFront();
            }
        });
    }

    private void initTree() {
        tree.setShowsRootHandles(true);
        // hide node icons
        TreeCellRenderer renderer = new DefaultTreeCellRenderer() {
            public Component getTreeCellRendererComponent(JTree tree, Object value,
                    boolean sel, boolean expanded,
                    boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
                        hasFocus);
                setIcon(null);
                return this;
            }
        };
        tree.setCellRenderer(renderer);
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // click tree-node
                TagTreeNode node = (TagTreeNode) tree.getLastSelectedPathComponent();
                if (node != null) {
                    selectedItem = node.getItem();
                    button.setText(node.toString());
                    window.setVisible(false);
                }
            }
        });
    }

    public Object getSelectedItem() {
        return selectedItem;
    }

    /**
     * tree data update
     */
    public void reloadTree(JTree tree) {
        this.tree = tree;
        initTree();
        scrollPane.getViewport().setView(tree); // update viewport
        scrollPane.validate();
        scrollPane.repaint();
    }

}
