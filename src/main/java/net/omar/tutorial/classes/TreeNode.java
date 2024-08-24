package net.omar.tutorial.classes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TreeNode {
    private String name;
    private TreeNode parent;
    private List<TreeNode> children = new ArrayList<>();

    public TreeNode(String name) {
        this.name = name;
    }

    public void addChild(TreeNode child) {
        child.parent = this;
        children.add(child);
    }

    public List<String> getPathFromRoot() {
        List<String> path = new ArrayList<>();
        for (TreeNode node = this; node != null; node = node.parent) {
            path.add(node.name);
        }

        List<String> reversedPath = new ArrayList<>();
        for (int i = path.size() - 1; i >= 0; i--) {
            reversedPath.add(path.get(i));
        }
        return reversedPath;
    }

    // Search for a node by name starting from the given node
    public TreeNode search(String name) {
        if (this.name.equals(name)) {
            return this;
        }
        for (TreeNode child : children) {
            TreeNode result = child.search(name);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

}