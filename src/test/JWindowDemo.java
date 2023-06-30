package test;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JWindow;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public class JWindowDemo {
    private static JLabel text;
    private static JWindow window;
    public static void main(String[] args) {
        JFrame frame = new JFrame("窗口示例");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JButton button = new JButton("显示窗口");
        text = new JLabel("111");
        
        window = new JWindow();
        window.setSize(200, 200);
        window.add(new JLabel("Hello"));
        window.add(createTreeScrollPane());
        window.setLocationRelativeTo(button);
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.setVisible(true);
            }
        });
        
        frame.add(button, BorderLayout.CENTER);
        frame.add(text, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }

    private static JScrollPane createTreeScrollPane() {
        // 创建根节点
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("根节点");
        // 添加子节点
        root.add(new DefaultMutableTreeNode("选项1"));
        root.add(new DefaultMutableTreeNode("选项2"));
        root.add(new DefaultMutableTreeNode("选项3"));
        // 创建树
        JTree tree = new JTree(root);
        tree.setShowsRootHandles(true);
        // 隐藏节点图标
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
        // 监听鼠标点击
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (node != null) {
                    text.setText(node.toString());
                    window.setVisible(false);
                }
            }
        });
        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(tree);
        return scrollPane;
    }
}