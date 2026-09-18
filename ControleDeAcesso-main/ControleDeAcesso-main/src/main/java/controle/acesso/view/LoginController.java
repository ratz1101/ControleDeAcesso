package controle.acesso.view;

import controle.acesso.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField txtLogin;
    @FXML private PasswordField pwdSenha;

    @FXML
    private void btnLoginAction(ActionEvent event) {
        if ("admin".equals(txtLogin.getText().trim()) && "admin".equals(pwdSenha.getText())) {
            try {
                Stage stage = (Stage) txtLogin.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("/controle/acesso/view/principal.fxml"));
                Scene scene = new Scene(loader.load());
                scene.getStylesheets().add(Main.class.getResource("/controle/acesso/css/estilo.css").toExternalForm());
                stage.setTitle("Controle de Acesso - Principal");
                stage.setScene(scene);
                stage.setResizable(true);
                stage.setWidth(900);
                stage.setHeight(600);
                stage.centerOnScreen();
            } catch (Exception e) {
                mostrarErro("Não foi possível abrir a tela principal.", e);
            }
        } else {
            mostrarErro("Login ou senha inválidos.\nUse admin / admin para o teste.", null);
        }
    }

    private void mostrarErro(String mensagem, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem + (e == null ? "" : "\n" + e.getMessage()));
        alert.showAndWait();
    }
}
