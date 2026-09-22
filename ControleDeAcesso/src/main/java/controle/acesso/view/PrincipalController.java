package controle.acesso.view;

import controle.acesso.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class PrincipalController {
    @FXML private BorderPane painelPrincipal;
    @FXML private Label lblBoasVindas;

    @FXML
    public void initialize() {
        lblBoasVindas.setText("Bem-vindo ao Controle de Acesso");
    }

    @FXML
    private void abrirCadastroUsuario() {
        abrirNoCentro("/controle/acesso/view/cadastroUsuario.fxml");
    }

    @FXML
    private void abrirListaUsuarios() {
        abrirNoCentro("/controle/acesso/view/listaUsuarios.fxml");
    }

    @FXML
    private void abrirHistorico() {
        abrirNoCentro("/controle/acesso/view/historico.fxml");
    }

    @FXML
    private void abrirRelatorioFrequencia() {
        abrirNoCentro("/controle/acesso/view/relatorioFrequencia.fxml");
    }

    @FXML
    private void abrirSobre() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sobre o projeto");
        alert.setHeaderText("Controle de Acesso");
        alert.setContentText(
                "Projeto acadêmico Java + JavaFX + FXML + JDBC + MariaDB.\n\n"
                + "Funções disponíveis:\n"
                + "• Cadastrar usuário\n"
                + "• Listar usuários\n"
                + "• Controle de status do usuário\n"
                + "• Histórico de acessos\n"
                + "• Relatório de frequência"
        );
        alert.showAndWait();
    }

    @FXML
    private void sair() {
        Stage stage = (Stage) painelPrincipal.getScene().getWindow();
        stage.close();
    }

    private void abrirNoCentro(String caminho) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(caminho));
            Node tela = loader.load();
            painelPrincipal.setCenter(tela);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Não foi possível abrir a tela.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
