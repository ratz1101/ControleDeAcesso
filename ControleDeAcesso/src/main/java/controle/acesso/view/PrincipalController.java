package controle.acesso.view;

import controle.acesso.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class PrincipalController {
    @FXML private BorderPane painelPrincipal;

    @FXML
    public void initialize() {
        abrirDashboard();
    }

    @FXML
    void abrirDashboard() {
        abrirNoCentro("/controle/acesso/view/dashboard.fxml");
    }

    @FXML
    void abrirCadastroUsuario() {
        abrirNoCentro("/controle/acesso/view/cadastroUsuario.fxml");
    }

    @FXML
    void abrirListaUsuarios() {
        abrirNoCentro("/controle/acesso/view/listaUsuarios.fxml");
    }

    @FXML
    void abrirHistorico() {
        abrirNoCentro("/controle/acesso/view/historico.fxml");
    }

    @FXML
    void abrirRelatorioFrequencia() {
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
                + "• Dashboard com indicadores\n"
                + "• Cadastrar, editar e excluir usuário\n"
                + "• Listar usuários\n"
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

    private Node abrirNoCentro(String caminho) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(caminho));
            Node tela = loader.load();

            Object controller = loader.getController();
            if (controller instanceof DashboardController dashboardController) {
                dashboardController.setPrincipalController(this);
            }

            painelPrincipal.setCenter(tela);
            return tela;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Não foi possível abrir a tela.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return null;
        }
    }
}
