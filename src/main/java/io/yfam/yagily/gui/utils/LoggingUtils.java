package io.yfam.yagily.gui.utils;

import java.io.OutputStream;

public final class LoggingUtils {
    public static final OutputStream DEV_NULL = new OutputStream() {
        public void write(int b) {
        }
    };
}
