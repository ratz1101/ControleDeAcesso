package controle.acesso.view;

import controle.acesso.dao.UsuarioDAO;
import controle.acesso.model.StatusEnum;
import controle.acesso.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CadastroUsuarioController {

    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private ComboBox<String> cmbCargo;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private ComboBox<StatusEnum> cmbStatus;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    public void initialize() {
        cmbStatus.getItems().setAll(StatusEnum.values());
        cmbStatus.setValue(StatusEnum.ATIVO);

        cmbCargo.getItems().addAll(
                "Professor",
                "Funcionário"
        );
    }

    @FXML
    private void btnCadastrarAction() {

        if (txtNome.getText().isBlank()
                || txtCpf.getText().isBlank()
                || cmbCargo.getValue() == null
                || txtEmail.getText().isBlank()
                || txtSenha.getText().isBlank()
                || cmbStatus.getValue() == null) {

            mostrar(Alert.AlertType.WARNING,
                    "Preencha todos os campos obrigatórios.");
            return;
        }

        if (!txtEmail.getText().contains("@")) {
            mostrar(Alert.AlertType.WARNING,
                    "Informe um e-mail válido.");
            return;
        }

        Usuario usuario = new Usuario(
                txtNome.getText().trim(),
                txtCpf.getText().trim(),
                cmbCargo.getValue(),
                txtEmail.getText().trim(),
                txtSenha.getText(),
                cmbStatus.getValue()
        );

        try {
            usuarioDAO.cadastrarUsuario(usuario);

            mostrar(
                    Alert.AlertType.INFORMATION,
                    "Usuário cadastrado com sucesso!\nID gerado: "
                    + usuario.getIdUsuario()
            );

            limparCampos();

        } catch (Exception e) {
            mostrar(
                    Alert.AlertType.ERROR,
                    "Erro ao cadastrar usuário:\n"
                    + e.getMessage()
            );
        }
    }

    @FXML
    private void btnLimparAction() {
        limparCampos();
    }

    private void limparCampos() {
        txtNome.clear();
        txtCpf.clear();
        cmbCargo.setValue(null);
        txtEmail.clear();
        txtSenha.clear();
        cmbStatus.setValue(StatusEnum.ATIVO);
        txtNome.requestFocus();
    }

    private void mostrar(Alert.AlertType tipo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Controle de Acesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}

