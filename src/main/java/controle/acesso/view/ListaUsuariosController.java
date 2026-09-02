package controle.acesso.view;

import controle.acesso.dao.UsuarioDAO;
import controle.acesso.model.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.scene.control.TableCell;

import java.time.LocalDateTime;
import java.util.List;
import controle.acesso.view.util.DateUtils;

public class ListaUsuariosController {
    @FXML private TableView<Usuario> tblUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colNome;
    @FXML private TableColumn<Usuario, String> colCpf;
    @FXML private TableColumn<Usuario, String> colCargo;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colStatus;
    @FXML private TableColumn<Usuario, LocalDateTime> colData;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colData.setCellValueFactory(new PropertyValueFactory<>("dataCadastro"));
        colData.setCellFactory((Callback<TableColumn<Usuario, LocalDateTime>, TableCell<Usuario, LocalDateTime>>) column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : DateUtils.formatar(item));
            }
        });
        carregarUsuarios(false);
    }

    @FXML
    private void btnListarAction() {
        carregarUsuarios(true);
    }

    private void carregarUsuarios(boolean mostrarMensagem) {
        try {
            List<Usuario> usuarios = usuarioDAO.listarUsuarios();
            tblUsuarios.setItems(FXCollections.observableArrayList(usuarios));
            if (mostrarMensagem) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Listagem");
                alert.setHeaderText(null);
                alert.setContentText(usuarios.size() + " usuário(s) encontrado(s).");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de banco de dados");
            alert.setHeaderText("Não foi possível listar os usuários.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void btnLimparAction() {
        tblUsuarios.getItems().clear();
    }
}
