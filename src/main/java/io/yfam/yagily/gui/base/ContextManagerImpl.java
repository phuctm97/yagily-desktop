package io.yfam.yagily.gui.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextManagerImpl implements ContextManager {
    private final Map<String, Object> _contextMap;
    private final List<RunnableP2<Context, Class<?>>> _listenersList;

    public ContextManagerImpl() {
        _contextMap = new HashMap<>();
        _listenersList = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Context> T getContext(Class<T> clazz) {
        Object obj = _contextMap.getOrDefault(clazz.getCanonicalName(), null);
        if (obj == null) return null;
        return (T) obj;
    }

    @Override
    public <T extends Context> void putContext(T obj) {
        _contextMap.put(obj.getClass().getCanonicalName(), obj);
        notifyListeners(obj, obj.getClass());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public <T extends Context> void eraseContext(Class<T> clazz) {
        if (_contextMap.remove(clazz.getCanonicalName()) != null) {
            notifyListeners(null, clazz);
        }
    }

    @Override
    public void addListener(RunnableP2<Context, Class<?>> handler) {
        _listenersList.add(handler);
    }

    private void notifyListeners(Context context, Class<?> clazz) {
        for (RunnableP2<Context, Class<?>> listener : _listenersList) {
            listener.run(context, clazz);
        }
    }
}
