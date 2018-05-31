package io.yfam.yagily.gui.context;

import io.yfam.yagily.dto.Sprint;
import io.yfam.yagily.gui.base.Context;

public class ActiveSprintContext implements Context {
    private final Sprint _activeSprint;

    public ActiveSprintContext(Sprint activeSprint) {
        _activeSprint = activeSprint;
    }

    public Sprint getActiveSprint() {
        return _activeSprint;
    }
}
