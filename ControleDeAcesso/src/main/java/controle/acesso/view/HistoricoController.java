package controle.acesso.view;

import controle.acesso.dao.LogAcessoDAO;
import controle.acesso.dao.UsuarioDAO;
import controle.acesso.model.LogAcesso;
import controle.acesso.model.ResultadoAcessoEnum;
import controle.acesso.model.TipoAcessoEnum;
import controle.acesso.model.Usuario;
import controle.acesso.view.util.DateUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class HistoricoController {

    @FXML private ComboBox<Usuario> cmbUsuarioFiltro;
    @FXML private DatePicker dtInicio;
    @FXML private DatePicker dtFim;
    @FXML private TableView<LogAcesso> tblHistorico;
    @FXML private TableColumn<LogAcesso, Integer> colId;
    @FXML private TableColumn<LogAcesso, String> colUsuario;
    @FXML private TableColumn<LogAcesso, LocalDateTime> colData;
    @FXML private TableColumn<LogAcesso, String> colTipo;
    @FXML private TableColumn<LogAcesso, String> colResultado;
    @FXML private TableColumn<LogAcesso, String> colMotivo;

    @FXML private ComboBox<Usuario> cmbUsuarioRegistro;
    @FXML private ComboBox<TipoAcessoEnum> cmbTipoRegistro;
    @FXML private ComboBox<ResultadoAcessoEnum> cmbResultadoRegistro;
    @FXML private TextField txtMotivoRegistro;

    private final LogAcessoDAO logAcessoDAO = new LogAcessoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    public void initialize() {
        configurarCelulasUsuario(cmbUsuarioFiltro);
        configurarCelulasUsuario(cmbUsuarioRegistro);

        cmbTipoRegistro.getItems().setAll(TipoAcessoEnum.values());
        cmbTipoRegistro.setValue(TipoAcessoEnum.ENTRADA);
        cmbResultadoRegistro.getItems().setAll(ResultadoAcessoEnum.values());
        cmbResultadoRegistro.setValue(ResultadoAcessoEnum.AUTORIZADO);

        colId.setCellValueFactory(new PropertyValueFactory<>("idLog"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("nomeUsuario"));
        colData.setCellValueFactory(new PropertyValueFactory<>("dataHora"));
        colData.setCellFactory((Callback<TableColumn<LogAcesso, LocalDateTime>, TableCell<LogAcesso, LocalDateTime>>) column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : DateUtils.formatar(item));
            }
        });
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoAcesso"));
        colResultado.setCellValueFactory(new PropertyValueFactory<>("resultado"));
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));

        carregarUsuarios();
        carregarHistorico();
    }

    private void configurarCelulasUsuario(ComboBox<Usuario> combo) {
        Callback<ListView<Usuario>, ListCell<Usuario>> fabrica = listView -> new ListCell<>() {
            @Override
            protected void updateItem(Usuario usuario, boolean empty) {
                super.updateItem(usuario, empty);
                setText(empty ? null : (usuario == null ? "Todos" : usuario.getNome()));
            }
        };
        combo.setCellFactory(fabrica);
        combo.setButtonCell(fabrica.call(null));
    }

    private void carregarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDAO.listarUsuarios();

            cmbUsuarioFiltro.getItems().clear();
            cmbUsuarioFiltro.getItems().add(null);
            cmbUsuarioFiltro.getItems().addAll(usuarios);
            cmbUsuarioFiltro.setValue(null);

            cmbUsuarioRegistro.getItems().setAll(usuarios);
        } catch (Exception e) {
            mostrar(Alert.AlertType.ERROR, "Não foi possível carregar os usuários:\n" + e.getMessage());
        }
    }

    @FXML
    private void btnFiltrarAction() {
        carregarHistorico();
    }

    @FXML
    private void btnLimparFiltroAction() {
        cmbUsuarioFiltro.setValue(null);
        dtInicio.setValue(null);
        dtFim.setValue(null);
        carregarHistorico();
    }

    private void carregarHistorico() {
        try {
            Usuario usuarioSelecionado = cmbUsuarioFiltro.getValue();
            Integer idUsuario = usuarioSelecionado == null ? null : usuarioSelecionado.getIdUsuario();

            LocalDateTime inicio = dtInicio.getValue() == null ? null : dtInicio.getValue().atStartOfDay();
            LocalDateTime fim = dtFim.getValue() == null ? null : dtFim.getValue().atTime(LocalTime.MAX);

            List<LogAcesso> historico = logAcessoDAO.listarHistorico(idUsuario, inicio, fim);
            tblHistorico.setItems(FXCollections.observableArrayList(historico));
        } catch (Exception e) {
            mostrar(Alert.AlertType.ERROR, "Não foi possível carregar o histórico:\n" + e.getMessage());
        }
    }

    @FXML
    private void btnRegistrarAcessoAction() {
        Usuario usuario = cmbUsuarioRegistro.getValue();
        TipoAcessoEnum tipo = cmbTipoRegistro.getValue();
        ResultadoAcessoEnum resultado = cmbResultadoRegistro.getValue();

        if (usuario == null || tipo == null || resultado == null) {
            mostrar(Alert.AlertType.WARNING, "Selecione o usuário, o tipo e o resultado do acesso.");
            return;
        }

        LogAcesso log = new LogAcesso(
                usuario.getIdUsuario(),
                tipo,
                resultado,
                txtMotivoRegistro.getText().isBlank() ? null : txtMotivoRegistro.getText().trim()
        );

        try {
            logAcessoDAO.registrarAcesso(log);
            txtMotivoRegistro.clear();
            carregarHistorico();
            mostrar(Alert.AlertType.INFORMATION, "Acesso registrado com sucesso!");
        } catch (Exception e) {
            mostrar(Alert.AlertType.ERROR, "Erro ao registrar acesso:\n" + e.getMessage());
        }
    }

    private void mostrar(Alert.AlertType tipo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Controle de Acesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
