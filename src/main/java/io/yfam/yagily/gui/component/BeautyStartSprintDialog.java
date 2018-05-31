package io.yfam.yagily.gui.component;

import io.yfam.yagily.bus.SprintBus;
import io.yfam.yagily.dto.Sprint;
import io.yfam.yagily.gui.base.ContextAwareBase;
import io.yfam.yagily.gui.base.FutureDone;
import io.yfam.yagily.gui.context.MainWindowContext;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static io.yfam.yagily.gui.utils.ConcurrentUtils.launch;
import static io.yfam.yagily.gui.utils.GuiUtils.showError;

public class BeautyStartSprintDialog extends ContextAwareBase implements Initializable, FutureDone {
    @FXML
    private Label _codeText;

    @FXML
    private Label _titleLabel;

    @FXML
    private TextField _startDateText;

    @FXML
    private DatePicker _endDatePicker;

    @FXML
    private ComboBox<String> _durationSelect;

    @FXML
    private TextArea _descriptionText;

    @FXML
    private Button _startButton;

    private Runnable _doneHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _isStarted = false;
        _titleLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/SourceSansPro-SemiBold.ttf"), 28.0));

        LocalDate date = LocalDate.now();
        _startDateText.setText(String.format("Today %d-%d-%d", date.getDayOfMonth(), date.getMonth().getValue(), date.getYear()));

        _durationSelect.getItems().addAll(
                "1 week",
                "1.5 weeks",
                "2 weeks",
                "2.5 weeks",
                "3 weeks",
                "4 weeks"
        );
        _durationSelect.getSelectionModel().select(2);
        _endDatePicker.setValue(LocalDate.now().plusDays(14));
        _endDatePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate object) {
                return String.format("%d-%d-%d", object.getDayOfMonth(), object.getMonth().getValue(), object.getYear());
            }

            @Override
            public LocalDate fromString(String string) {
                return LocalDate.parse(string, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            }
        });
    }

    @FXML
    void onSelectDuration(ActionEvent event) {
        switch (_durationSelect.getSelectionModel().getSelectedIndex()) {
            case 0:
                _endDatePicker.setValue(LocalDate.now().plusDays(7));
                break;
            case 1:
                _endDatePicker.setValue(LocalDate.now().plusDays(10));
                break;
            case 2:
                _endDatePicker.setValue(LocalDate.now().plusDays(14));
                break;
            case 3:
                _endDatePicker.setValue(LocalDate.now().plusDays(17));
                break;
            case 4:
                _endDatePicker.setValue(LocalDate.now().plusDays(21));
                break;
            case 5:
                _endDatePicker.setValue(LocalDate.now().plusDays(28));
                break;
        }
    }

    @FXML
    void onClickClose(MouseEvent event) {
        closeSelf();
    }

    @FXML
    void onPressStart(ActionEvent event) {
        startSprint();
    }

    private final SprintBus _sprintBus = new SprintBus();

    private Sprint _sprint;

    public Sprint getSprint() {
        return _sprint;
    }

    public void setSprint(Sprint sprint) {
        _sprint = sprint;
        onSprintChange();
    }

    private void onSprintChange() {
        _codeText.setText(String.format("#%d", _sprint.getOrder()));
    }

    private boolean _isStarted;

    public boolean isStarted() {
        return _isStarted;
    }

    private void startSprint() {
        launch(new Task<Object>() {
            @Override
            protected Object call() throws Exception {
                _sprintBus.startSprint(_sprint, _endDatePicker.getValue(), _descriptionText.getText());
                return null;
            }

            @Override
            protected void succeeded() {
                _isStarted = true;
                closeSelf();
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());
                getException().printStackTrace();
            }
        });
    }

    private void closeSelf() {
        getContext(MainWindowContext.class).closeDialog();
        if (_doneHandler != null) _doneHandler.run();
    }

    @Override
    public void onDone(Runnable handler) {
        _doneHandler = handler;
    }
}
