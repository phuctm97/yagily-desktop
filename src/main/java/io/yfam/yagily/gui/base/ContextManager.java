package io.yfam.yagily.gui.base;

public interface ContextManager {
    <T extends Context> T getContext(Class<T> clazz);

    <T extends Context> void putContext(T obj);

    <T extends Context> void eraseContext(Class<T> clazz);

    void addListener(RunnableP2<Context, Class<?>> handler);
}
