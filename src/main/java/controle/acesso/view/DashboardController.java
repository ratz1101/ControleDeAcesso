package controle.acesso.view;

import controle.acesso.dao.LogAcessoDAO;
import controle.acesso.dao.UsuarioDAO;
import controle.acesso.model.LogAcesso;
import controle.acesso.model.ResultadoAcessoEnum;
import controle.acesso.model.StatusEnum;
import controle.acesso.model.Usuario;
import controle.acesso.view.util.DateUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.util.List;

public class DashboardController {

    @FXML private Label lblAtivos;
    @FXML private Label lblBloqueados;
    @FXML private Label lblAcessosHoje;
    @FXML private Label lblNegadosHoje;

    @FXML private TableView<LogAcesso> tblUltimos;
    @FXML private TableColumn<LogAcesso, String> colUsuario;
    @FXML private TableColumn<LogAcesso, LocalDateTime> colData;
    @FXML private TableColumn<LogAcesso, String> colTipo;
    @FXML private TableColumn<LogAcesso, String> colResultado;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final LogAcessoDAO logAcessoDAO = new LogAcessoDAO();

    private PrincipalController principalController;

    public void setPrincipalController(PrincipalController principalController) {
        this.principalController = principalController;
    }

    @FXML
    public void initialize() {
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("nomeUsuario"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoAcesso"));

        colData.setCellValueFactory(new PropertyValueFactory<>("dataHora"));
        colData.setCellFactory((Callback<TableColumn<LogAcesso, LocalDateTime>, TableCell<LogAcesso, LocalDateTime>>) column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : DateUtils.formatar(item));
            }
        });

        colResultado.setCellValueFactory(new PropertyValueFactory<>("resultado"));
        colResultado.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(item);
                badge.getStyleClass().add("badge");
                badge.getStyleClass().add("AUTORIZADO".equals(item) ? "badge-positivo" : "badge-negativo");
                setAlignment(Pos.CENTER_LEFT);
                setGraphic(badge);
            }
        });

        carregarResumo();
    }

    private void carregarResumo() {
        try {
            List<Usuario> usuarios = usuarioDAO.listarUsuarios();
            long ativos = usuarios.stream().filter(u -> u.getStatus() == StatusEnum.ATIVO).count();
            long bloqueados = usuarios.stream().filter(u -> u.getStatus() == StatusEnum.BLOQUEADO).count();
            lblAtivos.setText(String.valueOf(ativos));
            lblBloqueados.setText(String.valueOf(bloqueados));
        } catch (Exception e) {
            lblAtivos.setText("-");
            lblBloqueados.setText("-");
        }

        try {
            int autorizadosHoje = logAcessoDAO.contarAcessosHoje(ResultadoAcessoEnum.AUTORIZADO);
            int negadosHoje = logAcessoDAO.contarAcessosHoje(ResultadoAcessoEnum.NEGADO);
            lblAcessosHoje.setText(String.valueOf(autorizadosHoje));
            lblNegadosHoje.setText(String.valueOf(negadosHoje));
        } catch (Exception e) {
            lblAcessosHoje.setText("-");
            lblNegadosHoje.setText("-");
        }

        try {
            List<LogAcesso> ultimos = logAcessoDAO.listarUltimos(8);
            tblUltimos.setItems(FXCollections.observableArrayList(ultimos));
        } catch (Exception e) {
            tblUltimos.getItems().clear();
        }
    }

    @FXML
    private void btnCadastrarAction() {
        if (principalController != null) principalController.abrirCadastroUsuario();
    }

    @FXML
    private void btnHistoricoAction() {
        if (principalController != null) principalController.abrirHistorico();
    }

    @FXML
    private void btnRelatorioAction() {
        if (principalController != null) principalController.abrirRelatorioFrequencia();
    }

    @FXML
    private void btnAtualizarAction() {
        carregarResumo();
    }
}
