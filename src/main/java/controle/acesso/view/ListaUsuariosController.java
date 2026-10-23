package controle.acesso.view;

import controle.acesso.Main;
import controle.acesso.dao.LogAcessoDAO;
import controle.acesso.dao.UsuarioDAO;
import controle.acesso.model.StatusEnum;
import controle.acesso.model.Usuario;
import controle.acesso.view.util.DateUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ListaUsuariosController {
    @FXML private TableView<Usuario> tblUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colNome;
    @FXML private TableColumn<Usuario, String> colCpf;
    @FXML private TableColumn<Usuario, String> colCargo;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, StatusEnum> colStatus;
    @FXML private TableColumn<Usuario, LocalDateTime> colData;
    @FXML private TableColumn<Usuario, Void> colAcoes;
    @FXML private TextField txtBusca;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final LogAcessoDAO logAcessoDAO = new LogAcessoDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(StatusEnum item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(item.name());
                badge.getStyleClass().add("badge");
                badge.getStyleClass().add(switch (item) {
                    case ATIVO -> "badge-positivo";
                    case BLOQUEADO -> "badge-negativo";
                    case INATIVO -> "badge-neutro";
                });
                setAlignment(Pos.CENTER_LEFT);
                setGraphic(badge);
            }
        });

        colData.setCellValueFactory(new PropertyValueFactory<>("dataCadastro"));
        colData.setCellFactory((Callback<TableColumn<Usuario, LocalDateTime>, TableCell<Usuario, LocalDateTime>>) column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : DateUtils.formatar(item));
            }
        });

        colAcoes.setCellFactory(coluna -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnExcluir = new Button("Excluir");
            private final HBox caixa = new HBox(6.0, btnEditar, btnExcluir);

            {
                btnEditar.getStyleClass().addAll("button-pequeno");
                btnExcluir.getStyleClass().addAll("button-pequeno", "button-perigo");
                btnEditar.setOnAction(e -> editarUsuario(getTableView().getItems().get(getIndex())));
                btnExcluir.setOnAction(e -> excluirUsuario(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : caixa);
            }
        });

        carregarUsuarios(false);
    }

    @FXML
    private void btnListarAction() {
        carregarUsuarios(true);
    }

    @FXML
    private void btnLimparBuscaAction() {
        txtBusca.clear();
        carregarUsuarios(false);
    }

    @FXML
    private void btnBuscarAction() {
        String termo = txtBusca.getText() == null ? "" : txtBusca.getText().trim().toLowerCase();
        if (termo.isBlank()) {
            carregarUsuarios(false);
            return;
        }
        try {
            List<Usuario> usuarios = usuarioDAO.listarUsuarios().stream()
                    .filter(u -> u.getNome().toLowerCase().contains(termo)
                            || u.getCpf().toLowerCase().contains(termo)
                            || u.getEmail().toLowerCase().contains(termo))
                    .toList();
            tblUsuarios.setItems(FXCollections.observableArrayList(usuarios));
        } catch (Exception e) {
            mostrarErroBanco(e);
        }
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
            mostrarErroBanco(e);
        }
    }

    private void editarUsuario(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/controle/acesso/view/editarUsuario.fxml"));
            Parent raiz = loader.load();

            EditarUsuarioController controller = loader.getController();
            controller.setUsuario(usuario);

            Stage modal = new Stage();
            modal.setTitle("Editar Usuário");
            modal.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(raiz);
            scene.getStylesheets().add(Main.class.getResource("/controle/acesso/css/estilo.css").toExternalForm());
            modal.setScene(scene);
            modal.setResizable(false);
            modal.showAndWait();

            if (controller.isSalvo()) {
                carregarUsuarios(false);
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Não foi possível abrir a edição.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void excluirUsuario(Usuario usuario) {
        int registros;
        try {
            registros = logAcessoDAO.contarPorUsuario(usuario.getIdUsuario());
        } catch (Exception e) {
            mostrarErroBanco(e);
            return;
        }

        if (registros == 0) {
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Excluir usuário");
            confirmacao.setHeaderText("Excluir " + usuario.getNome() + "?");
            confirmacao.setContentText("Esse usuário não possui registros de acesso. Essa ação não pode ser desfeita.");
            Optional<ButtonType> resposta = confirmacao.showAndWait();
            if (resposta.isPresent() && resposta.get() == ButtonType.OK) {
                executarExclusao(usuario);
            }
            return;
        }

        ButtonType btnInativar = new ButtonType("Inativar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnExcluirTudo = new ButtonType("Excluir tudo", ButtonBar.ButtonData.OTHER);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert opcoes = new Alert(Alert.AlertType.CONFIRMATION, null, btnInativar, btnExcluirTudo, btnCancelar);
        opcoes.setTitle("Usuário com registros");
        opcoes.setHeaderText(usuario.getNome() + " possui " + registros + " registro(s) de acesso.");
        opcoes.setContentText("Recomendado: inativar o usuário, mantendo o histórico.\n"
                + "Excluir tudo remove o usuário e todos os registros dele, sem volta.");

        Optional<ButtonType> escolha = opcoes.showAndWait();
        if (escolha.isEmpty() || escolha.get() == btnCancelar) {
            return;
        }

        if (escolha.get() == btnInativar) {
            try {
                usuarioDAO.inativarUsuario(usuario.getIdUsuario());
                carregarUsuarios(false);
            } catch (Exception e) {
                mostrarErroBanco(e);
            }
            return;
        }

        Alert certeza = new Alert(Alert.AlertType.WARNING, null, ButtonType.YES, ButtonType.NO);
        certeza.setTitle("Excluir definitivamente");
        certeza.setHeaderText("Apagar " + usuario.getNome() + " e " + registros + " registro(s)?");
        certeza.setContentText("Essa ação não pode ser desfeita.");
        Optional<ButtonType> confirmou = certeza.showAndWait();
        if (confirmou.isPresent() && confirmou.get() == ButtonType.YES) {
            executarExclusao(usuario);
        }
    }

    private void executarExclusao(Usuario usuario) {
        try {
            usuarioDAO.excluirUsuario(usuario.getIdUsuario());
            carregarUsuarios(false);
        } catch (Exception e) {
            mostrarErroBanco(e);
        }
    }

    private void mostrarErroBanco(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro de banco de dados");
        alert.setHeaderText("Não foi possível completar a operação.");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    @FXML
    private void btnLimparAction() {
        tblUsuarios.getItems().clear();
    }
}
