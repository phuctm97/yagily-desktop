package io.yfam.yagily.gui.utils;

import io.yfam.yagily.gui.base.ContextAware;
import io.yfam.yagily.gui.base.ContextManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;

@SuppressWarnings("WeakerAccess")
public final class GuiUtils {
    public static Parent loadFXML(String resourcePath) {
        try {
            return FXMLLoader.load(GuiUtils.class.getResource(resourcePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> ControllerAndNode<T> loadFXML(String resourcePath, Class<T> clazz) {
        FXMLLoader fxmlLoader = new FXMLLoader(GuiUtils.class.getResource(resourcePath));
        try {
            Parent node = fxmlLoader.load();
            T controller = fxmlLoader.getController();
            return new ControllerAndNode<>(controller, node);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends ContextAware> ControllerAndNode<T> loadFXML(String resourcePath, Class<T> clazz, ContextManager contextManager) {
        FXMLLoader fxmlLoader = new FXMLLoader(GuiUtils.class.getResource(resourcePath));

        try {
            T controller = clazz.newInstance();
            controller.setContextManager(contextManager);
            fxmlLoader.setController(controller);
            return new ControllerAndNode<>(controller, fxmlLoader.load());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Parent loadFXML(String resourcePath, Object controller) {
        try {
            FXMLLoader loader = new FXMLLoader(GuiUtils.class.getResource(resourcePath));
            loader.setController(controller);
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR, message);
        alert.showAndWait();
    }

    public static void showInfo(String message) {
        Alert alert = new Alert(AlertType.INFORMATION, message);
        alert.showAndWait();
    }
}
