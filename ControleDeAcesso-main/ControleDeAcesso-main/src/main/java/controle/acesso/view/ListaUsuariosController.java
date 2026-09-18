package controle.acesso.view;

import controle.acesso.Main;
import controle.acesso.dao.UsuarioDAO;
import controle.acesso.model.StatusEnum;
import controle.acesso.model.Usuario;
import controle.acesso.view.util.DateUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import java.time.LocalDateTime;
import java.util.List;
import javafx.scene.Parent;

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
        
    @FXML
    private void btnDeletarAction() {
        Usuario usuarioSelecionado = tblUsuarios.getSelectionModel().getSelectedItem();

        if (usuarioSelecionado == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Aviso", "Nenhum usuário selecionado", 
                         "Por favor, selecione um usuário na tabela para deletar.");
            return;
        }

        try {
            usuarioDAO.deletarUsuario(usuarioSelecionado.getIdUsuario());
            tblUsuarios.getItems().remove(usuarioSelecionado);
            exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", null, "Usuário deletado com sucesso!");
        } catch (Exception e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível deletar o usuário", e.getMessage());
        }
    }

    @FXML
    private void btnAtualizarAction() {
        Usuario usuarioSelecionado = tblUsuarios.getSelectionModel().getSelectedItem();

        if (usuarioSelecionado == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Aviso", "Nenhum usuário selecionado", 
                         "Por favor, selecione um usuário na tabela para editar.");
            return;
        }

        try {
            // Carrega a tela de cadastro e obtém o controlador dela
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/controle/acesso/view/cadastroUsuario.fxml"));
            Node telaCadastro = loader.load();
            
            CadastroUsuarioController cadastroController = loader.getController();
            // Preenche os campos da tela de cadastro com o usuário que selecionamos na tabela
            cadastroController.preencherCampos(usuarioSelecionado);
            
            // Encontra o painel principal (BorderPane) para colocar a tela de edição bem no centro
            BorderPane painelPrincipal = (BorderPane) tblUsuarios.getScene().lookup("#painelPrincipal");
            if (painelPrincipal != null) {
                painelPrincipal.setCenter(telaCadastro);
            } else {
                // Caso não encontre por ID, abre em uma nova janela de segurança
                javafx.stage.Stage stage = new javafx.stage.Stage();
                stage.setTitle("Editar Usuário");
                stage.setScene(new javafx.scene.Scene((Parent) telaCadastro));
                stage.showAndWait();
                tblUsuarios.refresh();
            }
        } catch (Exception e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela de edição", e.getMessage());
            e.printStackTrace();
        }
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String cabecalho, String conteudo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }
}
