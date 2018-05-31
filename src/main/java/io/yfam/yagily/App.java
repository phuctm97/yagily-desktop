package io.yfam.yagily;

import io.yfam.yagily.gui.base.ContextManager;
import io.yfam.yagily.gui.base.ContextManagerImpl;
import io.yfam.yagily.gui.screens.BeautyLoginScreen;
import io.yfam.yagily.gui.utils.ConcurrentUtils;
import io.yfam.yagily.gui.utils.ControllerAndNode;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.Objects;

import static io.yfam.yagily.gui.utils.GuiConstants.APP_NAME;
import static io.yfam.yagily.gui.utils.GuiUtils.loadFXML;
import static io.yfam.yagily.gui.utils.GuiUtils.showError;

public class App extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        // global exception handler
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
            showError(e.getMessage());
            LOGGER.error("Unhandled exception.", e);
        });

        // build context manager
        ContextManager appContextManager = new ContextManagerImpl();

        // load login screen and inject context
        ControllerAndNode<BeautyLoginScreen> controllerAndNode
                = loadFXML("/screens/beauty-login-screen.fxml", BeautyLoginScreen.class, appContextManager);

        // start application
        primaryStage.setTitle(APP_NAME);
        primaryStage.setScene(new Scene(controllerAndNode.getNode()));
        primaryStage.show();
        primaryStage.sizeToScene();
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());

        LOGGER.info("Application started.");
    }

    @Override
    public void stop() throws Exception {
        ConcurrentUtils.shutdownConcurrentService();

        LOGGER.info("Application shut down.");
    }

    public static void main(String[] args) throws ClassNotFoundException, URISyntaxException {
        // initial configurations
        configLogging();
        configDatabase();

        // start application
        launch(args);
    }

    private static void configLogging() throws URISyntaxException {
        System.setProperty("derby.stream.error.field", "io.yfam.yagily.gui.utils.LoggingUtils.DEV_NULL");

        org.apache.logging.log4j.core.LoggerContext loggerContext =
                (org.apache.logging.log4j.core.LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
        loggerContext.setConfigLocation(Objects.requireNonNull(
                App.class.getClassLoader().getResource("configs/log4j2.xml")).toURI());
    }

    private static void configDatabase() throws ClassNotFoundException {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    }
}
