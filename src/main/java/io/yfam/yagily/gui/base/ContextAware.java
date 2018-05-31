package io.yfam.yagily.gui.base;

public interface ContextAware {
    ContextManager getContextManager();

    void setContextManager(ContextManager contextManager);
}
