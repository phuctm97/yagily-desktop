package io.yfam.yagily.gui.utils;

import javafx.scene.Parent;

public class ControllerAndNode<T> {
    private final T _controller;
    private final Parent _node;

    public ControllerAndNode(T controller, Parent node) {
        _controller = controller;
        _node = node;
    }

    public T getController() {
        return _controller;
    }

    public Parent getNode() {
        return _node;
    }
}
