package com.alexcorp.springspirit.core;

import com.alexcorp.springspirit.utils.Tree;

import java.util.HashMap;
import java.util.Map;

public class StateMachine {

    private final Map<Class<?>, Tree<Class<?>>> statesInTree = new HashMap<>();
    private final Tree<Class<?>> stateTree = new Tree<>(null);

    public Tree<Class<?>> loadTreeNode(Class<?> state) {
        Tree<Class<?>> node = statesInTree.get(state);
        if(node == null) {
            node = new Tree<>(state);
            statesInTree.put(state, node);
            stateTree.addChild(node);

            Class<?>[] parentStates = state.getInterfaces();
            for (Class<?> parentState : parentStates) {
                Tree<Class<?>> parentNode = loadTreeNode(parentState);
                parentNode.addChild(node);
            }

        }

        return node;
    }

    public boolean isAvailableState(Class<?> state, Class<?>[] states) {
        if (state == null && (states == null || states.length == 0)) {
            return true;
        }

        if (state != null && states != null) {
            for (Class<?> allowedState : states) {
                Tree<Class<?>> stateNode = statesInTree.get(allowedState);

                while (stateNode.getData() != null) {
                    if(stateNode.getData().equals(state)) {
                        return true;
                    }

                    stateNode = stateNode.getParent();
                }
            }
        }

        return false;
    }

}
