package com.alexcorp.springspirit.utils;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

public class Tree<T> {

    @Getter
    private T data;
    @Getter
    private Tree<T> parent;
    @Getter
    private List<Tree<T>> children = new LinkedList<>();

    public Tree(T data) {
        this.data = data;
    }

    public Tree<T> addChild(T child) {
        Tree<T> childNode = new Tree<>(child);
        childNode.parent = this;
        children.add(childNode);

        return childNode;
    }

    public Tree<T> addChild(Tree node) {
        if(node.parent != null) {
            parent.getChildren().remove(node);
        }

        node.parent = this;
        children.add(node);

        return node;
    }

}
