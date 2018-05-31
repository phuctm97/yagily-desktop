package io.yfam.yagily.gui.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class ConcurrentUtils {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(5);

    public static void launch(Runnable runnable) {
        EXECUTOR_SERVICE.execute(runnable);
    }

    public static void shutdownConcurrentService() throws InterruptedException {
        EXECUTOR_SERVICE.shutdown();
        if (!EXECUTOR_SERVICE.awaitTermination(1, TimeUnit.MINUTES)) {
            System.err.println("Force shutdown EXECUTOR SERVICE!");
            EXECUTOR_SERVICE.shutdownNow();
        }
    }
}
