package io.yfam.yagily.gui.base;

public abstract class ContextAwareBase implements ContextAware {
    private ContextManager _contextManager;

    @Override
    public ContextManager getContextManager() {
        return _contextManager;
    }

    @Override
    public void setContextManager(ContextManager contextManager) {
        _contextManager = contextManager;
    }

    protected <T extends Context> T getContext(Class<T> clazz) {
        if (_contextManager == null) return null;
        return _contextManager.getContext(clazz);
    }

    protected <T extends Context> void putContext(T obj) {
        if (_contextManager == null) throw new ContextManagerNotSetException();
        _contextManager.putContext(obj);
    }

    protected <T extends Context> void eraseContext(Class<T> clazz) {
        if (_contextManager == null) throw new ContextManagerNotSetException();
        _contextManager.eraseContext(clazz);
    }
}
