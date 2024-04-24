package gui.base;

import javax.swing.tree.DefaultMutableTreeNode;

public class TagTreeNode extends DefaultMutableTreeNode {
    private Object item;
    
    public TagTreeNode(Object item) {
        this.item = item;
    }
    
    @Override
    public String toString() {
        return item.toString();
    }

    public Object getItem() {
        return item;
    }

    public void setItem(Object item) {
        this.item = item;
    }
}
