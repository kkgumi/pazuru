package pazuru.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import pazuru.Core;
import pazuru.exception.NoMatchRuleException;
import pazuru.exception.NoResourceException;
import pazuru.exception.UnexpectedResponseException;

import java.io.File;
import java.io.IOException;

public class Controller {
    public GridPane root;
    public Button startBtn;
    public ProgressBar progressBar;
    public TextField url;
    public TextField savePath;
    public TextField account;
    public PasswordField password;
    public TextField initPage;
    public TextField endPage;
    public Button pathSelectBtn;
    public Label errorLabel;
    public Label statusLabel;

    public void go(ActionEvent actionEvent) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Platform.runLater(() -> startBtn.setDisable(true));
                    Core core = new Core(url.getText(), account.getText(), password.getText(), savePath.getText());
                    int startPageNumber = 1;
                    int endPageNumber = -1;
                    if (!initPage.getText().equals("")) startPageNumber = Integer.parseInt(initPage.getText());
                    if (!endPage.getText().equals("")) endPageNumber = Integer.parseInt(endPage.getText());
                    Platform.runLater(() -> statusLabel.setText("登录中..."));
                    if (!core.authenticate()) Platform.runLater(() -> errorLabel.setText("登录失败，将匿名登录。"));
                    Platform.runLater(() -> statusLabel.setText("拉取中..."));
                    core.list();
                    Platform.runLater(() -> statusLabel.setText("保存中..."));
                    core.save(startPageNumber, endPageNumber, savePath.getText());
                } catch (NumberFormatException e) {
                    Platform.runLater(() -> errorLabel.setText("错误的页码"));
                } catch (NoMatchRuleException e) {
                    Platform.runLater(() -> errorLabel.setText("无法识别的URL"));
                } catch (IOException | UnexpectedResponseException e) {
                    Platform.runLater(() -> errorLabel.setText("网络/本地读写错误"));
                    e.printStackTrace();
                } catch (NoResourceException e) {
                    Platform.runLater(() -> errorLabel.setText("无资源；请检查登录口令"));
                    e.printStackTrace();
                } finally {
                    Platform.runLater(() -> statusLabel.setText(""));
                    startBtn.setDisable(false);
                }
            }
        };
        thread.setName("go");
        thread.start();
    }

    public void selectPath(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("存储路径");
        File selectedDirectory = directoryChooser.showDialog(root.getScene().getWindow());
        savePath.setText(selectedDirectory.getAbsolutePath());
    }
}
